package com.submeter.service;

import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.commonlib.response.OperationResult;

public interface TenantService {
    OperationResult<TenantDTO> getByTenantId(int tenantId);
}
