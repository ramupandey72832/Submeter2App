package com.submeter.service;

import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.commonlib.response.OperationResult;

import java.util.List;

public interface TenancyService {
    public OperationResult<List<TenancyDTO>> getByRoomId(int roomId);
    public OperationResult<List<TenancyDTO>> getByRoomNumber(String roomNumber);
    public OperationResult<TenancyDTO> getActiveTenancyByRoomNumber(String roomNumber);
    public OperationResult<Void> add(TenancyDTO tenancyDTO);
    public OperationResult<TenancyDTO> getActiveTenancyByRoomId(int roomId);
    public OperationResult<Void> update(TenancyDTO tenancyDTO);
    public OperationResult<Void> endTenancy(TenancyDTO tenancyDTO);
    public OperationResult<List<TenancyDTO>> getAll();

}
