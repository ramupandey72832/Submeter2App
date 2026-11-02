package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.SubmeterAllocationDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.SubmeterAllocationService;

public class SubmeterAllocationServiceImpl implements SubmeterAllocationService {
    void add(int meterId,int roomId,String staringDate,String endingDate){}
    void update(int submeterAllocationId){}
    void getById(int submeterAllocationId){}
    void getAll(){}

    @Override
    public OperationResult<SubmeterAllocationDTO> getActiveMeterByRoomId(int roomId) {
        return null;
    }

    @Override
    public OperationResult<SubmeterAllocationDTO> getActiveMeterByMeterId(int meterId) {
        return null;
    }
}


