package com.submeter.service;

import com.submeter.commonlib.dto.MeterReadingDTO;
import com.submeter.commonlib.response.OperationResult;

public interface MeterReadingService {
    OperationResult<MeterReadingDTO> getLatestByMeterId(int meterId);
    public void update(int currentReadingId,long readingValue,String note );
}
