package com.submeter.service;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.OperationResult;

import java.util.List;

public interface BillService {
    public OperationResult<List<BillDTO>> getAll();
    public OperationResult<List<BillDTO>> getAllPending();
    public OperationResult<Void> update(BillDTO billDTO);
    public OperationResult<Void> add(BillDTO billDTO);
    public OperationResult<BillDTO> getById(int billId);
    public OperationResult<Double> getTotalTenantBalance(int tenantId);
}
