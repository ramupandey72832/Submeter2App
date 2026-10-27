package com.submeter.presentation.viewmodel;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.service.BillService;
import com.submeter.service.MeterReadingService;
import com.submeter.service.impl.core.BillServiceImpl;
import com.submeter.service.impl.core.MeterReadingServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * This class will be in bill fragment to show the bills and also work with edit bills
 */
public class BillViewModel extends BaseViewModel{

    private final BillService billService;
    private final MeterReadingService meterReadingService;

    private List<BillDTO> fullBillList = new ArrayList<>();
    private List<BillDTO> filteredBillList = new ArrayList<>();

    public BillViewModel(BillService billService, MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
        this.billService = billService;
    }

    public List<BillDTO> getFilteredBillList() { return filteredBillList; }

    // --- UI Actions ---

    public void fetchBills() {
        setLoading(true);
        setErrorMessage(null);
        setOperationSuccess(false);
        notifyStateChanged();

        new Thread(() -> {
            try {
                List<BillDTO> bills = billService.getAll();

                if (bills != null) {
                    this.fullBillList = new ArrayList<>(bills);
                    // Sort descending by Bill ID natively inside the Business Tier
                    Collections.sort(this.fullBillList, Comparator.comparing(BillDTO::getId).reversed());
                } else {
                    this.fullBillList = new ArrayList<>();
                }

                // Default initial filtered list state equals full data cache
                this.filteredBillList = new ArrayList<>(this.fullBillList);
                setOperationSuccess(true);

            } catch (Exception e) {
                String msg = "Database Error while compilation of financial reports: " + e.getMessage();
                setErrorMessage(msg);
            } finally {
                setLoading(false);
                notifyStateChanged();
            }
        }).start();
    }

    public void filterBills(String text) {
        if (text == null || text.trim().isEmpty()) {
            this.filteredBillList = new ArrayList<>(this.fullBillList);
            notifyStateChanged();
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

        this.filteredBillList = tempFilteredList;
        notifyStateChanged();
    }

    public void updateBill(int billId, long currentReading, long previousReading, double rate, double fixed,double extra, double total, String note) {

            // update meterReading , update bill ( Never include payment in here )
            // since this is bill Only , we will do UPDATE BILL and meter reading will be in meterReadingSErvice

            setLoading(true);
            setErrorMessage(null);
            notifyStateChanged();

            new Thread(() -> {
                try {
                    BillDTO billDTO = new BillDTO();
                    billService.updateById(billId,billDTO);
                    meterReadingService.update(billDTO.getCurrentReadingId(),billDTO.getCurrentReading(),"" );

                    // Never Include Payment here , we have to use payment id to edit them
                } catch (Exception e) {
                    setErrorMessage(
                            "Failed to load dashboard metrics: " + e.getMessage()
                    );

                } finally {
                    setLoading(false);
                    notifyStateChanged();
                }
            }).start();
    }
}