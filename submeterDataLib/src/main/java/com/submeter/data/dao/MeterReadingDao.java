package com.submeter.data.dao;

import com.submeter.data.entities.MeterReading;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MeterReadingDao {
    int insert(MeterReading reading) throws SQLException;
    Optional<MeterReading> getById(int readingId) throws SQLException;
    Optional<List<MeterReading>> getReadingsByMeterId(int meterId) throws SQLException;
    Optional<MeterReading> getLatestReadingByMeterId(int meterId) throws SQLException;
    Optional<MeterReading> getLatestMeterReadingBySerialNumber(String serialNumber) throws SQLException;
    List<MeterReading> getAllReadings() throws SQLException;

    boolean updateReading(MeterReading reading) throws SQLException;
    boolean deleteReading(int readingId) throws SQLException;
}