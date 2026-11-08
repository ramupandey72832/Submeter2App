package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.PaymentDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {

    public void add(PaymentDTO paymentDTO){
//        int billId,long amountPaid,String paymentDate,String note
    }

    @Override
    public OperationResult<Void> addPayment(PaymentDTO paymentDTO) {
        return null;
    }
//    void updateById(int paymentId,long amountPaid,String paymentDate,String note){}
//    void getById(int paymentId){}
//    void getAll(){}
}
