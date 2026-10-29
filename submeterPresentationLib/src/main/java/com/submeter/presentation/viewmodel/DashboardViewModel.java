package com.submeter.presentation.viewmodel;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.dto.PaymentDTO;
import com.submeter.service.BillService;
import com.submeter.service.PaymentService;
import com.submeter.service.ServiceManager;
import com.submeter.service.impl.core.BillServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * IT WILL BE USED ON DASHBOARD FRAGMENT , IT WILL SHOW PENDING BILLS ,
 * AND STATS LIKE TOTAL UNITS , TOTAL BILL ISSUED , SUM OF ALL BILL
 * since Dashboard also have a paybtn to pay bill , we have to show a popup box to pay bill
 */
public class DashboardViewModel extends BaseViewModel {

    private final BillService billService;
    private final PaymentService paymentService;

    private double totalUnits;
    private int totalBillsCount;
    private double totalBilledAmount;

    private List<BillDTO> pendingBillsList = new ArrayList<>();

    public DashboardViewModel(BillService billService,PaymentService paymentService) {
        this.billService = billService;
        this.paymentService = paymentService;
    }

    public double getTotalUnits() {
        return totalUnits;
    }
    public int getTotalBillsCount() {
        return totalBillsCount;
    }
    public double getTotalBilledAmount() {
        return totalBilledAmount;
    }
    public List<BillDTO> getPendingBills() {
        return new ArrayList<>(pendingBillsList);
    }

    public void loadDashboardSummary() {

        setLoading(true);
        setErrorMessage(null);
        notifyStateChanged();

        new Thread(() -> {
            try {
                List<BillDTO> allBills = billService.getAll().getData();

                double units = 0.0;
                double billedAmount = 0.0;
                int billCount = 0;

                if (allBills != null) {
                    for (BillDTO bill : allBills) {
                        units += bill.getCurrentReading()
                                - bill.getPreviousReading();

                        billedAmount += bill.getTotalAmount();
                        billCount++;
                    }
                }

                List<BillDTO> pendingBills = billService.getAllPending().getData();

                totalUnits = units;
                totalBilledAmount = billedAmount;
                totalBillsCount = billCount;

                pendingBillsList = pendingBills == null
                        ? new ArrayList<>()
                        : new ArrayList<>(pendingBills);

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

    public void payBill(int billId,double amount,String paymentDate,String note){
        PaymentDTO paymentDTO =  PaymentDTO.builder().id(0).billId(billId).amountPaid(amount).
                paymentDate(paymentDate).note(note).build();
        paymentService.add(paymentDTO);
    }
}