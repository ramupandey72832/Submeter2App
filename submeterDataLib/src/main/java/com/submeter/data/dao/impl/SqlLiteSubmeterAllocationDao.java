package com.submeter.data.dao.impl;

import com.submeter.data.dao.SubmeterAllocationDao;
import com.submeter.data.entities.SubmeterAllocation;
import com.example.rrallutilitylib.config.PropertiesLoader;
import com.example.rrallutilitylib.db.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlLiteSubmeterAllocationDao implements SubmeterAllocationDao {

    @Override
    public int insertAllocation(SubmeterAllocation allocation) throws SQLException {
        // Business Rule Check: Ensure this meter isn't already actively allocated elsewhere
        List<SubmeterAllocation> activeAllocations = getActiveAllocationsByMeterId(allocation.getMeterId());
        if (!activeAllocations.isEmpty()) {
            throw new SQLException("Submeter with ID " + allocation.getMeterId() + " is already actively allocated to a room.");
        }

        String sql = PropertiesLoader.getProperty("queries.properties", "allocation.insert");
        return JdbcHelper.executeInsert(sql,
                allocation.getMeterId(),
                allocation.getRoomId(),
                allocation.getStartingDate(),
                allocation.getEndingDate() != null ? allocation.getEndingDate() : ""
        );
    }

    @Override
    public Optional<SubmeterAllocation> getAllocationById(int allocationId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "allocation.get_by_id");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToAllocation, allocationId);
    }

    @Override
    public List<SubmeterAllocation> getActiveAllocationsByMeterId(int meterId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "allocation.get_active_by_meter");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToAllocation, meterId);
    }

    @Override
    public List<SubmeterAllocation> getActiveAllocationsByRoomId(int roomId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "allocation.get_active_by_room");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToAllocation, roomId);
    }

    @Override
    public List<SubmeterAllocation> getAllAllocations() throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "allocation.get_all");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToAllocation);
    }

    @Override
    public boolean endAllocation(int allocationId, String endDate) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "allocation.end");
        return JdbcHelper.executeUpdate(sql, endDate, allocationId);
    }

    @Override
    public boolean updateAllocation(SubmeterAllocation allocation) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "allocation.update");
        return JdbcHelper.executeUpdate(sql,
                allocation.getMeterId(),
                allocation.getRoomId(),
                allocation.getStartingDate(),
                allocation.getEndingDate() != null ? allocation.getEndingDate() : "",
                allocation.getId()
        );
    }

    @Override
    public boolean deleteAllocation(int allocationId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "allocation.delete");
        return JdbcHelper.executeUpdate(sql, allocationId);
    }

    private SubmeterAllocation mapResultSetToAllocation(ResultSet rs) throws SQLException {
        SubmeterAllocation allocation = new SubmeterAllocation();
        allocation.setId(rs.getInt("id"));
        allocation.setMeterId(rs.getInt("meter_id"));
        allocation.setRoomId(rs.getInt("room_id"));
        allocation.setStartingDate(rs.getString("starting_date"));
        allocation.setEndingDate(rs.getString("ending_date"));
        return allocation;
    }
}