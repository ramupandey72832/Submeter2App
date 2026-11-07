package com.submeter.data.dao.impl;

import com.submeter.data.dao.SubmeterDao;
import com.submeter.data.entities.Submeter;
import com.example.rrallutilitylib.config.PropertiesLoader;
import com.example.rrallutilitylib.db.JdbcHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlLiteSubmeterDao implements SubmeterDao {
    @Override
    public int insertSubmeter(Submeter submeter) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "submeter.insert");
        if (sql == null || sql.trim().isEmpty()) {
            throw new SQLException("Query 'submeter.insert' not found in queries.properties!");
        }

        return JdbcHelper.executeInsert(sql,
                submeter.getMeterSerialNumber(),
                submeter.getInitialReading(),
                submeter.getNote()
        );
    }
    @Override
    public Optional<Submeter> getSubmeterById(int meterId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "submeter.get_by_id");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToSubmeter, meterId);
    }

    @Override
    public Optional<Submeter> getSubmeterBySerialNumber(String serialNumber) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "submeter.get_by_serial");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToSubmeter, serialNumber);
    }



    @Override
    public List<Submeter> getAllSubmeters() throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "submeter.get_all");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToSubmeter);
    }

    @Override
    public boolean updateSubmeter(Submeter submeter) throws SQLException {
        // Room ID is no longer updated here
        String sql = PropertiesLoader.getProperty("queries.properties", "submeter.update");
        return JdbcHelper.executeUpdate(sql,
                submeter.getMeterSerialNumber(),
                submeter.getInitialReading(),
                submeter.getNote(),
                submeter.getId()
        );
    }

    @Override
    public boolean deleteSubmeter(int meterId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "submeter.delete");
        return JdbcHelper.executeUpdate(sql, meterId);
    }

    /**
     * Maps a single row from the ResultSet to a Submeter entity object (without room_id).
     */
    private Submeter mapResultSetToSubmeter(ResultSet rs) throws SQLException {
        Submeter submeter = new Submeter();
        submeter.setId(rs.getInt("id"));
        submeter.setMeterSerialNumber(rs.getString("meter_serial_number"));
        submeter.setInitialReading(rs.getLong("initial_reading"));
        submeter.setNote(rs.getString("note"));
        return submeter;
    }
}
