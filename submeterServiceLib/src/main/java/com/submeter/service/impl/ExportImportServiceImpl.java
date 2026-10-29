package com.submeter.service.impl;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.ExportImportService;

import java.util.List;

public class ExportImportServiceImpl implements ExportImportService {
    @Override
    public OperationResult<List<BillDTO>> getLatestBills() {
        return null;
    }

    @Override
    public OperationResult<List<BillDTO>> getLastThreeMonthBills() {
        return null;
    }

    @Override
    public OperationResult<List<BillDTO>> getBillsByDateRange(String startingDate, String endingDate) {
        return OperationResult.failure("No bills found for the given range");
//        return null;
    }

    @Override
    public OperationResult<Void> importAllTable() {
        return null;
    }

    @Override
    public OperationResult<Void> exportAllTable() {
        return null;
    }
}
