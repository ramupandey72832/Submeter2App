package com.submeter.service;

import com.submeter.data.dao.DaoManager;
import com.submeter.data.dao.impl.SqlLiteBillDao;
import com.submeter.data.dao.impl.SqlLiteMeterReadingDao;
import com.submeter.data.dao.impl.SqlLitePaymentDao;
import com.submeter.data.dao.impl.SqlLiteSubmeterAllocationDao;
import com.submeter.data.dao.impl.SqlLiteTenancyDao;
import com.submeter.data.dao.impl.SqlLiteTenantDao;
import com.submeter.data.db.DatabaseInitializer;
import com.submeter.service.impl.DatabaseMigrationServiceImpl;
import com.submeter.service.impl.ExportImportServiceImpl;
import com.submeter.service.impl.core.RoomServiceImpl;
import com.submeter.service.impl.core.SubmeterAllocationServiceImpl;
import com.submeter.service.impl.core.SubmeterServiceImpl;
import com.submeter.service.impl.core.TenantServiceImpl;
import com.submeter.service.impl.core.TenancyServiceImpl;
import com.submeter.service.impl.core.MeterReadingServiceImpl;
import com.submeter.service.impl.core.BillServiceImpl;
import com.submeter.service.impl.core.PaymentServiceImpl;

public final class ServiceManager {

    private static RoomService roomService;
    private static SubmeterService submeterService;
    private static TenantService tenantService;
    private static TenancyService tenancyService;
    private static MeterReadingService meterReadingService;
    private static BillService billService;
    private static PaymentService paymentService;
    private static DatabaseSetup databaseSetup;

    public static DatabaseMigrationService getDatabaseMigrationService() {
        ensureInitialized();
        if (databaseMigrationService == null) {
            databaseMigrationService = new DatabaseMigrationServiceImpl();
        }
        return databaseMigrationService;
    }

    private static DatabaseMigrationService databaseMigrationService;



    private static SubmeterAllocationService submeterAllocationService;
    private static ExportImportService exportImportService;

    private ServiceManager() {
        // Prevent instantiation.
    }

    public static synchronized void initialize() {
        if (roomService != null) {
            return; // Already initialized
        }
        databaseMigrationService = new DatabaseMigrationServiceImpl();

        // 2. Ensure DAOs are initialized
        DaoManager.initialize();

        // 3. Initialize all services with their respective DAOs safely
        roomService = new RoomServiceImpl(DaoManager.getRoomDao());
        submeterService = new SubmeterServiceImpl(DaoManager.getSubmeterDao());
        tenantService = new TenantServiceImpl(DaoManager.getTenantDao() != null
                ? DaoManager.getTenantDao() : new SqlLiteTenantDao());
        tenancyService = new TenancyServiceImpl(DaoManager.getTenancyDao() != null
                ? DaoManager.getTenancyDao() : new SqlLiteTenancyDao());
        meterReadingService = new MeterReadingServiceImpl(DaoManager.getMeterReadingDao() != null
                ? DaoManager.getMeterReadingDao() : new SqlLiteMeterReadingDao());
        billService = new BillServiceImpl(DaoManager.getBillDao() != null ? DaoManager.getBillDao() : new SqlLiteBillDao());
        paymentService = new PaymentServiceImpl(DaoManager.getPaymentDao() != null ? DaoManager.getPaymentDao()
                : new SqlLitePaymentDao());
        databaseSetup = new DatabaseSetup();
        exportImportService = new ExportImportServiceImpl();
        submeterAllocationService = new SubmeterAllocationServiceImpl(DaoManager.getSubmeterAllocationDao() != null
                ? DaoManager.getSubmeterAllocationDao() : new SqlLiteSubmeterAllocationDao());
    }

    private static void ensureInitialized() {
        if (roomService == null) {
            initialize(); // Auto-initialize if someone tries to grab a service before manual init
        }
    }

    public static RoomService getRoomService() {
        ensureInitialized();
        return roomService;
    }

    public static SubmeterService getSubmeterService() {
        ensureInitialized();
        return submeterService;
    }

    public static SubmeterAllocationService getSubmeterAllocationService() {
        ensureInitialized();
        return submeterAllocationService;
    }

    public static TenantService getTenantService() {
        ensureInitialized();
        return tenantService;
    }

    public static TenancyService getTenancyService() {
        ensureInitialized();
        return tenancyService;
    }

    public static MeterReadingService getMeterReadingService() {
        ensureInitialized();
        return meterReadingService;
    }

    public static BillService getBillService() {
        ensureInitialized();
        return billService;
    }

    public static PaymentService getPaymentService() {
        ensureInitialized();
        return paymentService;
    }

    public static ExportImportService getExportImportService() {
        ensureInitialized();
        return exportImportService;
    }

    public static DatabaseSetup getDatabaseSetup(){
        ensureInitialized();
        return databaseSetup;
    }
}

/**
 * initialize during startup
 * DatabaseInitializer.initializeDb(platform, dbPath);
 * ServiceManager.initialize();
 */
