package com.submeter.service;

import com.submeter.commonlib.dto.SubmeterDTO;
import com.submeter.commonlib.response.OperationResult;

import java.util.List;

public interface SubmeterService {
    OperationResult<Void>  add(SubmeterDTO submeterDTO);
    OperationResult<Void> updateById(SubmeterDTO submeterDTO);
    public OperationResult<SubmeterDTO> getById(int id);
    public OperationResult<SubmeterDTO> getBySerialNumber(String serialNumber);
    OperationResult<List<SubmeterDTO>> getAll();
}
