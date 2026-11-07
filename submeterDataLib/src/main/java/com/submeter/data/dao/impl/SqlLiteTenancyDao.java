package com.submeter.data.dao.impl;

import com.submeter.data.dao.TenancyDao;
import com.submeter.data.entities.Tenancy;
import com.example.rrallutilitylib.config.PropertiesLoader;
import com.example.rrallutilitylib.db.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlLiteTenancyDao implements TenancyDao {

    @Override
    public boolean add(Tenancy tenancy) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.insert");
        int generatedId = JdbcHelper.executeInsert(sql,
                tenancy.getRoomId(),
                tenancy.getTenantId(),
                tenancy.getStartDate(),
                tenancy.getEndDate() != null ? tenancy.getEndDate() : ""
        );
        if (generatedId > 0) {
            tenancy.setId(generatedId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Tenancy> getById(int tenancyId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.get_by_id");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToTenancy, tenancyId);
    }

    @Override
    public Optional<Tenancy> getActiveTenancyByRoomId(int roomId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.get_active_by_room_id");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToTenancy, roomId);
    }

    @Override
    public Optional<Tenancy> getActiveTenancyByRoomNumber(String roomNumber) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.get_active_by_room_number");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToTenancy, roomNumber);
    }

    @Override
    public boolean update(Tenancy tenancy) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.update");
        return JdbcHelper.executeUpdate(sql,
                tenancy.getRoomId(),
                tenancy.getTenantId(),
                tenancy.getStartDate(),
                tenancy.getEndDate() != null ? tenancy.getEndDate() : "",
                tenancy.getId()
        );
    }

    @Override
    public List<Tenancy> getByRoomId(int roomId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.get_by_room_id");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToTenancy, roomId);
    }

    @Override
    public List<Tenancy> getByRoomNumber(String roomNumber) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.get_by_room_number");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToTenancy, roomNumber);
    }

    @Override
    public boolean endTenancy(Tenancy tenancy) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.end_tenancy");
        return JdbcHelper.executeUpdate(sql,
                tenancy.getEndDate(),
                tenancy.getId()
        );
    }

    @Override
    public List<Tenancy> getAll() throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "tenancy.get_all");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToTenancy);
    }

    private Tenancy mapResultSetToTenancy(ResultSet rs) throws SQLException {
        Tenancy tenancy = new Tenancy();
        tenancy.setId(rs.getInt("id"));
        tenancy.setRoomId(rs.getInt("room_id"));
        tenancy.setTenantId(rs.getInt("tenant_id"));
        tenancy.setStartDate(rs.getString("start_date"));
        tenancy.setEndDate(rs.getString("end_date"));
        return tenancy;
    }
}