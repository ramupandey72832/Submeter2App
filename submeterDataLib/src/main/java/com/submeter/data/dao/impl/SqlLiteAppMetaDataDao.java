package com.submeter.data.dao.impl;

import com.submeter.data.dao.AppMetaDataDao;
import com.submeter.data.entities.AppMetaData;

import java.sql.SQLException;
import java.util.List;


import com.example.rrallutilitylib.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlLiteAppMetaDataDao implements AppMetaDataDao {

    @Override
    public List<AppMetaData> getAll() throws SQLException {
        String sql = "SELECT key, value FROM app_metadata;";
        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            throw new SQLException("Database connection could not be retrieved.");
        }

        List<AppMetaData> metadataList = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AppMetaData data = new AppMetaData();
                data.setKey(rs.getString("key"));
                data.setValue(rs.getString("value"));
                metadataList.add(data);
            }
        }
        return metadataList;
    }

    @Override
    public Optional<AppMetaData> getByKey(String key) throws SQLException {
        String sql = "SELECT key, value FROM app_metadata WHERE key = ?;";
        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            throw new SQLException("Database connection could not be retrieved.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AppMetaData data = new AppMetaData();
                    data.setKey(rs.getString("key"));
                    data.setValue(rs.getString("value"));
                    return Optional.of(data);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void set(String key, String value) throws SQLException {
        String sql = "INSERT OR REPLACE INTO app_metadata (key, value) VALUES (?, ?);";
        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            throw new SQLException("Database connection could not be retrieved.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        }
    }

    @Override
    public boolean exists(String key) throws SQLException {
        String sql = "SELECT 1 FROM app_metadata WHERE key = ?;";
        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) {
            throw new SQLException("Database connection could not be retrieved.");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
