package com.submeter.service;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.OperationResult;

import java.util.List;

public interface BillService {
    public OperationResult<List<BillDTO>> getAll();
    public OperationResult<List<BillDTO>> getAllPending();
    public OperationResult<Void> updateById(int billId,BillDTO billDTO);
}
