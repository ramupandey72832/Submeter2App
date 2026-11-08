package com.submeter.service;

import com.submeter.commonlib.dto.PaymentDTO;
import com.submeter.commonlib.response.OperationResult;

public interface PaymentService {
    public OperationResult<Void> addPayment(PaymentDTO paymentDTO);
}
