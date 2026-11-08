package com.submeter.service;

import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.commonlib.response.OperationResult;

import java.util.List;

public interface TenantService {

    OperationResult<TenantDTO> getByTenantId(int tenantId);
    OperationResult<TenantDTO> getDefaultTenant();
    OperationResult<TenantDTO> getByTenantAadhaar(String aadhaar);
    OperationResult<Void> add(TenantDTO tenantDTO);
    OperationResult<List<TenantDTO>> getAll();
    OperationResult<Void> update(TenantDTO tenantDTO);
    OperationResult<Void> delete(TenantDTO tenantDTO);
}
