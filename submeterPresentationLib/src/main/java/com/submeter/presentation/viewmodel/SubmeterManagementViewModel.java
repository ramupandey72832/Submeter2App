package com.submeter.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.SubmeterDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.ServiceManager;
import com.submeter.service.SubmeterService;

import java.util.List;

public class SubmeterManagementViewModel extends ViewModel {

    private final SubmeterService submeterService;

    // LiveData channels for UI state observation
    private final MutableLiveData<SubmeterDTO> submeterLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

    public SubmeterManagementViewModel(){
        submeterService = ServiceManager.getSubmeterService();
    }

    public LiveData<SubmeterDTO> getSubmeterLiveData() {
        return submeterLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    // --- 1. SAVE SUBMETER ---
    public void saveSubmeter(String serialNumber, String readingStr, String note) {
        if (serialNumber == null || serialNumber.trim().isEmpty() ||
                readingStr == null || readingStr.trim().isEmpty()) {
            toastMessageLiveData.setValue("Serial Number and Initial Reading are required");
            return;
        }

        try {
            long initialReading = Long.parseLong(readingStr.trim());
            SubmeterDTO newSubmeter = new SubmeterDTO();
            newSubmeter.setMeterSerialNumber(serialNumber.trim());
            newSubmeter.setInitialReading(initialReading);
            newSubmeter.setNote(note != null ? note.trim() : "");

            OperationResult<Void> result = submeterService.add(newSubmeter);
            if (result.isSuccess()) {
                toastMessageLiveData.setValue(result.getMessage());
            } else {
                toastMessageLiveData.setValue(result.getMessage());
            }
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Initial Reading format");
        }
    }

    // --- 2A. SEARCH BY ID ---
    public void searchSubmeterById(String idStr) {
        if (idStr == null || idStr.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter Submeter ID to search");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());
            OperationResult<SubmeterDTO> result = submeterService.getById(id);

            if (result.isSuccess() && result.getData() != null) {
                submeterLiveData.setValue(result.getData());
                toastMessageLiveData.setValue("Submeter found by ID!");
            } else {
                submeterLiveData.setValue(null);
                toastMessageLiveData.setValue("No submeter found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Submeter ID format");
        }
    }

    // --- 2B. SEARCH BY SERIAL NUMBER ---
    public void searchSubmeterBySerial(String serial) {
        if (serial == null || serial.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter Serial Number to search");
            return;
        }


        OperationResult<SubmeterDTO> result = submeterService.getBySerialNumber(serial);

        if (result.isSuccess() && result.getData() != null) {
            submeterLiveData.setValue(result.getData());
            toastMessageLiveData.setValue("Submeter found by Serial!");
        } else {
            submeterLiveData.setValue(null);
            toastMessageLiveData.setValue("No submeter found with Serial: " + serial);
        }
    }

    // --- 3. UPDATE SUBMETER ---
    public void updateSubmeter(SubmeterDTO currentSubmeter, String updatedSerial, String updatedReadingStr, String updatedNote) {
        if (currentSubmeter == null) {
            toastMessageLiveData.setValue("No submeter selected for update");
            return;
        }

        if (updatedSerial == null || updatedSerial.trim().isEmpty() ||
                updatedReadingStr == null || updatedReadingStr.trim().isEmpty()) {
            toastMessageLiveData.setValue("Serial Number and Reading cannot be empty");
            return;
        }

        try {
            long reading = Long.parseLong(updatedReadingStr.trim());
            currentSubmeter.setMeterSerialNumber(updatedSerial.trim());
            currentSubmeter.setInitialReading(reading);
            currentSubmeter.setNote(updatedNote != null ? updatedNote.trim() : "");

            OperationResult<Void> result = submeterService.updateById(currentSubmeter);
            if (result.isSuccess()) {
                toastMessageLiveData.setValue(result.getMessage());
                submeterLiveData.setValue(null); // Clear selection after update
            } else {
                toastMessageLiveData.setValue(result.getMessage());
            }
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Initial Reading format");
        }
    }
}