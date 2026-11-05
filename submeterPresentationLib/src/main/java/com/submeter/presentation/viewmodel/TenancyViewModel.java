package com.submeter.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.TenancyDTO;

public class TenancyViewModel extends ViewModel {

    private final MutableLiveData<TenancyDTO> tenancyLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

    public LiveData<TenancyDTO> getTenancyLiveData() {
        return tenancyLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    // --- 1. SAVE TENANCY ---
    public void saveTenancy(String roomIdStr, String tenantIdStr, String startDate, String endDate) {
        if (roomIdStr == null || roomIdStr.trim().isEmpty() ||
                tenantIdStr == null || tenantIdStr.trim().isEmpty() ||
                startDate == null || startDate.trim().isEmpty()) {
            toastMessageLiveData.setValue("Room ID, Tenant ID, and Start Date are required");
            return;
        }

        try {
            int roomId = Integer.parseInt(roomIdStr.trim());
            int tenantId = Integer.parseInt(tenantIdStr.trim());

            TenancyDTO newTenancy = new TenancyDTO();
            newTenancy.setRoomId(roomId);
            newTenancy.setTenantId(tenantId);
            newTenancy.setStartDate(startDate.trim());
            newTenancy.setEndDate(endDate != null ? endDate.trim() : "");

            // TODO: Call your Repository/DAO to insert into database
            // tenancyRepository.insert(newTenancy);

            toastMessageLiveData.setValue("Room allocated successfully!");
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Room ID or Tenant ID format");
        }
    }

    // --- 2. SEARCH BY ID ---
    public void searchTenancyById(String idStr) {
        if (idStr == null || idStr.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter Tenancy ID to search");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());

            // TODO: Replace with actual async/repository call
            TenancyDTO found = fetchByIdFromDbOrApi(id);

            if (found != null) {
                tenancyLiveData.setValue(found);
                toastMessageLiveData.setValue("Tenancy allocation found!");
            } else {
                tenancyLiveData.setValue(null);
                toastMessageLiveData.setValue("No allocation found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Tenancy ID format");
        }
    }

    // --- 3. UPDATE TENANCY ---
    public void updateTenancy(TenancyDTO currentTenancy, String roomIdStr, String tenantIdStr, String startDate, String endDate) {
        if (currentTenancy == null) {
            toastMessageLiveData.setValue("No tenancy selected for update");
            return;
        }

        if (roomIdStr == null || roomIdStr.trim().isEmpty() ||
                tenantIdStr == null || tenantIdStr.trim().isEmpty() ||
                startDate == null || startDate.trim().isEmpty()) {
            toastMessageLiveData.setValue("Room ID, Tenant ID, and Start Date cannot be empty");
            return;
        }

        try {
            int roomId = Integer.parseInt(roomIdStr.trim());
            int tenantId = Integer.parseInt(tenantIdStr.trim());

            currentTenancy.setRoomId(roomId);
            currentTenancy.setTenantId(tenantId);
            currentTenancy.setStartDate(startDate.trim());
            currentTenancy.setEndDate(endDate != null ? endDate.trim() : "");

            // TODO: Call your Repository/DAO to update database
            // tenancyRepository.update(currentTenancy);

            toastMessageLiveData.setValue("Tenancy updated successfully!");
            tenancyLiveData.setValue(null); // Clear selection after update
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Room ID or Tenant ID format");
        }
    }

    // --- Mock Fetch Method (Replace with your DAO/Repository) ---
    private TenancyDTO fetchByIdFromDbOrApi(int id) {
        if (id == 1) {
            TenancyDTO tenancy = new TenancyDTO();
            tenancy.setId(1);
            tenancy.setRoomId(101);
            tenancy.setTenantId(501);
            tenancy.setStartDate("2026-01-01");
            tenancy.setEndDate("");
            return tenancy;
        }
        return null;
    }
}