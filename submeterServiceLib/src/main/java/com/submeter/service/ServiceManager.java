package com.submeter.service;

import com.submeter.data.dao.DaoManager;
import com.submeter.data.dao.impl.SqlLiteBillDao;
import com.submeter.data.db.DatabaseInitializer;
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
    private static BillService billingService;
    private static PaymentService paymentService;
    private static DatabaseSetup databaseSetup;



    private static SubmeterAllocationService submeterAllocationService;
    private static ExportImportService exportImportService;

    private ServiceManager() {
        // Prevent instantiation.
    }

    public static synchronized void initialize() {
        if (roomService != null) {
            return; // Already initialized
        }

        // 2. Ensure DAOs are initialized
        DaoManager.initialize();

        // 3. Initialize all services with their respective DAOs safely
        roomService = new RoomServiceImpl(DaoManager.getRoomDao());
        submeterService = new SubmeterServiceImpl();
        tenantService = new TenantServiceImpl();
        tenancyService = new TenancyServiceImpl();
        meterReadingService = new MeterReadingServiceImpl();
        billingService = new BillServiceImpl(DaoManager.getBillDao() != null ? DaoManager.getBillDao() : new SqlLiteBillDao());
        paymentService = new PaymentServiceImpl();
        databaseSetup = new DatabaseSetup();
        exportImportService = new ExportImportServiceImpl();
        submeterAllocationService = new SubmeterAllocationServiceImpl();
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
        return billingService;
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
