package com.submeter.service;


import com.submeter.data.dao.DaoManager;
import com.submeter.data.entities.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableDataService {

    public List<String> getTableHeaders(String tableName) {
        switch (tableName.toUpperCase()) {
            case "ROOMS": return Arrays.asList("Room ID", "Room Number", "Room Type");
            case "TENANTS": return Arrays.asList("Tenant ID", "Name", "Aadhaar", "Mobile", "Address");
            case "TENANCIES": return Arrays.asList("Tenancy ID", "Room ID", "Tenant ID", "Start Date", "End Date", "Is Active");
            case "SUBMETERS": return Arrays.asList("Meter ID", "Room ID", "Serial Number", "Initial Reading");
            case "SUBMETERS_ALLOCATION": return Arrays.asList("Allocation ID", "Meter ID", "Room ID", "Start Date", "End Date");
            case "METER_READINGS": return Arrays.asList("Reading ID", "Meter ID", "Reading Value", "Reading Date");
            case "BILLS": return Arrays.asList("Bill ID", "Prev Reading ID", "Curr Reading ID", "Meter ID", "Serial Num",
                    "Tenant ID", "Tenant Name", "Room Num", "Units", "Rate", "Fixed", "Extra", "Total", "Note", "Date");
            default: throw new IllegalArgumentException("Unknown table name: " + tableName);
        }
    }

    public List<List<String>> getTableRows(String tableName) throws SQLException {
        List<List<String>> rows = new ArrayList<>();

        switch (tableName.toUpperCase()) {
            case "ROOMS":
                for (Room r : DaoManager.getRoomDao().getAllRooms()) {
                    rows.add(Arrays.asList(String.valueOf(r.getId()), r.getRoomNumber(), r.getRoomType()));
                }
                break;

            case "TENANTS":
                for (Tenant t : DaoManager.getTenantDao().getAllTenants()) {
                    rows.add(Arrays.asList(String.valueOf(t.getId()), t.getName(), t.getAadhaarNumber(), t.getPhoneNumber(), t.getAddress()));
                }
                break;

            case "TENANCIES":
                for (Tenancy ty : DaoManager.getTenancyDao().getAllTenancies()) {
                    rows.add(Arrays.asList(
                            String.valueOf(ty.getId()), String.valueOf(ty.getRoomId()), String.valueOf(ty.getTenantId()), ty.getStartDate(),
                            ty.getEndDate() != null ? ty.getEndDate() : "--", ty.getEndDate() == null ? "YES" : "NO"
                    ));
                }
                break;

            case "SUBMETERS":
                for (Submeter s : DaoManager.getSubmeterDao().getAllSubmeters()) {
                    rows.add(Arrays.asList(String.valueOf(s.getId()),
                            s.getMeterSerialNumber(), s.getInitialReading() + " kWh"));
                }
                break;

            case "SUBMETERS_ALLOCATION":
                for (SubmeterAllocation s : DaoManager.getSubmeterAllocationDao().getAllAllocations()) {
                    rows.add(Arrays.asList(String.valueOf(s.getId()),
                            String.valueOf(s.getMeterId()), String.valueOf(s.getRoomId()),
                            s.getStartingDate(), s.getEndingDate()));
                }
                break;

            case "BILLS":
                for (Bill b : DaoManager.getBillDao().getAllBills()) {
                    rows.add(Arrays.asList(
                            String.valueOf(b.getId()), String.valueOf(b.getPreviousReadingId()), String.valueOf(b.getCurrentReadingId()),
                            String.valueOf(b.getMeterId()), b.getMeterSerialNumber(), String.valueOf(b.getTenantId()), b.getTenantName(),
                            b.getRoomNumber(), String.valueOf(b.getUnitsConsumed()), String.valueOf(b.getRatePerUnit()),
                            String.valueOf(b.getFixedCharge()), String.valueOf(b.getExtraCharge()), "₹" + b.getTotalAmount(), b.getNote(), b.getBillingDate()
                    ));
                }
                break;

            case "METER_READINGS":
                for (MeterReading mr : DaoManager.getMeterReadingDao().getAllReadings()) {
                    rows.add(Arrays.asList(String.valueOf(mr.getId()), String.valueOf(mr.getMeterId()), mr.getReadingValue() + " kWh", mr.getDate()));
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown table name: " + tableName);
        }
        return rows;
    }
}
