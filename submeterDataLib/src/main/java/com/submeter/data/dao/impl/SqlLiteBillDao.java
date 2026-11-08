package com.submeter.data.dao.impl;

import com.example.rrallutilitylib.config.PropertiesLoader;
import com.example.rrallutilitylib.db.JdbcHelper;
import com.submeter.data.dao.BillDao;
import com.submeter.data.entities.Bill;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SqlLiteBillDao implements BillDao {

    @Override
    public boolean insert(Bill bill) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.insert");
        int generatedId = JdbcHelper.executeInsert(sql,
                bill.getMeterId(),
                bill.getRoomNumber(),
                bill.getTenantId(),
                bill.getTenantName(),
                bill.getMeterSerialNumber(),
                bill.getPreviousReadingId(),
                bill.getCurrentReadingId(),
                bill.getCurrentReading(),
                bill.getPreviousReading(),
                bill.getUnitsConsumed(),
                bill.getRatePerUnit(),
                bill.getFixedCharge(),
                bill.getExtraCharge(),
                bill.getTotalAmount(),
                bill.getNote(),
                bill.getBillingDate()
        );

        if (generatedId > 0) {
            bill.setId(generatedId);
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Bill bill) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.update");
        return JdbcHelper.executeUpdate(sql,
                bill.getMeterId(),
                bill.getRoomNumber(),
                bill.getTenantId(),
                bill.getTenantName(),
                bill.getMeterSerialNumber(),
                bill.getPreviousReadingId(),
                bill.getCurrentReadingId(),
                bill.getCurrentReading(),
                bill.getPreviousReading(),
                bill.getUnitsConsumed(),
                bill.getRatePerUnit(),
                bill.getFixedCharge(),
                bill.getExtraCharge(),
                bill.getTotalAmount(),
                bill.getNote(),
                bill.getBillingDate(),
                bill.getId()
        );
    }

    @Override
    public boolean deleteById(int billId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.delete_by_id");
        return JdbcHelper.executeUpdate(sql, billId);
    }

    @Override
    public Optional<Bill> getById(int billId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.get_by_id");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToBill, billId);
    }

    @Override
    public List<Bill> getByTenantId(int tenantId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.get_by_tenant_id");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToBill, tenantId);
    }

    @Override
    public List<Bill> getByMeterId(int meterId) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.get_by_meter_id");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToBill, meterId);
    }

    @Override
    public List<Bill> getAll() throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.get_all");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToBill);
    }

    @Override
    public Optional<Bill> getLatestBill(String roomNumber) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.get_latest_bill");
        return JdbcHelper.executeQuerySingle(sql, this::mapResultSetToBill, roomNumber);
    }

    @Override
    public List<Bill> getLatestThreeMonthBills() throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.get_latest_three_month_bills");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToBill);
    }

    @Override
    public List<Bill> getBillsByRange(String start, String end) throws SQLException {
        String sql = PropertiesLoader.getProperty("queries.properties", "bill.get_bills_by_range");
        return JdbcHelper.executeQueryList(sql, this::mapResultSetToBill, start, end);
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setMeterId(rs.getInt("meter_id"));
        bill.setRoomNumber(rs.getString("room_number"));
        bill.setTenantId(rs.getInt("tenant_id"));
        bill.setTenantName(rs.getString("tenant_name"));
        bill.setMeterSerialNumber(rs.getString("meter_serial_number"));
        bill.setPreviousReadingId(rs.getInt("previous_reading_id"));
        bill.setCurrentReadingId(rs.getInt("current_reading_id"));
        bill.setCurrentReading(rs.getLong("current_reading"));
        bill.setPreviousReading(rs.getLong("previous_reading"));
        bill.setUnitsConsumed(rs.getLong("units_consumed"));
        bill.setRatePerUnit(rs.getDouble("rate_per_unit"));
        bill.setFixedCharge(rs.getDouble("fixed_charge"));
        bill.setExtraCharge(rs.getDouble("extra_charge"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setNote(rs.getString("note"));
        bill.setBillingDate(rs.getString("billing_date"));
        return bill;
    }
}