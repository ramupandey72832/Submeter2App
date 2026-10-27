package com.submeter.commonlib.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDTO implements Serializable {
        // Highly recommended for version control during serialization
        private static final long serialVersionUID = 1L;

        private int id;
        private int meterId;
        private String roomNumber;
        private int tenantId;
        private String tenantName;
        private String meterSerialNumber;
        private int previousReadingId;
        private int currentReadingId;
        private long currentReading;
        private long previousReading;
        private long unitsConsumed;
        private double ratePerUnit;
        private double fixedCharge;
        private double extraCharge;
        private double totalAmount;
        private String note;
        private String billingDate;
        private double balance;
    }
