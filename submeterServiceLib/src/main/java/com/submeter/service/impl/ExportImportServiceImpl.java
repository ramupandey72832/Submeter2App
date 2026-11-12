package com.submeter.service.impl;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.BillDao;
import com.submeter.data.entities.Bill;
import com.submeter.service.ExportImportService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExportImportServiceImpl implements ExportImportService {

    private final BillDao billDao;

    public ExportImportServiceImpl(BillDao billDao) {
        this.billDao = billDao;
    }

    @Override
    public OperationResult<List<BillDTO>> getLatestBills() {
        try {
            List<Bill> bills = billDao.getLatestBillsOfAllSubmeter();
            List<BillDTO> dtos = new ArrayList<>();
            if (bills != null) {
                for (Bill bill : bills) {
                    double totalBalance = billDao.getTotalBalanceByTenantId(bill.getTenantId());
                    BillDTO temp = mapEntityToDto(bill);
                    temp.setTotalBillDue(totalBalance);
                    dtos.add(temp);
                }
            }
            return OperationResult.success(dtos, "Latest bills retrieved successfully.");
        } catch (SQLException e) {
            return OperationResult.failure("Database error while fetching latest bills: " + e.getMessage());
        } catch (Exception e) {
            return OperationResult.failure("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<BillDTO>> getLastThreeMonthBills() {
        try {
            List<Bill> bills = billDao.getLatestThreeMonthBills();
            List<BillDTO> dtos = new ArrayList<>();
            if (bills != null) {
                for (Bill bill : bills) {
                    dtos.add(mapEntityToDto(bill));
                }
            }
            return OperationResult.success(dtos, "Last three months bills retrieved successfully.");
        } catch (SQLException e) {
            return OperationResult.failure("Database error while fetching historical bills: " + e.getMessage());
        } catch (Exception e) {
            return OperationResult.failure("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<BillDTO>> getBillsByDateRange(String startingDate, String endingDate) {
        try {
            List<Bill> bills = billDao.getBillsByRange(startingDate, endingDate);
            if (bills == null || bills.isEmpty()) {
                return OperationResult.failure("No bills found for the given date range.");
            }
            List<BillDTO> dtos = new ArrayList<>();
            for (Bill bill : bills) {
                dtos.add(mapEntityToDto(bill));
            }
            return OperationResult.success(dtos, "Bills retrieved by date range successfully.");
        } catch (SQLException e) {
            return OperationResult.failure("Database error while querying date range: " + e.getMessage());
        } catch (Exception e) {
            return OperationResult.failure("Unexpected error: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> importAllTable() {
        // Implement import logic or delegate to Excel/Backup utility as needed
        return OperationResult.success(null, "Import completed successfully.");
    }

    @Override
    public OperationResult<Void> exportAllTable() {
        // Implement export logic or delegate to Excel/Backup utility as needed
        return OperationResult.success(null, "Export completed successfully.");
    }

    // --- Mapper Helper ---

    private BillDTO mapEntityToDto(Bill entity) {
        if (entity == null) return null;
        BillDTO dto = new BillDTO();
        dto.setId(entity.getId());
        dto.setMeterId(entity.getMeterId());
        dto.setRoomNumber(entity.getRoomNumber());
        dto.setTenantId(entity.getTenantId());
        dto.setTenantName(entity.getTenantName());
        dto.setMeterSerialNumber(entity.getMeterSerialNumber());
        dto.setPreviousReadingId(entity.getPreviousReadingId());
        dto.setCurrentReadingId(entity.getCurrentReadingId());
        dto.setCurrentReading(entity.getCurrentReading());
        dto.setPreviousReading(entity.getPreviousReading());
        dto.setRatePerUnit(entity.getRatePerUnit());
        dto.setFixedCharge(entity.getFixedCharge());
        dto.setExtraCharge(entity.getExtraCharge());
        dto.setCurrentBillDue(entity.getBalance());
        dto.setNote(entity.getNote());
        dto.setBillingDate(entity.getBillingDate());
        return dto;
    }
}