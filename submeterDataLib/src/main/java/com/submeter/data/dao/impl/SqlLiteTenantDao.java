package com.submeter.data.dao.impl;

import com.submeter.data.dao.TenantDao;
import com.submeter.data.entities.Tenant;
import com.example.rrallutilitylib.config.PropertiesLoader;
import com.example.rrallutilitylib.db.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlLiteTenantDao implements TenantDao {

    @Override
    public boolean insert(Tenant tenant) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenant.insert");
        int generatedId = JdbcHelper.executeInsert(sql,
                tenant.getName(),
                tenant.getPhoneNumber(),
                tenant.getAadhaarNumber(),
                tenant.getAddress()
        );
        if (generatedId > 0) {
            tenant.setId(generatedId);
            return true;
        }
        return false;
    }
    @Override
    public void insertDefaultTenantIfNotExists() throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenant.insert_default");
        JdbcHelper.executeUpdate(sql);
    }

    @Override
    public Optional<Tenant> getById(int tenantId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenant.get_by_id");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToTenant, tenantId);
    }

    @Override
    public Optional<Tenant> getByAadhaar(String aadhaarNumber) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenant.get_by_aadhaar");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToTenant, aadhaarNumber);
    }

    @Override
    public List<Tenant> getAll() throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenant.get_all");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToTenant);
    }

    @Override
    public boolean update(Tenant tenant) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenant.update");
        return JdbcHelper.executeUpdate(sql,
                tenant.getName(),
                tenant.getPhoneNumber(),
                tenant.getAadhaarNumber(),
                tenant.getAddress(),
                tenant.getId()
        );
    }

    @Override
    public boolean delete(int tenantId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenant.delete");
        return JdbcHelper.executeUpdate(sql, tenantId);
    }

    private Tenant mapResultSetToTenant(ResultSet rs) throws SQLException {
        Tenant tenant = new Tenant();
        tenant.setId(rs.getInt("id"));
        tenant.setName(rs.getString("name"));
        tenant.setPhoneNumber(rs.getString("phone_number"));
        tenant.setAadhaarNumber(rs.getString("aadhaar_number"));
        tenant.setAddress(rs.getString("address"));
        return tenant;
    }
}