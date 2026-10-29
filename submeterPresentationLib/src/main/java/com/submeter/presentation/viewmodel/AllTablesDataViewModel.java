package com.submeter.presentation.viewmodel;


import com.submeter.service.TableDataService;

import java.util.ArrayList;
import java.util.List;

public class AllTablesDataViewModel extends BaseViewModel {
    // TODO Change TABLEDATASERVICE NAME TO DIFFERENT

    private final TableDataService dataService = new TableDataService();
    private final List<String> currentHeaders = new ArrayList<>();
    private final List<List<String>> currentRows = new ArrayList<>();


    public void loadTableData(String tableName) {
        setLoading(true);
        setErrorMessage(null);
        setOperationSuccess(false);

        this.currentHeaders.clear();
        this.currentRows.clear();

        try {
            // Fetch headers and rows directly from the service layer
            List<String> fetchedHeaders = dataService.getTableHeaders(tableName);
            List<List<String>> fetchedRows = dataService.getTableRows(tableName);

            this.currentHeaders.addAll(fetchedHeaders);
            this.currentRows.addAll(fetchedRows);

            setOperationSuccess(true);
        } catch (Exception e) {
            setErrorMessage("Failed to load table data: " + e.getMessage());
        } finally {
            setLoading(false); // Triggers notifyStateChanged() automatically
        }
    }

    public List<String> getCurrentHeaders() { return currentHeaders; }
    public List<List<String>> getCurrentRows() { return currentRows; }
}
