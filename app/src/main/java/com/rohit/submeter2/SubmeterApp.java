package com.rohit.submeter2;


import android.app.Application;
import android.util.Log;

import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.DatabaseMigrationService;
import com.submeter.service.ServiceManager; // Or whichever class provides your services

public class SubmeterApp extends Application {

    private static final String TAG = "SubmeterApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // Run database migration and one-time seeding globally on app startup
        initializeDatabaseAndServices();
    }

    private void initializeDatabaseAndServices() {
        try {
            DatabaseMigrationService migrationService = ServiceManager.getDatabaseMigrationService();
            OperationResult<Void> result = migrationService.migrationDb();

            if (result.isSuccess()) {
                Log.i(TAG, "Database initialized and migrated successfully.");
            } else {
                Log.e(TAG, "Database migration failed: " + result.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "Critical error during app database initialization: " + e.getMessage(), e);
        }
    }
}