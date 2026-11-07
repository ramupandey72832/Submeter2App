package com.submeter.service.impl;

import com.example.rrallutilitylib.db.DatabaseConnection;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.DaoManager;
import com.submeter.service.DatabaseMigrationService;

public class DatabaseMigrationServiceImpl implements DatabaseMigrationService {

    @Override
    public OperationResult<Void> migrationDb() {
        boolean transactionStarted = false;

        try {
            DatabaseConnection.beginTransaction();
            transactionStarted = true;

            // 1. Run standard schema migrations
            boolean migrated = DaoManager.getDbMigrationDao().executeMigration();

            if (!migrated) {
                throw new IllegalStateException("Database migration did not complete.");
            }

            // 2. Execute any one-time-only setup tasks safely inside the transaction
            executeOneTimeInitializations();

            DatabaseConnection.commitTransaction();
            transactionStarted = false;

            return OperationResult.success(
                    null,
                    "Database migration completed successfully."
            );

        } catch (Exception e) {
            if (transactionStarted) {
                try {
                    DatabaseConnection.rollbackTransaction();
                } catch (Exception rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }

            return OperationResult.failure(
                    "Database migration failed: " + e.getMessage()
            );
        }
    }

    /**
     * Executes tasks that must run strictly ONCE across the lifetime of the application database.
     * Uses a metadata tracking key in the database to prevent duplicate execution.
     */
    public OperationResult<Void> executeOneTimeInitializations() {
        try {
            String metadataKey = "DEFAULT_TENANT_INITIALIZED";

            // Check if the one-time setup has already run
            boolean isAlreadyInitialized = DaoManager.getDbMigrationDao().checkMetadataKeyExists(metadataKey);

            if (!isAlreadyInitialized) {
                // Insert the default fallback tenant for unallocated rooms
                DaoManager.getTenantDao().insertDefaultTenantIfNotExists();

                // Mark as completed so it never runs again
                DaoManager.getDbMigrationDao().setMetadataKey(metadataKey, "true");
            }

            return OperationResult.success(null, "One-time initializations executed successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Failed to execute one-time initializations: " + e.getMessage());
        }
    }
}