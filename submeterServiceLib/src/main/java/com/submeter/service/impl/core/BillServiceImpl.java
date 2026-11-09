package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.BillDao;
import com.submeter.data.dao.DaoManager;
import com.submeter.data.entities.Bill;
import com.submeter.service.BillService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BillServiceImpl implements BillService {

    private final BillDao billDao;

    public BillServiceImpl(BillDao billDao) {
        this.billDao = billDao != null ? billDao : DaoManager.getBillDao();
    }

    @Override
    public OperationResult<List<BillDTO>> getAll() {
        try {
            List<Bill> bills = billDao.getAll();
            List<BillDTO> dtos = bills.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            return OperationResult.success(dtos, "Bills retrieved successfully.");
        } catch (SQLException e) {
            return OperationResult.failure("Failed to retrieve bills: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<BillDTO>> getAllPending() {
        try {
            // Fetch only bills with an outstanding balance directly from the DAO
            List<Bill> pendingBills = billDao.getPendingBills();

            if (pendingBills == null || pendingBills.isEmpty()) {
                return OperationResult.success(new ArrayList<>(), "No pending bills found.");
            }

            List<BillDTO> billDTOs = new ArrayList<>();
            for (Bill bill : pendingBills) {
                BillDTO dto = mapToDTO(bill); // Use your existing entity-to-DTO mapper
                // Ensure your BillDTO also carries the balance value to display in the UI
                dto.setBalance(bill.getBalance());
                billDTOs.add(dto);
            }

            return OperationResult.success(billDTOs, "Pending bills retrieved successfully.");
        } catch (SQLException e) {
            return OperationResult.failure("Database error while fetching pending bills: " + e.getMessage());
        } catch (Exception e) {
            return OperationResult.failure("Failed to retrieve pending bills: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> update(BillDTO billDTO) {
        if (billDTO == null) {
            return OperationResult.failure("BillDTO cannot be null.");
        }
        try {
            Bill bill = mapToEntity(billDTO);
            boolean updated = billDao.update(bill);
            if (updated) {
                return OperationResult.success(null, "Bill updated successfully.");
            } else {
                return OperationResult.failure("Bill not found or could not be updated.");
            }
        } catch (SQLException e) {
            return OperationResult.failure("Failed to update bill: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> add(BillDTO billDTO) {
        if (billDTO == null) {
            return OperationResult.failure("BillDTO cannot be null.");
        }
        try {
            billDTO.setBalance(billDTO.getTotalAmount());
            Bill bill = mapToEntity(billDTO);
            boolean isAdded = billDao.insert(bill);
            if (isAdded) {
                return OperationResult.success(null, "Bill added successfully.");
            } else {
                return OperationResult.failure("Failed to insert bill into database.");
            }
        } catch (SQLException e) {
            return OperationResult.failure("Failed to add bill: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<BillDTO> getById(int billId) {
        try {
            Optional<Bill> billOpt = billDao.getById(billId);
            if (billOpt.isPresent()) {
                return OperationResult.success(mapToDTO(billOpt.get()), "Bill found.");
            } else {
                return OperationResult.failure("Bill with ID " + billId + " not found.");
            }
        } catch (SQLException e) {
            return OperationResult.failure("Failed to retrieve bill by ID: " + e.getMessage());
        }
    }

    // --- Mappers between Entity and DTO ---

    private BillDTO mapToDTO(Bill bill) {
        if (bill == null) return null;
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setMeterId(bill.getMeterId());
        dto.setRoomNumber(bill.getRoomNumber());
        dto.setTenantId(bill.getTenantId());
        dto.setTenantName(bill.getTenantName());
        dto.setMeterSerialNumber(bill.getMeterSerialNumber());
        dto.setPreviousReadingId(bill.getPreviousReadingId());
        dto.setCurrentReadingId(bill.getCurrentReadingId());
        dto.setCurrentReading(bill.getCurrentReading());
        dto.setPreviousReading(bill.getPreviousReading());
        dto.setUnitsConsumed(bill.getUnitsConsumed());
        dto.setRatePerUnit(bill.getRatePerUnit());
        dto.setFixedCharge(bill.getFixedCharge());
        dto.setExtraCharge(bill.getExtraCharge());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setNote(bill.getNote());
        dto.setBillingDate(bill.getBillingDate());
        dto.setBalance(bill.getBalance());
        return dto;
    }

    private Bill mapToEntity(BillDTO dto) {
        if (dto == null) return null;
        Bill bill = new Bill();
        bill.setId(dto.getId());
        bill.setMeterId(dto.getMeterId());
        bill.setRoomNumber(dto.getRoomNumber());
        bill.setTenantId(dto.getTenantId());
        bill.setTenantName(dto.getTenantName());
        bill.setMeterSerialNumber(dto.getMeterSerialNumber());
        bill.setPreviousReadingId(dto.getPreviousReadingId());
        bill.setCurrentReadingId(dto.getCurrentReadingId());
        bill.setCurrentReading(dto.getCurrentReading());
        bill.setPreviousReading(dto.getPreviousReading());
        bill.setUnitsConsumed(dto.getUnitsConsumed());
        bill.setRatePerUnit(dto.getRatePerUnit());
        bill.setFixedCharge(dto.getFixedCharge());
        bill.setExtraCharge(dto.getExtraCharge());
        bill.setTotalAmount(dto.getTotalAmount());
        bill.setNote(dto.getNote());
        bill.setBillingDate(dto.getBillingDate());
        bill.setBalance(dto.getBalance());
        return bill;
    }
}