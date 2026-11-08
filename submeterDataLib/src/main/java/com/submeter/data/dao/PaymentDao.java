package com.submeter.data.dao;

import com.submeter.data.entities.Payment;
import com.submeter.data.entities.Room;

import java.sql.SQLException;
import java.util.List;

public interface PaymentDao {
    boolean insert(Payment payment) throws SQLException;

    List<Payment> getByBillId(int billId) throws SQLException;

    Payment getById(int id) throws SQLException;

    List<Payment> getAll() throws SQLException;
}
