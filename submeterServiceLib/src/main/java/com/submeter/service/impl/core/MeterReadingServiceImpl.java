package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.MeterReadingDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.MeterReadingService;

public class MeterReadingServiceImpl implements MeterReadingService {

     public void add(int submeterId, long readingValue, String readingDate, String imageURI){}

    @Override
    public OperationResult<MeterReadingDTO> getLatestByMeterId(int meterId) {
        return null;
    }

    public void update(int meterReadingId, long readingValue, String imageURI){}
    void getAll(){}
    void getBySubmeterId(int submeterId){ }
    void getById(int meterReadingId){ }
}
