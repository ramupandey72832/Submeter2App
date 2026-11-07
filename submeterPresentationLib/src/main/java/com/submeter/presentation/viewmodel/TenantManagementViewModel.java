package com.submeter.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.ServiceManager;
import com.submeter.service.TenantService;

import java.util.List;

public class TenantManagementViewModel extends ViewModel {

    private final TenantService tenantService = ServiceManager.getTenantService();

    private final MutableLiveData<TenantDTO> tenantLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

    public LiveData<TenantDTO> getTenantLiveData() {
        return tenantLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    // --- 1. SAVE TENANT ---
    public void saveTenant(String name, String phone, String aadhaar, String address) {
        if (name == null || name.trim().isEmpty() ||
                aadhaar == null || aadhaar.trim().isEmpty()) {
            toastMessageLiveData.setValue("Name and Identification Number are required");
            return;
        }

        try {
            TenantDTO newTenant = new TenantDTO();
            newTenant.setName(name.trim());
            newTenant.setPhoneNumber(phone != null ? phone.trim() : "");
            newTenant.setAadhaarNumber(aadhaar.trim());
            newTenant.setAddress(address != null ? address.trim() : "");

            OperationResult<Void> result = tenantService.add(newTenant);
            if (result.isSuccess()) {
                toastMessageLiveData.setValue("Tenant added successfully!");
            } else {
                toastMessageLiveData.setValue(result.getMessage() != null ? result.getMessage() : "Failed to add tenant.");
            }
        } catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
        }
    }

    // --- 2A. SEARCH BY ID ---
    public void searchTenantById(String idStr) {
        if (idStr == null || idStr.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter Tenant ID to search");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());
            OperationResult<TenantDTO> result = tenantService.getByTenantId(id);

            if (result.isSuccess() && result.getData() != null) {
                tenantLiveData.setValue(result.getData());
                toastMessageLiveData.setValue("Tenant found by ID!");
            } else {
                tenantLiveData.setValue(null);
                toastMessageLiveData.setValue("No tenant found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Tenant ID format");
        } catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
        }
    }

    // --- 2B. SEARCH BY PHONE NUMBER ---
    public void searchTenantByAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter aadhaar Number to search");
            return;
        }

        try {

            OperationResult<TenantDTO> result = tenantService.getByTenantAadhaar(aadhaar.trim());

            if (result.isSuccess() && result.getData() != null) {
                tenantLiveData.setValue(result.getData());
                toastMessageLiveData.setValue("Tenant found by aadhaar!");
            } else {
                tenantLiveData.setValue(null);
                toastMessageLiveData.setValue("No tenant found with aadhaar: " + aadhaar);
            }
        }  catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
        }
    }

    // --- 3. UPDATE TENANT ---
    public void updateTenant(TenantDTO currentTenant, String name, String phone, String aadhaar, String address) {
        if (currentTenant == null) {
            toastMessageLiveData.setValue("No tenant selected for update");
            return;
        }

        if (name == null || name.trim().isEmpty() ||
                aadhaar == null || aadhaar.trim().isEmpty()) {
            toastMessageLiveData.setValue("Name and Identification Number cannot be empty");
            return;
        }

        try {
            currentTenant.setName(name.trim());
            currentTenant.setPhoneNumber(phone != null ? phone.trim() : "");
            currentTenant.setAadhaarNumber(aadhaar.trim());
            currentTenant.setAddress(address != null ? address.trim() : "");

            OperationResult<Void> result = tenantService.update(currentTenant);
            if (result.isSuccess()) {
                toastMessageLiveData.setValue("Tenant updated successfully!");
                tenantLiveData.setValue(null); // Clear selection after update
            } else {
                toastMessageLiveData.setValue(result.getMessage() != null ? result.getMessage() : "Failed to update tenant.");
            }
        } catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
        }
    }
}