package com.submeter.data.dao;

import com.submeter.data.entities.Tenancy;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TenancyDao {
    boolean add(Tenancy tenancy) throws SQLException;
    Optional<Tenancy> getById(int tenancyId) throws SQLException;
    Optional<Tenancy> getActiveTenancyByRoomId(int roomId) throws SQLException;
    Optional<Tenancy> getActiveTenancyByRoomNumber(String roomNumber) throws SQLException;
    boolean update(Tenancy tenancy) throws SQLException;
    List<Tenancy> getByRoomId(int roomId) throws SQLException;
    List<Tenancy> getByRoomNumber(String roomNumber) throws SQLException;
    boolean endTenancy(Tenancy tenancy) throws SQLException;
    List<Tenancy> getAll() throws SQLException;
}