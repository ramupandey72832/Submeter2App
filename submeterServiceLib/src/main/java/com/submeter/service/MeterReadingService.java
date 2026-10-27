package com.submeter.service;

import com.submeter.commonlib.dto.BillDTO;

public interface MeterReadingService {
    public void update(int currentReadingId,long readingValue,String note );
}
