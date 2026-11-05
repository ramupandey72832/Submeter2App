package com.submeter.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.TenantDTO;

public class TenantManagementViewModel extends ViewModel {

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
            toastMessageLiveData.setValue("Name, and Aadhaar are required");
            return;
        }

        TenantDTO newTenant = new TenantDTO();
        newTenant.setName(name.trim());
        newTenant.setPhoneNumber(phone != null ? phone.trim() : "");
        newTenant.setAadhaarNumber(aadhaar.trim());
        newTenant.setAddress(address != null ? address.trim() : "");

        // TODO: Call your Repository/DAO to insert into database
        // tenantRepository.insert(newTenant);

        toastMessageLiveData.setValue("Tenant added successfully!");
    }

    // --- 2A. SEARCH BY ID ---
    public void searchTenantById(String idStr) {
        if (idStr == null || idStr.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter Tenant ID to search");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());

            // TODO: Replace with actual async/repository call
            TenantDTO found = fetchByIdFromDbOrApi(id);

            if (found != null) {
                tenantLiveData.setValue(found);
                toastMessageLiveData.setValue("Tenant found by ID!");
            } else {
                tenantLiveData.setValue(null);
                toastMessageLiveData.setValue("No tenant found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Tenant ID format");
        }
    }

    // --- 2B. SEARCH BY PHONE NUMBER ---
    public void searchTenantByAadhaar(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            toastMessageLiveData.setValue("Enter Phone Number to search");
            return;
        }

        // TODO: Replace with actual async/repository call
        TenantDTO found = fetchByPhoneFromDbOrApi(phone.trim());

        if (found != null) {
            tenantLiveData.setValue(found);
            toastMessageLiveData.setValue("Tenant found by Phone Number!");
        } else {
            tenantLiveData.setValue(null);
            toastMessageLiveData.setValue("No tenant found with Phone: " + phone);
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
            toastMessageLiveData.setValue("Name, Phone, and Aadhaar cannot be empty");
            return;
        }

        currentTenant.setName(name.trim());
        currentTenant.setPhoneNumber(phone.trim());
        currentTenant.setAadhaarNumber(aadhaar.trim());
        currentTenant.setAddress(address != null ? address.trim() : "");

        // TODO: Call your Repository/DAO to update database
        // tenantRepository.update(currentTenant);

        toastMessageLiveData.setValue("Tenant updated successfully!");
        tenantLiveData.setValue(null); // Clear selection after update
    }

    // --- Mock Fetch Methods (Replace with your DAO/Repository) ---
    private TenantDTO fetchByIdFromDbOrApi(int id) {
        if (id == 1) {
            TenantDTO tenant = new TenantDTO();
            tenant.setId(1);
            tenant.setName("John Doe");
            tenant.setPhoneNumber("9876543210");
            tenant.setAadhaarNumber("[Aadhaar Redacted]");
            tenant.setAddress("Room 101, Main Building");
            return tenant;
        }
        return null;
    }

    private TenantDTO fetchByPhoneFromDbOrApi(String phone) {
        if (phone.equals("9876543210")) {
            TenantDTO tenant = new TenantDTO();
            tenant.setId(1);
            tenant.setName("John Doe");
            tenant.setPhoneNumber("9876543210");
            tenant.setAadhaarNumber("[Aadhaar Redacted]");
            tenant.setAddress("Room 101, Main Building");
            return tenant;
        }
        return null;
    }
}