package com.submeter.service;

import com.submeter.commonlib.dto.SubmeterDTO;
import com.submeter.commonlib.response.OperationResult;

public interface SubmeterService {
    OperationResult<SubmeterDTO> getById(int id);
}
