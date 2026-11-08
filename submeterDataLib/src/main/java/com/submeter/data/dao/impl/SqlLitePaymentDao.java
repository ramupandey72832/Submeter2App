package com.submeter.data.dao.impl;


import com.example.rrallutilitylib.config.PropertiesLoader;
import com.example.rrallutilitylib.db.JdbcHelper;
import com.submeter.data.dao.PaymentDao;
import com.submeter.data.entities.Payment;
import com.submeter.data.entities.Room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlLitePaymentDao implements PaymentDao {


    @Override
    public boolean insert(Payment payment) throws SQLException {
        if (payment == null) {
            return false;
        }

        String query = PropertiesLoader.getProperty("queries.properties", "payment.insert");
        int generatedId = JdbcHelper.executeInsert(
                query,
                payment.getBillId(),
                payment.getAmountPaid(),
                payment.getPaymentDate(),
                payment.getNote()
        );

        if (generatedId > 0) {
            payment.setId(generatedId);
            return true;
        }
        return false;
    }

    @Override
    public List<Payment> getByBillId(int billId) throws SQLException {
        String query = PropertiesLoader.getProperty("queries.properties", "payment.getByBillId");
        return JdbcHelper.executeQueryList(query, this::mapResultSetToPayment , billId);
    }

    @Override
    public Payment getById(int id) throws SQLException {
        String query = PropertiesLoader.getProperty("queries.properties", "payment.getById");
        Optional<Payment> optionalPayment = JdbcHelper.executeQuerySingle(query, this::mapResultSetToPayment , id);
        return optionalPayment.orElse(null);
    }

    @Override
    public List<Payment> getAll() throws SQLException {
        String query = PropertiesLoader.getProperty("queries.properties", "payment.getAll");
        return JdbcHelper.executeQueryList(query, this::mapResultSetToPayment );
    }
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setBillId(rs.getInt("bill_id"));
        payment.setAmountPaid(rs.getLong("amount_paid"));
        payment.setPaymentDate(rs.getString("payment_date"));
        payment.setNote(rs.getString("note"));
        return payment;
    };

}