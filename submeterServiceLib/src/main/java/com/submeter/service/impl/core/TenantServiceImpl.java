package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.TenantService;

public class TenantServiceImpl implements TenantService {

    void add(String name,String phoneNumber,String aadhaarNumber, String address){}
    void updateById(int tenantId, String name,String phoneNumber,String aadhaarNumber, String address){}
    void getById(int tenantId){}
    void getAll(){}

    @Override
    public OperationResult<TenantDTO> getByTenantId(int tenantId) {
        return null;
    }
}
