package com.submeter.data.dao;

import com.submeter.data.entities.Tenant;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TenantDao {
    // Create
    boolean insert(Tenant tenant) throws SQLException;
    Optional<Tenant> getById(int tenantId) throws SQLException;
    Optional<Tenant> getByAadhaar(String aadhaarNumber) throws SQLException;
    List<Tenant> getAll() throws SQLException;
    boolean update(Tenant tenant) throws SQLException;

    boolean delete(int tenantId) throws SQLException;
    void insertDefaultTenantIfNotExists() throws SQLException;
}