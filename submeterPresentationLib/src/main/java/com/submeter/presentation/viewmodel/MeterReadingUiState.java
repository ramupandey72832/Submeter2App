package com.submeter.presentation.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReadingUiState {
    private  boolean success;
    private  String roomNumber;
    private  String tenantName;
    private  int tenantId;
    private  String serialNumber;
    private  int previousReadingId;
    private  long previousReadingValue;
    private  double defaultRate;
    private  double defaultFixedCharge;
}
