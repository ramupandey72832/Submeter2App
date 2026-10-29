package com.submeter.service;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.OperationResult;

import java.util.List;

public interface ExportImportService {
    OperationResult<List<BillDTO>> getLatestBills();
    OperationResult<List<BillDTO>> getLastThreeMonthBills();
    OperationResult<List<BillDTO>> getBillsByDateRange(String startingDate,String endingDate);
    OperationResult<Void> importAllTable();
    OperationResult<Void>  exportAllTable();
}
