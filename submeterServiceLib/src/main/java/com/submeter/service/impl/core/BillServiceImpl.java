package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.response.ResourceNotFoundException;
import com.submeter.data.dao.BillDao;
import com.submeter.data.entities.Bill;
import com.submeter.service.BillService;

import java.sql.SQLException;
import java.util.List;

public class BillServiceImpl implements BillService {

    BillDao billDao ;

    public BillServiceImpl(BillDao billDao){
        this.billDao = billDao;
    }

    public void add(){}
    public void updateById(int billId,BillDTO billDTO){
        // update meterReading , update bill ( Never include payment in here )
        // since this is bill Only , we will do UPDATE BILL and meter reading will be in meterReadingSErvice
    }
    public BillDTO getById(int billId) throws SQLException {
        Bill bill = billDao.getById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

        // Leverage Lombok's builder instead of a custom constructor
        return BillDTO.builder()
                .id(bill.getId())
                .meterId(bill.getMeterId())
                .roomNumber(bill.getRoomNumber())
                .tenantId(bill.getTenantId())
                .tenantName(bill.getTenantName())
                .meterSerialNumber(bill.getMeterSerialNumber())
                .previousReadingId(bill.getPreviousReadingId())
                .currentReadingId(bill.getCurrentReadingId())
                .currentReading(bill.getCurrentReading())
                .previousReading(bill.getPreviousReading())
                .unitsConsumed(bill.getUnitsConsumed())
                .ratePerUnit(bill.getRatePerUnit())
                .fixedCharge(bill.getFixedCharge())
                .extraCharge(bill.getExtraCharge())
                .totalAmount(bill.getTotalAmount())
                .note(bill.getNote())
                .billingDate(bill.getBillingDate() != null ? bill.getBillingDate().toString() : null)
                .build();
    }


    public List<BillDTO> getAll(){ return null;}
    public List<BillDTO> getAllPending(){

        return null;
    }
}


// use DTO TO GET AND SEND DATA but in function body of service we can use entity