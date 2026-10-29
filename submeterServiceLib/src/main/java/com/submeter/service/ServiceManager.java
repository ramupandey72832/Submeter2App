package com.submeter.service;

import com.submeter.data.dao.DaoManager;
import com.submeter.data.dao.impl.SqlLiteBillDao;
import com.submeter.service.impl.ExportImportServiceImpl;
import com.submeter.service.impl.core.RoomServiceImpl;
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



    private static ExportImportService exportImportService;

    private ServiceManager() {
        // Prevent instantiation.
    }

    public static synchronized void initialize() {

        if (databaseSetup != null) {
            return;
        }

        roomService = new RoomServiceImpl(DaoManager.getRoomDao());
        submeterService = new SubmeterServiceImpl();
        tenantService = new TenantServiceImpl();
        tenancyService = new TenancyServiceImpl();
        meterReadingService = new MeterReadingServiceImpl();
        billingService = new BillServiceImpl(new SqlLiteBillDao());
        paymentService = new PaymentServiceImpl();
        databaseSetup = new DatabaseSetup();
        exportImportService =new ExportImportServiceImpl();
    }

    public static RoomService getRoomService() {
        ensureInitialized();
        return roomService;
    }

    public static SubmeterService getSubmeterService() {
        ensureInitialized();
        return submeterService;
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

    public static BillService getBillingService() {
        ensureInitialized();
        return billingService;
    }

    public static PaymentService getPaymentService() {
        ensureInitialized();
        return paymentService;
    }

    public static ExportImportService getExportImportService() {
        return exportImportService;
    }

    public static DatabaseSetup getDatabaseSetup(){
        return databaseSetup;
    }

    private static void ensureInitialized() {
        if (roomService == null) {
            throw new IllegalStateException(
                    "ServiceManager has not been initialized.");
        }
    }
}

/**
 * initialize during startup
 * DatabaseInitializer.initializeDb(platform, dbPath);
 * ServiceManager.initialize();
 */
