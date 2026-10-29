package com.submeter.presentation.viewmodel;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.ExportImportService;
import com.submeter.service.utils.ExcelGenerator;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExportImportBillViewModel extends BaseViewModel {

    private final ExportImportService exportImportService;

    // Core Presentation States
    private boolean isLoading = false;
    private String errorMessage = null;
    private String successMessage = null;

    // Domain Data Holders
    private Map<String, List<BillDTO>> groupedPreviousReports = new LinkedHashMap<>();
    private List<BillDTO> rangeFilteredBills = new ArrayList<>();
    private List<BillDTO> latestMonthlyBills = new ArrayList<>();
    private byte[] generatedExcelReportBytes = null;
    private byte[] generatedBackupBytes = null;

    public ExportImportBillViewModel(ExportImportService exportImportService) {
        this.exportImportService = exportImportService;
    }

    // --- State Getters ---
    public boolean isLoading() { return isLoading; }
    public String getErrorMessage() { return errorMessage; }
    public String getSuccessMessage() { return successMessage; }
    public Map<String, List<BillDTO>> getGroupedPreviousReports() { return groupedPreviousReports; }
    public List<BillDTO> getRangeFilteredBills() { return rangeFilteredBills; }
    public List<BillDTO> getLatestMonthlyBills() { return latestMonthlyBills; }
    public byte[] getGeneratedExcelReportBytes() { return generatedExcelReportBytes; }
    public byte[] getGeneratedBackupBytes() { return generatedBackupBytes; }

    // --- Business Actions ---

    public void loadHistoricalThreeMonthReports() {
        updateLoadingState(true);
        new Thread(() -> {
            try {
                OperationResult<List<BillDTO>> result = exportImportService.getLastThreeMonthBills();

                // Guard check for null result or unsuccessful operation
                if (result != null && result.isSuccess() && result.getData() != null) {
                    List<BillDTO> bills = result.getData();
                    this.groupedPreviousReports = bills.stream()
                            .collect(Collectors.groupingBy(
                                    b -> b.getBillingDate() != null && b.getBillingDate().length() >= 7
                                            ? b.getBillingDate().substring(0, 7)
                                            : "Unknown",
                                    LinkedHashMap::new,
                                    Collectors.toList()
                            ));
                } else {
                    this.groupedPreviousReports = new LinkedHashMap<>();
                }
            } catch (Exception e) {
                this.errorMessage = "Failed to load past histories: " + e.getMessage();
            } finally {
                updateLoadingState(false);
            }
        }).start();
    }

    public void exportBillsByRange(String startDate, String endDate) {
        updateLoadingState(true);
        new Thread(() -> {
            try {
                OperationResult<List<BillDTO>> result = exportImportService.getBillsByDateRange(startDate, endDate);

                if (result != null && result.isSuccess() && result.getData() != null) {
                    this.rangeFilteredBills = result.getData();
                    OperationResult<byte[]> excelResult = ExcelGenerator.writeBillsToWorksheet(this.rangeFilteredBills);

                    if (excelResult != null && excelResult.isSuccess()) {
                        this.generatedExcelReportBytes = excelResult.getData();
                        this.successMessage = "Excel range report compiled successfully!";
                    } else {
                        this.errorMessage = excelResult != null ? excelResult.getMessage() : "Failed to compile Excel report";
                    }
                } else {
                    this.errorMessage = result != null ? result.getMessage() : "Service returned a null response";
                }
            } catch (Exception e) {
                this.errorMessage = "Error processing date bounds report: " + e.getMessage();
            } finally {
                updateLoadingState(false);
            }
        }).start();
    }

    public void getLatestBills() {
        updateLoadingState(true);
        new Thread(() -> {
            try {
                OperationResult<List<BillDTO>> result = exportImportService.getLatestBills();
                if (!result.isSuccess()) {
                    this.errorMessage = result.getMessage();
                    return;
                }
                this.latestMonthlyBills = result.getData();
            } catch (Exception e) {
                this.errorMessage = "Error gathering active targets: " + e.getMessage();
            } finally {
                updateLoadingState(false);
            }
        }).start();
    }

    public void executeFullExcelBackup() {
        updateLoadingState(true);
        new Thread(() -> {
            OperationResult<byte[]> result = ExcelGenerator.writeAllTableToWorksheet();
            if (result.isSuccess()) {
                this.generatedBackupBytes = result.getData();
                this.successMessage = "Database successfully backed up to Excel workbook!";
            } else {
                this.errorMessage = result.getMessage();
            }
            updateLoadingState(false);
        }).start();
    }

    // Accepts byte array payload instead of raw InputStream parameter
    public void executeFullExcelImport(byte[] excelFileBytes) {
        if (excelFileBytes == null || excelFileBytes.length == 0) {
            this.errorMessage = "Import file payload is empty.";
            notifyStateChanged();
            return;
        }

        updateLoadingState(true);
        new Thread(() -> {
            try {
                // Pass byte[] directly instead of wrapping it in a ByteArrayInputStream
                OperationResult<Void> result = ExcelGenerator.importWorksheetToDatabase(excelFileBytes);

                if (result.isSuccess()) {
                    this.successMessage = "Database successfully restored from Excel workbook!";
                    loadHistoricalThreeMonthReports();
                } else {
                    this.errorMessage = result.getMessage();
                }
            } catch (Exception e) {
                this.errorMessage = "Import execution failed: " + e.getMessage();
            } finally {
                updateLoadingState(false);
            }
        }).start();
    }

    // --- Private Helpers ---
    private void updateLoadingState(boolean loading) {
        this.isLoading = loading;
        if (loading) {
            this.errorMessage = null;
            this.successMessage = null;
        }
        notifyStateChanged();
    }
}