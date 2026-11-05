package com.submeter.presentation.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.SubmeterDTO;

public class SubmeterManagementViewModel extends ViewModel {

    // LiveData channels for UI state observation
    private final MutableLiveData<SubmeterDTO> submeterLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

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

            // TODO: Call your Repository/DAO to insert into database (e.g., Room DB async)
            // submeterRepository.insert(newSubmeter);

            toastMessageLiveData.setValue("Submeter added successfully!");
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

            // TODO: Replace with actual async/repository call
            SubmeterDTO found = fetchByIdFromDbOrApi(id);

            if (found != null) {
                submeterLiveData.setValue(found);
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

        // TODO: Replace with actual async/repository call
        SubmeterDTO found = fetchBySerialFromDbOrApi(serial.trim());

        if (found != null) {
            submeterLiveData.setValue(found);
            toastMessageLiveData.setValue("Submeter found by Serial Number!");
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

            // TODO: Call your Repository/DAO to update database
            // submeterRepository.update(currentSubmeter);

            toastMessageLiveData.setValue("Submeter updated successfully!");
            submeterLiveData.setValue(null); // Clear selection after update
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Initial Reading format");
        }
    }

    // --- Mock Fetch Methods (Replace with your DAO/Repository) ---
    private SubmeterDTO fetchByIdFromDbOrApi(int id) {
        if (id == 1) {
            SubmeterDTO submeter = new SubmeterDTO();
            submeter.setId(1);
            submeter.setMeterSerialNumber("SN-1001");
            submeter.setInitialReading(150L);
            submeter.setNote("Main Hall Meter");
            return submeter;
        }
        return null;
    }

    private SubmeterDTO fetchBySerialFromDbOrApi(String serial) {
        if (serial.equalsIgnoreCase("SN-1001")) {
            SubmeterDTO submeter = new SubmeterDTO();
            submeter.setId(1);
            submeter.setMeterSerialNumber("SN-1001");
            submeter.setInitialReading(150L);
            submeter.setNote("Main Hall Meter");
            return submeter;
        }
        return null;
    }
}