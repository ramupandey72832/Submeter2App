package com.submeter.presentation.viewmodel;

import com.submeter.service.MeterReadingService;
import com.submeter.service.RoomService;
import com.submeter.service.SubmeterAllocationService;
import com.submeter.service.SubmeterService;
import com.submeter.service.TenancyService;
import com.submeter.service.TenantService;

public class MeterReadingViewModel extends BaseViewModel {

    private final RoomService roomService;
    private final MeterReadingService meterReadingService;
    private final TenancyService tenancyService;
    private final SubmeterAllocationService submeterAllocationService;
    private final TenantService tenantService;
    private final SubmeterService submeterService;

    private MeterReadingUiState uiState;

    public MeterReadingViewModel(RoomService roomService,
                                 MeterReadingService meterReadingService,
                                 TenancyService tenancyService,
                                 SubmeterAllocationService submeterAllocationService,
                                 TenantService tenantService,
                                 SubmeterService submeterService) {
        this.roomService = roomService;
        this.meterReadingService = meterReadingService;
        this.tenancyService = tenancyService;
        this.submeterAllocationService = submeterAllocationService;
        this.tenantService = tenantService;
        this.submeterService = submeterService;
    }

    public MeterReadingUiState getUiState() {
        return uiState;
    }

    public void getLatestMeterReadingByRoomNumber(String roomNumber, String defaultFallbackTenantName) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            setErrorMessage("Room number cannot be empty.");
            return;
        }

        setLoading(true);

        new Thread(() -> {
            try {
                var room = roomService.getByRoomNumber(roomNumber);
                if (room == null || room.getData() == null) {
                    setErrorMessage("Room not found: " + roomNumber);
                    return;
                }
                int roomId = room.getData().getId();

                var allocation = submeterAllocationService.getActiveMeterByRoomId(roomId);
                if (allocation == null || allocation.getData() == null) {
                    setErrorMessage("No active submeter allocated for this room.");
                    return;
                }
                int meterId = allocation.getData().getMeterId();

                var tenancy = tenancyService.getByRoomId(roomId);
                int tenantId = 0;
                String tenantName = null;

                if (tenancy != null && tenancy.getData() != null) {
                    tenantId = tenancy.getData().getTenantId();
                    var tenant = tenantService.getByTenantId(tenantId);
                    if (tenant != null && tenant.getData() != null) {
                        tenantName = tenant.getData().getName();
                    }
                }

                // Fallback to the passed string if tenant name couldn't be resolved
                if (tenantName == null || tenantName.trim().isEmpty()) {
                    tenantName = (defaultFallbackTenantName != null && !defaultFallbackTenantName.trim().isEmpty())
                            ? defaultFallbackTenantName
                            : "admin";
                }

                var reading = meterReadingService.getLatestByMeterId(meterId);
                var submeter = submeterService.getById(meterId);

                // Success: Set UI State
                this.uiState = new MeterReadingUiState(
                        true,
                        roomNumber,
                        tenantName,
                        tenantId,
                        submeter != null && submeter.getData() != null ? submeter.getData().getMeterSerialNumber() : "",
                        reading != null && reading.getData() != null ? reading.getData().getId() : 0,
                        reading != null && reading.getData() != null ? reading.getData().getReadingValue() : 0,
                        0, 0
                );

                setErrorMessage(null);
                setOperationSuccess(true);

            } catch (Exception e) {
                this.uiState = null;
                setErrorMessage("Error: " + e.getMessage());
            } finally {
                setLoading(false);
            }
        }).start();
    }
}