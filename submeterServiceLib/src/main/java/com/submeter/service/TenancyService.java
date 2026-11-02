package com.submeter.service;

import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.commonlib.response.OperationResult;

public interface TenancyService {
    OperationResult<TenancyDTO> getByRoomId(int roomId);
}
