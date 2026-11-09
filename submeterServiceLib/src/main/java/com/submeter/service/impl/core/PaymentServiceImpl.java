package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.PaymentDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.PaymentDao;
import com.submeter.data.entities.Payment;
import com.submeter.service.PaymentService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentDao paymentDao;

    public PaymentServiceImpl(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    @Override
    public OperationResult<Void> addPayment(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            return OperationResult.failure("Payment details cannot be null.");
        }
        if (paymentDTO.getBillId() <= 0) {
            return OperationResult.failure("A valid bill ID is required to make a payment.");
        }
        if (paymentDTO.getAmountPaid() <= 0) {
            return OperationResult.failure("Payment amount must be greater than zero.");
        }

        try {
            Payment payment = mapDTOToEntity(paymentDTO);
            boolean isInserted = paymentDao.insert(payment);

            if (isInserted) {
                return OperationResult.success(null, "Payment added successfully.");
            } else {
                return OperationResult.failure("Failed to record payment in the database.");
            }

        } catch (SQLException e) {
            return OperationResult.failure("Database error while processing payment: " + e.getMessage());
        } catch (Exception e) {
            return OperationResult.failure("Unexpected error: " + e.getMessage());
        }
    }

    public OperationResult<Void> updateById(int paymentId, long amountPaid, String paymentDate, String note) {
        if (paymentId <= 0) {
            return OperationResult.failure("Invalid payment ID.");
        }
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);
            payment.setAmountPaid(amountPaid);
            payment.setPaymentDate(paymentDate);
            payment.setNote(note);

            boolean isUpdated = paymentDao.update(payment);
            if (isUpdated) {
                return OperationResult.success(null, "Payment updated successfully.");
            } else {
                return OperationResult.failure("Failed to update payment.");
            }
        } catch (SQLException e) {
            return OperationResult.failure("Database error while updating payment: " + e.getMessage());
        } catch (Exception e) {
            return OperationResult.failure("Unexpected error: " + e.getMessage());
        }
    }

    public OperationResult<PaymentDTO> getById(int paymentId) {
        if (paymentId <= 0) {
            return OperationResult.failure("Invalid payment ID.");
        }
        try {
            Payment payment = paymentDao.getById(paymentId);
            if (payment != null) {
                return OperationResult.success(mapEntityToDTO(payment), "Payment retrieved successfully.");
            } else {
                return OperationResult.failure("Payment not found.");
            }
        } catch (SQLException e) {
            return OperationResult.failure("Database error: " + e.getMessage());
        } catch (Exception e) {
            return OperationResult.failure("Unexpected error: " + e.getMessage());
        }
    }

    public OperationResult<List<PaymentDTO>> getAll() {
        try {
            List<Payment> payments = paymentDao.getAll();
            List<PaymentDTO> dtos = new ArrayList<>();
            if (payments != null) {
                for (Payment p : payments) {
                    dtos.add(mapEntityToDTO(p));
                }
            }
            return OperationResult.success(dtos, "Payments retrieved successfully.");
        } catch (SQLException e) {
            return OperationResult.failure("Database error: " + e.getMessage());
        } catch (Exception e) {
            return OperationResult.failure("Unexpected error: " + e.getMessage());
        }
    }

    // --- Mappers ---

    private Payment mapDTOToEntity(PaymentDTO dto) {
        if (dto == null) return null;
        Payment entity = new Payment();
        entity.setId(dto.getId());
        entity.setBillId(dto.getBillId());
        entity.setAmountPaid(dto.getAmountPaid());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setNote(dto.getNote());
        return entity;
    }

    private PaymentDTO mapEntityToDTO(Payment entity) {
        if (entity == null) return null;
        PaymentDTO dto = new PaymentDTO();
        dto.setId(entity.getId());
        dto.setBillId(entity.getBillId());
        dto.setAmountPaid(entity.getAmountPaid());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setNote(entity.getNote());
        return dto;
    }
}