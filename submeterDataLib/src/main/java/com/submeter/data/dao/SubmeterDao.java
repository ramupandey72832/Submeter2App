package com.submeter.data.dao;

import com.submeter.data.entities.Submeter;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SubmeterDao {
    int insertSubmeter(Submeter submeter) throws SQLException;

    // Read
    Optional<Submeter> getSubmeterById(int meterId) throws SQLException;
    Optional<Submeter> getSubmeterBySerialNumber(String serialNumber) throws SQLException;
    List<Submeter> getAllSubmeters() throws SQLException;

    // Update
    boolean updateSubmeter(Submeter submeter) throws SQLException;

    // Delete
    boolean deleteSubmeter(int meterId) throws SQLException;
}
