package com.submeter.service.utils;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.DaoManager;
import com.submeter.data.dao.DatabaseBackupDao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelGenerator {

    private static final String[] BILL_REPORT_HEADERS = {
            "Bill ID", "Room No.", "Tenant Name", "Billing Date", "Meter Serial",
            "Prev Reading", "Curr Reading", "Rate", "Fixed Charge", "Total", "Balance"
    };

    /**
     * Writes bill report data into an in-memory Excel workbook byte array.
     */
    public static OperationResult<byte[]> writeBillsToWorksheet(List<BillDTO> bills) {
        if (bills == null) {
            bills = new ArrayList<>();
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(baos);
            WritableSheet sheet = workbook.createSheet("Report", 0);

            // 1. Add Headers
            for (int i = 0; i < BILL_REPORT_HEADERS.length; i++) {
                sheet.addCell(new Label(i, 0, BILL_REPORT_HEADERS[i]));
            }

            // 2. Add Data Rows
            int row = 1;
            for (BillDTO item : bills) {
                sheet.addCell(new Number(0, row, item.getId()));
                sheet.addCell(new Label(1, row, safeString(item.getRoomNumber())));
                sheet.addCell(new Label(2, row, safeString(item.getTenantName())));
                sheet.addCell(new Label(3, row, safeString(item.getBillingDate())));
                sheet.addCell(new Label(4, row, safeString(item.getMeterSerialNumber())));
                sheet.addCell(new Number(5, row, item.getPreviousReading()));
                sheet.addCell(new Number(6, row, item.getCurrentReading()));
                sheet.addCell(new Number(7, row, item.getRatePerUnit()));
                sheet.addCell(new Number(8, row, item.getFixedCharge()));
                sheet.addCell(new Number(9, row, item.getTotalAmount()));
                sheet.addCell(new Number(10, row, item.getBalance()));
                row++;
            }

            workbook.write();
            workbook.close();

            return OperationResult.success(baos.toByteArray(), "Bill report generated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.failure("Failed to write Excel billing rows: " + e.getMessage());
        }
    }

    /**
     * Exports all managed database tables into an in-memory Excel workbook byte array.
     */
    public static OperationResult<byte[]> writeAllTableToWorksheet() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            WritableWorkbook workbook = Workbook.createWorkbook(baos);

            Map<String, DatabaseBackupDao.TableRecordSet> databaseData =
                    DaoManager.getDatabaseBackupDao().exportDatabaseTables();
            int sheetIndex = 0;

            for (Map.Entry<String, DatabaseBackupDao.TableRecordSet> entry : databaseData.entrySet()) {
                String tableName = entry.getKey();
                DatabaseBackupDao.TableRecordSet recordSet = entry.getValue();

                WritableSheet sheet = workbook.createSheet(tableName, sheetIndex++);
                int currentRow = 0;

                // Write Table Headers
                writeRowValues(sheet, currentRow++, recordSet.getColumns());

                // Write Table Rows
                for (List<String> rowValues : recordSet.getRows()) {
                    writeRowValues(sheet, currentRow++, rowValues);
                }
            }

            workbook.write();
            workbook.close();

            return OperationResult.success(baos.toByteArray(), "Export completed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.failure("Export framework failure: " + e.getMessage());
        }
    }

    /**
     * Parses an in-memory Excel byte array payload and passes structured table data to DAO.
     */
    /**
     * Parses an in-memory Excel byte array payload and passes structured table data to DAO.
     */
    public static OperationResult<Void> importWorksheetToDatabase(byte[] excelBytes) {
        if (excelBytes == null || excelBytes.length == 0) {
            return OperationResult.failure("Import file byte array cannot be empty or null.");
        }

        Workbook workbook = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(excelBytes)) {
            workbook = Workbook.getWorkbook(bais);

            Map<String, DatabaseBackupDao.TableRecordSet> importedData = new LinkedHashMap<>();

            for (String tableName : DaoManager.getDatabaseBackupDao().MANAGED_BACKUP_TABLES) {
                Sheet sheet = workbook.getSheet(tableName);
                if (sheet == null) continue;

                int rows = sheet.getRows();
                int cols = sheet.getColumns();
                if (rows <= 1) continue;

                List<String> columns = readRowValues(sheet, 0, cols);
                List<List<String>> dataRows = new ArrayList<>();

                for (int r = 1; r < rows; r++) {
                    dataRows.add(readRowValues(sheet, r, columns.size()));
                }

                importedData.put(tableName, new DatabaseBackupDao.TableRecordSet(columns, dataRows));
            }

            // Directly send parsed record sets to the DAO
            DaoManager.getDatabaseBackupDao().importDatabaseTables(importedData);

            return OperationResult.success(null, "Import completed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return OperationResult.failure("Import validation engine failed: " + e.getMessage());
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    // --- Helper Utilities ---

    private static void writeRowValues(WritableSheet sheet, int row, List<String> values) throws Exception {
        if (sheet == null || values == null) return;
        for (int col = 0; col < values.size(); col++) {
            String val = values.get(col);
            sheet.addCell(new Label(col, row, val != null ? val : ""));
        }
    }

    private static List<String> readRowValues(Sheet sheet, int rowIndex, int expectedColCount) {
        List<String> row = new ArrayList<>(expectedColCount);
        int sheetCols = sheet.getColumns();
        for (int c = 0; c < expectedColCount; c++) {
            if (c < sheetCols) {
                row.add(sheet.getCell(c, rowIndex).getContents());
            } else {
                row.add("");
            }
        }
        return row;
    }

    private static String safeString(String value) {
        return value != null ? value : "";
    }
}