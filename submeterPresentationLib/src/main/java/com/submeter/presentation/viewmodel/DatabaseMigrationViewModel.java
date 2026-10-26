package com.submeter.presentation.viewmodel;

import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.DatabaseMigrationService;

/**
 * IT WILL BE USED FOR MIGRATION ( USE IN MIGRATION FRAGMENT )
 * DIRECT LINK TO DATA LAYER MIGRATION CODE THROUGH SERVICE LAYER
 */

public class DatabaseMigrationViewModel extends BaseViewModel {

    private final DatabaseMigrationService service;

    public DatabaseMigrationViewModel(DatabaseMigrationService service) {
        this.service = service;
    }

    public void runMigrationDb() {
        setLoading(true);
        setErrorMessage(null);
        setOperationSuccess(false);

        new Thread(() -> {
            OperationResult<Void> result = service.migrationDb();

            setOperationSuccess(result.isSuccess());

            if (!result.isSuccess()) {
                setErrorMessage(result.getMessage());
            }

            setLoading(false);
        }).start();
    }

}
