package com.submeter.service;

import com.submeter.commonlib.dto.MeterReadingDTO;
import com.submeter.commonlib.response.OperationResult;

import java.util.List;

public interface MeterReadingService {
    OperationResult<MeterReadingDTO> getLatestByMeterId(int meterId);
    OperationResult<MeterReadingDTO> getById(int Id);
    OperationResult<MeterReadingDTO> getBySubmeterId(int meterId);
    public OperationResult<Void> update(MeterReadingDTO meterReadingDTO);
    public OperationResult<Integer> add(MeterReadingDTO meterReadingDTO);
    public OperationResult<Void> delete(int meterReadingId);
    public OperationResult<List<MeterReadingDTO>> getAll();

}
