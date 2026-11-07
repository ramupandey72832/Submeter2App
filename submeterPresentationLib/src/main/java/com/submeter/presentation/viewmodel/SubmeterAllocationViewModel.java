package com.submeter.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.commonlib.dto.SubmeterAllocationDTO;
import com.submeter.commonlib.dto.SubmeterDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.RoomService;
import com.submeter.service.ServiceManager;
import com.submeter.service.SubmeterAllocationService;
import com.submeter.service.SubmeterService;

public class SubmeterAllocationViewModel extends ViewModel {

    private final SubmeterAllocationService allocationService = ServiceManager.getSubmeterAllocationService();
    private final SubmeterService submeterService = ServiceManager.getSubmeterService();
    private final RoomService roomService = ServiceManager.getRoomService();

    private final MutableLiveData<SubmeterAllocationDTO> allocationLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

    public LiveData<SubmeterAllocationDTO> getAllocationLiveData() {
        return allocationLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    // --- 1. SAVE ALLOCATION ---
    public void saveAllocation(String meterSerialNumber, String roomNumber, String startingDate, String endingDate) {
        if (meterSerialNumber == null || meterSerialNumber.trim().isEmpty() ||
                roomNumber == null || roomNumber.trim().isEmpty() ||
                startingDate == null || startingDate.trim().isEmpty()) {
            toastMessageLiveData.setValue("Meter Serial Number, Room Number, and Starting Date are required");
            return;
        }

        try {
            String serial = meterSerialNumber.trim();
            String targetRoomNum = roomNumber.trim();

            // 1. Fetch Submeter DTO by Serial Number
            OperationResult<SubmeterDTO> submeterResult = submeterService.getBySerialNumber(serial);
            if (!submeterResult.isSuccess() || submeterResult.getData() == null) {
                toastMessageLiveData.setValue("Submeter with serial '" + serial + "' does not exist.");
                return;
            }

            // 2. Fetch Room DTO by Room Number
            OperationResult<RoomDTO> roomResult = roomService.getByRoomNumber(targetRoomNum);
            if (!roomResult.isSuccess() || roomResult.getData() == null) {
                toastMessageLiveData.setValue("Room number '" + targetRoomNum + "' does not exist.");
                return;
            }

            // 3. Build Allocation DTO
            SubmeterAllocationDTO newAllocation = new SubmeterAllocationDTO();
            newAllocation.setMeterId(submeterResult.getData().getId());
            newAllocation.setRoomId(roomResult.getData().getId());
            newAllocation.setStartingDate(startingDate.trim());
            newAllocation.setEndingDate(endingDate != null ? endingDate.trim() : "");

            // 4. Save via allocation service
            OperationResult<Void> result = allocationService.add(newAllocation);
            if (result.isSuccess()) {
                toastMessageLiveData.setValue("Submeter allocated successfully!");
            } else {
                toastMessageLiveData.setValue(result.getMessage() != null ? result.getMessage() : "Failed to allocate submeter.");
            }

        } catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
        }
    }

    // --- 2. SEARCH BY ID (Automatically populates Serial Number & Room Number) ---
    public void searchAllocationById(String idStr) {
        if (idStr == null || idStr.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter Allocation ID to search");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());
            OperationResult<SubmeterAllocationDTO> allocationResult = allocationService.getById(id);

            if (!allocationResult.isSuccess() || allocationResult.getData() == null) {
                allocationLiveData.setValue(null);
                toastMessageLiveData.setValue("No allocation found with ID: " + id);
                return;
            }

            SubmeterAllocationDTO allocation = allocationResult.getData();

            // Populate human-readable fields for the UI form
            OperationResult<SubmeterDTO> submeterResult = submeterService.getById(allocation.getMeterId());
            if (submeterResult.isSuccess() && submeterResult.getData() != null) {
                allocation.setMeterSerialNumber(submeterResult.getData().getMeterSerialNumber());
            }

            OperationResult<RoomDTO> roomResult = roomService.getRoomById(allocation.getRoomId());
            if (roomResult.isSuccess() && roomResult.getData() != null) {
                allocation.setRoomNumber(roomResult.getData().getRoomNumber());
            }

            allocationLiveData.setValue(allocation);
            toastMessageLiveData.setValue("Allocation found!");

        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Allocation ID format");
        }
    }

    // --- 3. UPDATE ALLOCATION ---
    public void updateAllocation(SubmeterAllocationDTO currentAllocation, String meterSerialNumber, String roomNumber, String startingDate, String endingDate) {
        if (currentAllocation == null) {
            toastMessageLiveData.setValue("No allocation selected for update");
            return;
        }

        if (meterSerialNumber == null || meterSerialNumber.trim().isEmpty() ||
                roomNumber == null || roomNumber.trim().isEmpty() ||
                startingDate == null || startingDate.trim().isEmpty()) {
            toastMessageLiveData.setValue("Meter Serial Number, Room Number, and Starting Date cannot be empty");
            return;
        }

        try {
            String serial = meterSerialNumber.trim();
            String targetRoomNum = roomNumber.trim();

            // 1. Resolve new Serial Number to ID
            OperationResult<SubmeterDTO> submeterResult = submeterService.getBySerialNumber(serial);
            if (!submeterResult.isSuccess() || submeterResult.getData() == null) {
                toastMessageLiveData.setValue("Submeter with serial '" + serial + "' does not exist.");
                return;
            }

            // 2. Resolve new Room Number to ID
            OperationResult<RoomDTO> roomResult = roomService.getByRoomNumber(targetRoomNum);
            if (!roomResult.isSuccess() || roomResult.getData() == null) {
                toastMessageLiveData.setValue("Room number '" + targetRoomNum + "' does not exist.");
                return;
            }

            // 3. Update fields
            currentAllocation.setMeterId(submeterResult.getData().getId());
            currentAllocation.setRoomId(roomResult.getData().getId());
            currentAllocation.setStartingDate(startingDate.trim());
            currentAllocation.setEndingDate(endingDate != null ? endingDate.trim() : "");

            OperationResult<Void> result = allocationService.update(currentAllocation);
            if (result.isSuccess()) {
                toastMessageLiveData.setValue("Allocation updated successfully!");
                allocationLiveData.setValue(null); // Clear selection after update
            } else {
                toastMessageLiveData.setValue(result.getMessage() != null ? result.getMessage() : "Failed to update allocation.");
            }
        } catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
        }
    }
}