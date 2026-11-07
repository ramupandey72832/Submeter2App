package com.submeter.data.dao;

import java.sql.SQLException;

public interface DbMigrationDao {
    boolean executeMigration() throws SQLException;
    boolean checkMetadataKeyExists(String key) throws SQLException;
    void setMetadataKey(String key, String value) throws SQLException;
}