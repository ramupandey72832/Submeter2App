package com.submeter.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.SubmeterAllocationDTO;

public class SubmeterAllocationViewModel extends ViewModel {

    private final MutableLiveData<SubmeterAllocationDTO> allocationLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

    public LiveData<SubmeterAllocationDTO> getAllocationLiveData() {
        return allocationLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    // --- 1. SAVE ALLOCATION ---
    public void saveAllocation(String meterIdStr, String roomIdStr, String startingDate, String endingDate) {
        if (meterIdStr == null || meterIdStr.trim().isEmpty() ||
                roomIdStr == null || roomIdStr.trim().isEmpty() ||
                startingDate == null || startingDate.trim().isEmpty()) {
            toastMessageLiveData.setValue("Meter ID, Room ID, and Starting Date are required");
            return;
        }

        try {
            int meterId = Integer.parseInt(meterIdStr.trim());
            int roomId = Integer.parseInt(roomIdStr.trim());

            SubmeterAllocationDTO newAllocation = new SubmeterAllocationDTO();
            newAllocation.setMeterId(meterId);
            newAllocation.setRoomId(roomId);
            newAllocation.setStartingDate(startingDate.trim());
            newAllocation.setEndingDate(endingDate != null ? endingDate.trim() : "");

            // TODO: Call your Repository/DAO to insert into database
            // allocationRepository.insert(newAllocation);

            toastMessageLiveData.setValue("Submeter allocated successfully!");
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Meter ID or Room ID format");
        }
    }

    // --- 2. SEARCH BY ID ---
    public void searchAllocationById(String idStr) {
        if (idStr == null || idStr.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter Allocation ID to search");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());

            // TODO: Replace with actual async/repository call
            SubmeterAllocationDTO found = fetchByIdFromDbOrApi(id);

            if (found != null) {
                allocationLiveData.setValue(found);
                toastMessageLiveData.setValue("Allocation found by ID!");
            } else {
                allocationLiveData.setValue(null);
                toastMessageLiveData.setValue("No allocation found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Allocation ID format");
        }
    }

    // --- 3. UPDATE ALLOCATION ---
    public void updateAllocation(SubmeterAllocationDTO currentAllocation, String meterIdStr, String roomIdStr, String startingDate, String endingDate) {
        if (currentAllocation == null) {
            toastMessageLiveData.setValue("No allocation selected for update");
            return;
        }

        if (meterIdStr == null || meterIdStr.trim().isEmpty() ||
                roomIdStr == null || roomIdStr.trim().isEmpty() ||
                startingDate == null || startingDate.trim().isEmpty()) {
            toastMessageLiveData.setValue("Meter ID, Room ID, and Starting Date cannot be empty");
            return;
        }

        try {
            int meterId = Integer.parseInt(meterIdStr.trim());
            int roomId = Integer.parseInt(roomIdStr.trim());

            currentAllocation.setMeterId(meterId);
            currentAllocation.setRoomId(roomId);
            currentAllocation.setStartingDate(startingDate.trim());
            currentAllocation.setEndingDate(endingDate != null ? endingDate.trim() : "");

            // TODO: Call your Repository/DAO to update database
            // allocationRepository.update(currentAllocation);

            toastMessageLiveData.setValue("Allocation updated successfully!");
            allocationLiveData.setValue(null); // Clear selection after update
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Meter ID or Room ID format");
        }
    }

    // --- Mock Fetch Method (Replace with your DAO/Repository) ---
    private SubmeterAllocationDTO fetchByIdFromDbOrApi(int id) {
        if (id == 1) {
            SubmeterAllocationDTO allocation = new SubmeterAllocationDTO();
            allocation.setId(1);
            allocation.setMeterId(101);
            allocation.setRoomId(202);
            allocation.setStartingDate("2026-01-01");
            allocation.setEndingDate("");
            return allocation;
        }
        return null;
    }
}