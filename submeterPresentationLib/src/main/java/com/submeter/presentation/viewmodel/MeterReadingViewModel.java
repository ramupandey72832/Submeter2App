package com.submeter.presentation.viewmodel;

import com.submeter.commonlib.dto.SubmeterDTO;
import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.impl.core.MeterReadingServiceImpl;
import com.submeter.service.impl.core.RoomServiceImpl;
import com.submeter.service.impl.core.TenancyServiceImpl;

import java.util.Optional;


/**
 * On MeterReading Fragment we have to show the previous recorded meter Reading and Tenant Details
 * Our Recorded person will only use Room Number Instead of submeter Serial Number
 * Use Room Number to find Room id and use this id on SubmeterAllocation table and Tenancy Table
 * and USE METERREADING TO GET THE LATEST BILL OF A SPECIFIC METER THROUGH METERID ( SUBMETER_ALLOCATION TABLE
 * HAVE METER ID)
 */

public class MeterReadingViewModel {

    private final RoomServiceImpl roomService;
    private final MeterReadingServiceImpl meterReadingService;
    private final TenancyServiceImpl tenancyService;

    public MeterReadingViewModel(RoomServiceImpl roomService,
                                 MeterReadingServiceImpl meterReadingService,
                                 TenancyServiceImpl tenancyService) {
        this.roomService = roomService;
        this.meterReadingService = meterReadingService;
        this.tenancyService = tenancyService;
    }


    // --- Clean Action Flow ---

//    public void checkRoomDetails(String roomNumber) {
//        if (roomNumber == null || roomNumber.trim().isEmpty()) {
//            updateState(new UiState(false, "Validation Error: Room number cannot be blank.", false, "", 0.0, "", ""));
//            return;
//        }
//
//        roomNumber = roomNumber.trim();
//
//        // Show Loading State
//
//
//        // Execute background check (Consider using an ExecutorService or Android Architecture Components)
//        new Thread(() -> {
//            try {
//                // Step 1: Verify Submeter
//                OperationResult<SubmeterDTO> submeterOpt = roomService.getSubmeterByRoomNumber(roomNumber);
//                if (!submeterOpt.isSuccess()) {
//                    emitError("Asset Error: No deployed submeter found for room " + roomNumber);
//                    return;
//                }
//                SubmeterDTO submeter = submeterOpt.getData();
//
//                // Step 2: Verify Active Tenancy
//                TenancyDTO tenancy = tenancyService.findActiveTenancyByRoomNumber(roomNumber);
//                if (tenancy == null) {
//                    emitError("Lease Error: There is no active tenant assigned to room " + roomNumber);
//                    return;
//                }
//
//                // Step 3: Verify Tenant Demographics
//                Optional<TenantDTO> tenantOpt = tenancyService.findTenantByAadhar(tenancy.getTenantAaddhar());
//                if (tenantOpt.isEmpty()) {
//                    emitError("Data Inconsistency: Active occupant records are missing.");
//                    return;
//                }
//
//                // Step 4: Fetch Historical Data & Publish Success State
//                double prevReading = meterReadingService.getLatestReading(submeter.getMeterSerialNumber());
//                TenantDTO tenant = tenantOpt.get();
//
//
//            } catch (Exception e) {
//                emitError("Critical Verification Failure: " + e.getMessage());
//            }
//        }).start();
//    }
}