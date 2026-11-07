package com.submeter.service;

import com.submeter.commonlib.dto.SubmeterAllocationDTO;
import com.submeter.commonlib.response.OperationResult;

public interface SubmeterAllocationService {
    OperationResult<SubmeterAllocationDTO> getActiveMeterByRoomId(int roomId);
    OperationResult<SubmeterAllocationDTO> getActiveMeterByMeterId(int meterId);
    OperationResult<Void> add(SubmeterAllocationDTO submeterAllocationDTO);
    OperationResult<SubmeterAllocationDTO> getById(int id);
    OperationResult<Void> update(SubmeterAllocationDTO submeterAllocationDTO);
}
