package com.submeter.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rrallutilitylib.util.DateUtils;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.dto.MeterReadingDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.BillService;
import com.submeter.service.MeterReadingService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * ViewModel for BillFragment managing bills, filtering, updates, and reactive UI states using ViewModel directly.
 */
public class BillViewModel extends ViewModel {

    private final BillService billService;
    private final MeterReadingService meterReadingService;

    private List<BillDTO> fullBillList = new ArrayList<>();

    // Reactive UI LiveData states
    private final MutableLiveData<List<BillDTO>> filteredBillListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> operationSuccessLiveData = new MutableLiveData<>(false);

    public BillViewModel(BillService billService, MeterReadingService meterReadingService) {
        this.billService = billService;
        this.meterReadingService = meterReadingService;
    }

    // --- Getters for LiveData ---

    public LiveData<List<BillDTO>> getFilteredBillListLiveData() {
        return filteredBillListLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    public LiveData<Boolean> getOperationSuccessLiveData() {
        return operationSuccessLiveData;
    }

    private void showToast(String message) {
        toastMessageLiveData.postValue(message);
    }

    // --- UI Actions ---

    public void getBills() {
        isLoadingLiveData.postValue(true);
        errorMessageLiveData.postValue(null);
        operationSuccessLiveData.postValue(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                OperationResult<List<BillDTO>> result = billService.getAll();
                List<BillDTO> bills = result != null ? result.getData() : null;

                if (bills != null) {
                    this.fullBillList = new ArrayList<>(bills);
                    // Sort descending by Bill ID natively
                    Collections.sort(this.fullBillList, Comparator.comparing(BillDTO::getId).reversed());
                } else {
                    this.fullBillList = new ArrayList<>();
                }

                filteredBillListLiveData.postValue(new ArrayList<>(this.fullBillList));
                operationSuccessLiveData.postValue(true);

            } catch (Exception e) {
                String msg = "Database Error while fetching bills: " + e.getMessage();
                errorMessageLiveData.postValue(msg);
                showToast(msg);
            } finally {
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void getFilteredBills(String text) {
        if (text == null || text.trim().isEmpty()) {
            filteredBillListLiveData.setValue(new ArrayList<>(this.fullBillList));
            return;
        }

        List<BillDTO> tempFilteredList = new ArrayList<>();
        String cleanQuery = text.toLowerCase().trim();

        for (BillDTO item : this.fullBillList) {
            if ((item.getTenantName() != null && item.getTenantName().toLowerCase().contains(cleanQuery)) ||
                    (item.getRoomNumber() != null && item.getRoomNumber().contains(cleanQuery))) {
                tempFilteredList.add(item);
            }
        }

        filteredBillListLiveData.setValue(tempFilteredList);
    }

    public void updateBill(int billId, long currentReading, long previousReading, double rate, double fixed, double extra, double total, String note) {
        isLoadingLiveData.postValue(true);
        errorMessageLiveData.postValue(null);
        operationSuccessLiveData.postValue(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                OperationResult<BillDTO> existingBillResult = billService.getById(billId);
                if (existingBillResult == null || !existingBillResult.isSuccess() || existingBillResult.getData() == null) {
                    String err = "Failed to fetch bill for updating.";
                    errorMessageLiveData.postValue(err);
                    showToast(err);
                    return;
                }

                BillDTO billDTO = existingBillResult.getData();
                billDTO.setCurrentReading(currentReading);
                billDTO.setPreviousReading(previousReading);
                billDTO.setRatePerUnit(rate);
                billDTO.setFixedCharge(fixed);
                billDTO.setExtraCharge(extra);
                billDTO.setTotalAmount(total);
                billDTO.setNote(note);

                long units = currentReading - previousReading;
                billDTO.setUnitsConsumed(Math.max(0, units));

                OperationResult<Void> updateResult = billService.update(billDTO);
                if (updateResult == null || !updateResult.isSuccess()) {
                    String err = "Failed to update bill: " + (updateResult != null ? updateResult.getMessage() : "Unknown error");
                    errorMessageLiveData.postValue(err);
                    showToast(err);
                    return;
                }

                if (meterReadingService != null && billDTO.getCurrentReadingId() > 0) {
                    MeterReadingDTO meterReadingDTO = new MeterReadingDTO();
                    meterReadingDTO.setId(billDTO.getCurrentReadingId());
                    meterReadingDTO.setMeterId(billDTO.getMeterId());
                    meterReadingDTO.setReadingValue(currentReading);
                    meterReadingDTO.setDate(DateUtils.toDatabaseFormat(billDTO.getBillingDate()));

                    meterReadingService.update(meterReadingDTO);
                }

                operationSuccessLiveData.postValue(true);
                showToast("Bill updated successfully!");
                getBills();

            } catch (Exception e) {
                String err = "Failed to update bill metrics: " + e.getMessage();
                errorMessageLiveData.postValue(err);
                showToast(err);
            } finally {
                isLoadingLiveData.postValue(false);
            }
        });
    }

    public void addMeterReadingAndGenerateBill(String roomNumber, String tenantName, int tenantId,
                                               int meterId, String meterSerialNumber,
                                               long readingValue, int previousReadingId, long previousReadingValue,
                                               double rate, double fixed, double extra, String billingDate, String note) {
        isLoadingLiveData.postValue(true);
        errorMessageLiveData.postValue(null);
        operationSuccessLiveData.postValue(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
//                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                MeterReadingDTO readingDTO = new MeterReadingDTO();
                readingDTO.setMeterId(meterId);
                readingDTO.setReadingValue(readingValue);
                readingDTO.setDate(billingDate);

                OperationResult<Integer> readingResult = meterReadingService.add(readingDTO);
                if (readingResult == null || !readingResult.isSuccess()) {
                    String err = "Failed to add meter reading: " + (readingResult != null ? readingResult.getMessage() : "Unknown error");
                    errorMessageLiveData.postValue(err);
                    showToast(err);
                    return;
                }

                long newReadingId = readingResult.getData();

                BillDTO newBill = new BillDTO();
                newBill.setTenantId(tenantId);
                newBill.setTenantName(tenantName);
                newBill.setMeterSerialNumber(meterSerialNumber);
                newBill.setMeterId(meterId);
                newBill.setRoomNumber(roomNumber);
                newBill.setCurrentReading(readingValue);
                newBill.setCurrentReadingId((int) newReadingId);
                newBill.setPreviousReading(previousReadingValue);
                newBill.setPreviousReadingId(previousReadingId);
                newBill.setRatePerUnit(rate);
                newBill.setFixedCharge(fixed);
                newBill.setExtraCharge(extra);
                newBill.setBillingDate(billingDate);
                newBill.setNote(note);
                newBill.setTotalAmount(newBill.getTotalAmount());
                newBill.setCurrentBillDue(newBill.getTotalAmount());

                System.out.printf("BillDTO_Debug "+ newBill.toString());


                OperationResult<Void> billResult = billService.add(newBill);
                if (billResult != null && billResult.isSuccess()) {
                    operationSuccessLiveData.postValue(true);
                    showToast("Bill generated successfully!");
                    getBills();
                } else {
                    String err = "Failed to generate bill: " + (billResult != null ? billResult.getMessage() : "Unknown error");
                    errorMessageLiveData.postValue(err);
                    showToast(err);
                }

            } catch (Exception e) {
                String err = "Error during meter reading and bill generation: " + e.getMessage();
                errorMessageLiveData.postValue(err);
                showToast(err);
            } finally {
                isLoadingLiveData.postValue(false);
            }
        });
    }
}