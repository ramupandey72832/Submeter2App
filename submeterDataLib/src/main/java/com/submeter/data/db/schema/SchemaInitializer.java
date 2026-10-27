package com.submeter.data.db.schema;


import com.example.rrallutilitylib.config.PropertiesLoader;
import com.example.rrallutilitylib.db.JdbcHelper;

public final class SchemaInitializer {

    private SchemaInitializer() {
        throw new UnsupportedOperationException(
                "SchemaInitializer is a utility class"
        );
    }

    /**
     * Creates all application tables and views.
     *
     * SQL statements are executed in dependency order.
     */
    public static void initializeSchema() throws Exception {

        executeSchemaCreation();

        System.out.println("Database schema initialized successfully.");
    }

    /**
     * Drops all application tables and views.
     *
     * WARNING: This operation deletes database structures.
     */
    public static void dropSchema() throws Exception {

        executeSchemaDeletion();

        System.out.println("Database schema dropped successfully.");
    }

    private static void executeSchemaCreation() throws Exception {

        execute("schema.enable_foreign_keys");

        execute("schema.create_room_table");
        execute("schema.create_submeter_table");
        execute("schema.create_tenant_table");
        execute("schema.create_tenancy_table");
        execute("schema.create_meter_reading_table");
        execute("schema.create_bills_table");
        execute("schema.create_submeter_allocation_table");
        execute("schema.create_payments_table");

//        execute("schema.create_bill_summaries_view");
    }

    private static void executeSchemaDeletion() throws Exception {

        execute("schema.disable_foreign_keys");

//        execute("schema.drop_bill_summaries_view");

        execute("schema.drop_rooms_table");
        execute("schema.drop_submeters_table");
        execute("schema.drop_tenants_table");
        execute("schema.drop_meter_readings_table");
        execute("schema.drop_tenancy_table");
        execute("schema.drop_submeter_allocation_table");
        execute("schema.drop_bills_table");
        execute("schema.drop_payment_table");
    }

    private static void execute(String sqlKey) throws Exception {
        JdbcHelper.executeUpdate(PropertiesLoader.getProperty("queries.properties", sqlKey));
    }
}