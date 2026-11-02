package com.submeter.presentation.viewmodel;

import com.submeter.commonlib.dto.SubmeterDTO;
import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.MeterReadingService;
import com.submeter.service.RoomService;
import com.submeter.service.SubmeterAllocationService;
import com.submeter.service.SubmeterService;
import com.submeter.service.TenancyService;
import com.submeter.service.TenantService;
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

public class MeterReadingViewModel extends BaseViewModel{

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

        public void getLatestMeterReadingByRoomNumber(String roomNumber) {
            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                setErrorMessage("Room number cannot be empty.");
                return;
            }

            setLoading(true);

            new Thread(() -> {
                try {
                    //1 . get Room id from Room Table then get the latest Submeter allocation
                    var roomDTO = roomService.findByRoomNumber(roomNumber);
                    if(roomDTO.getData() == null){return;}

                    int roomId = roomDTO.getData().getId();
                    var submeterAllocationDTO = submeterAllocationService.getActiveMeterByRoomId(roomId);
                    if(submeterAllocationDTO.getData() == null){return;}

                    int meterId = submeterAllocationDTO.getData().getMeterId();
                    var meterReadingDTO = meterReadingService.getLatestByMeterId(meterId);
                    if(meterReadingDTO.getData() == null){return;}

                    var tenancyDTO = tenancyService.getByRoomId(roomId);
                    if(tenancyDTO.getData() == null){return;}
                    int tenantId = tenancyDTO.getData().getTenantId();

                    var tenantDTO = tenantService.getByTenantId(tenantId);
                    if(tenantDTO.getData() == null){return;}

                    var SubmeterDTO = submeterService.getById(meterId);
                    if(SubmeterDTO.getData() == null){return;}
                    String meterSerialNumber = SubmeterDTO.getData().getMeterSerialNumber();


                    this.uiState = new MeterReadingUiState(
                            true,
                            roomNumber,
                            tenantDTO.getData().getName(),
                            tenantId,
                            meterSerialNumber,
                            meterReadingDTO.getData().getId(),
                            meterReadingDTO.getData().getReadingValue(),
                            0,
                            0
                    );

                    setOperationSuccess(true);
                    setErrorMessage(null);

                } catch (Exception e) {
                    this.uiState = null;
                    setOperationSuccess(false);
                    setErrorMessage(e.getMessage());
                } finally {
                    setLoading(false);
                }
            }).start();
        }
}
