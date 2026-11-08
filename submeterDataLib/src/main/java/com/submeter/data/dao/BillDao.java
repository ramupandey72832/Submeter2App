package com.submeter.data.dao;

import com.submeter.data.entities.Bill;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;

public interface BillDao {
    boolean insert(Bill bill) throws SQLException;
    boolean update(Bill bill) throws SQLException;
    boolean deleteById(int billId) throws SQLException;
    Optional<Bill> getById(int billId) throws SQLException;
    List<Bill> getByTenantId(int tenantId) throws SQLException;
    List<Bill> getByMeterId(int meterId) throws SQLException;
    List<Bill> getAll() throws SQLException;
    Optional<Bill> getLatestBill(String roomNumber) throws SQLException;
    List<Bill> getLatestThreeMonthBills() throws SQLException;
    List<Bill> getBillsByRange(String start, String end) throws SQLException;
    public List<Bill> getPendingBills() throws SQLException;
}