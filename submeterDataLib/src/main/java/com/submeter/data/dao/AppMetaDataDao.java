package com.submeter.data.dao;

import com.submeter.data.entities.AppMetaData;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AppMetaDataDao {
    List<AppMetaData> getAll() throws SQLException;
    Optional<AppMetaData> getByKey(String key) throws SQLException;
    void set(String key, String value) throws SQLException;
    boolean exists(String key) throws SQLException;
}