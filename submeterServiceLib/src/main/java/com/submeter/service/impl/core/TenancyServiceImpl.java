package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.TenancyService;

public class TenancyServiceImpl implements TenancyService {

    void add(int roomId,int tenantId,String startingDate, String endingDate){}
    void updateById(int tenancyId){}
    void getById(int tenancyId){}
    void getAll(){}

    @Override
    public OperationResult<TenancyDTO> getByRoomId(int roomId) {
        return null;
    }
}
