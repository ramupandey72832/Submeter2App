package com.submeter.data.dao;

import com.example.rrallutilitylib.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseBackupDao {

    public final String[] MANAGED_BACKUP_TABLES = {
            "rooms",
            "tenants",
            "submeters",
            "tenancies",
            "meter_readings",
            "bills"
    };

    /**
     * Encapsulates the schema columns and row dataset for a specific database table.
     */
    public static class TableRecordSet {
        private final List<String> columns;
        private final List<List<String>> rows;

        public TableRecordSet(List<String> columns, List<List<String>> rows) {
            this.columns = columns;
            this.rows = rows;
        }

        public List<String> getColumns() {
            return columns;
        }

        public List<List<String>> getRows() {
            return rows;
        }
    }

    /**
     * Exports all managed database tables into an in-memory map of table record sets.
     */
    public Map<String, TableRecordSet> exportDatabaseTables() throws Exception {
        Map<String, TableRecordSet> exportedDatabaseMap = new LinkedHashMap<>();
        Connection connection = DatabaseConnection.getConnection();

        for (String tableName : MANAGED_BACKUP_TABLES) {
            String query = "SELECT * FROM `" + tableName + "`";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                List<String> columns = new ArrayList<>(columnCount);
                for (int col = 1; col <= columnCount; col++) {
                    columns.add(metaData.getColumnName(col));
                }

                List<List<String>> rows = new ArrayList<>();
                while (resultSet.next()) {
                    List<String> row = new ArrayList<>(columnCount);
                    for (int col = 1; col <= columnCount; col++) {
                        String value = resultSet.getString(col);
                        row.add(value != null ? value : "");
                    }
                    rows.add(row);
                }

                exportedDatabaseMap.put(tableName, new TableRecordSet(columns, rows));
            }
        }
        return exportedDatabaseMap;
    }

    /**
     * Imports database tables directly from a map of table record sets.
     */
    public void importDatabaseTables(Map<String, TableRecordSet> importedData) throws Exception {
        if (importedData == null || importedData.isEmpty()) {
            return;
        }

        Connection connection = DatabaseConnection.getConnection();
        DatabaseConnection.beginTransaction();

        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = OFF;");
            }

            // --- DELETE IN REVERSE ORDER (Child tables first) ---
            for (int i = MANAGED_BACKUP_TABLES.length - 1; i >= 0; i--) {
                String tableName = MANAGED_BACKUP_TABLES[i];
                try (Statement deleteStatement = connection.createStatement()) {
                    deleteStatement.execute("DELETE FROM `" + tableName + "`");
                }
            }

            // --- INSERT IN FORWARD ORDER (Parent tables first) ---
            for (String tableName : MANAGED_BACKUP_TABLES) {
                TableRecordSet recordSet = importedData.get(tableName);
                if (recordSet == null || recordSet.getColumns().isEmpty() || recordSet.getRows().isEmpty()) {
                    continue;
                }

                List<String> columns = recordSet.getColumns();
                List<List<String>> rows = recordSet.getRows();

                StringBuilder columnsBuilder = new StringBuilder();
                StringBuilder placeholdersBuilder = new StringBuilder();

                for (int c = 0; c < columns.size(); c++) {
                    columnsBuilder.append("`").append(columns.get(c)).append("`");
                    placeholdersBuilder.append("?");
                    if (c < columns.size() - 1) {
                        columnsBuilder.append(", ");
                        placeholdersBuilder.append(", ");
                    }
                }

                String insertSql = "INSERT INTO `" + tableName + "` (" + columnsBuilder + ") VALUES (" + placeholdersBuilder + ");";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                    for (List<String> rowData : rows) {
                        for (int c = 0; c < columns.size(); c++) {
                            String cellContent = (c < rowData.size()) ? rowData.get(c) : null;
                            if (cellContent == null || cellContent.isEmpty()) {
                                preparedStatement.setNull(c + 1, java.sql.Types.NULL);
                            } else {
                                preparedStatement.setString(c + 1, cellContent);
                            }
                        }
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                }
            }

            DatabaseConnection.commitTransaction();

        } catch (Exception e) {
            DatabaseConnection.rollbackTransaction();
            throw e;
        } finally {
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON;");
            } catch (Exception ignored) {}
        }
    }
}