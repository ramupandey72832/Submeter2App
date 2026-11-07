package com.submeter.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.RoomService;
import com.submeter.service.ServiceManager;
import com.submeter.service.TenancyService;
import com.submeter.service.TenantService;

import java.util.List;

public class TenancyViewModel extends ViewModel {

    private final TenancyService tenancyService = ServiceManager.getTenancyService();
    private final RoomService roomService = ServiceManager.getRoomService();
    private final TenantService tenantService = ServiceManager.getTenantService();

    private final MutableLiveData<TenancyDTO> tenancyLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

    public LiveData<TenancyDTO> getTenancyLiveData() {
        return tenancyLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    // --- 1. SAVE TENANCY (Using Room Number and Tenant Aadhaar Number) ---
    public void saveTenancy(String roomNumber, String tenantAadhaar, String startDate, String endDate) {
        if (roomNumber == null || roomNumber.trim().isEmpty() ||
                tenantAadhaar == null || tenantAadhaar.trim().isEmpty() ||
                startDate == null || startDate.trim().isEmpty()) {
            toastMessageLiveData.setValue("Room Number, Tenant Aadhaar, and Start Date are required");
            return;
        }

        try {
            String targetRoomNum = roomNumber.trim();
            String targetAadhaar = tenantAadhaar.trim();

            // 1. Resolve Room Number to ID via RoomService
            OperationResult<RoomDTO> roomResult = roomService.getByRoomNumber(targetRoomNum);
            if (!roomResult.isSuccess() || roomResult.getData() == null) {
                toastMessageLiveData.setValue("Room number '" + targetRoomNum + "' does not exist.");
                return;
            }
            int roomId = roomResult.getData().getId();

            // 2. Resolve Tenant Aadhaar to ID via TenantService (scanning all or using a direct lookup if available)
            OperationResult<List<TenantDTO>> tenantsResult = tenantService.getAll();
            if (!tenantsResult.isSuccess() || tenantsResult.getData() == null) {
                toastMessageLiveData.setValue("Failed to retrieve tenants for validation.");
                return;
            }

            TenantDTO matchedTenant = tenantsResult.getData().stream()
                    .filter(t -> t.getAadhaarNumber() != null && t.getAadhaarNumber().equalsIgnoreCase(targetAadhaar))
                    .findFirst()
                    .orElse(null);

            if (matchedTenant == null) {
                toastMessageLiveData.setValue("Tenant with Aadhaar '" + targetAadhaar + "' does not exist.");
                return;
            }
            int tenantId = matchedTenant.getId();

            // 3. Build Tenancy DTO
            TenancyDTO newTenancy = new TenancyDTO();
            newTenancy.setRoomId(roomId);
            newTenancy.setTenantId(tenantId);
            newTenancy.setStartDate(startDate.trim());
            newTenancy.setEndDate(endDate != null ? endDate.trim() : "");

            // 4. Save via TenancyService
            OperationResult<Void> result = tenancyService.add(newTenancy);
            if (result.isSuccess()) {
                toastMessageLiveData.setValue("Room allocated successfully!");
            } else {
                toastMessageLiveData.setValue(result.getMessage() != null ? result.getMessage() : "Failed to allocate room.");
            }

        } catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
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
            OperationResult<TenancyDTO> result = tenancyService.getActiveTenancyByRoomId(id); // Alternatively use a getById if exposed

            if (result.isSuccess() && result.getData() != null) {
                tenancyLiveData.setValue(result.getData());
                toastMessageLiveData.setValue("Tenancy allocation found!");
            } else {
                tenancyLiveData.setValue(null);
                toastMessageLiveData.setValue("No allocation found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            toastMessageLiveData.setValue("Invalid Tenancy ID format");
        } catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
        }
    }

    // --- 3. UPDATE TENANCY ---
    public void updateTenancy(TenancyDTO currentTenancy, String roomNumber, String tenantAadhaar, String startDate, String endDate) {
        if (currentTenancy == null) {
            toastMessageLiveData.setValue("No tenancy selected for update");
            return;
        }

        if (roomNumber == null || roomNumber.trim().isEmpty() ||
                tenantAadhaar == null || tenantAadhaar.trim().isEmpty() ||
                startDate == null || startDate.trim().isEmpty()) {
            toastMessageLiveData.setValue("Room Number, Tenant Aadhaar, and Start Date cannot be empty");
            return;
        }

        try {
            String targetRoomNum = roomNumber.trim();
            String targetAadhaar = tenantAadhaar.trim();

            // 1. Resolve Room Number to ID
            OperationResult<RoomDTO> roomResult = roomService.getByRoomNumber(targetRoomNum);
            if (!roomResult.isSuccess() || roomResult.getData() == null) {
                toastMessageLiveData.setValue("Room number '" + targetRoomNum + "' does not exist.");
                return;
            }
            int roomId = roomResult.getData().getId();

            // 2. Resolve Tenant Aadhaar to ID
            OperationResult<List<TenantDTO>> tenantsResult = tenantService.getAll();
            if (!tenantsResult.isSuccess() || tenantsResult.getData() == null) {
                toastMessageLiveData.setValue("Failed to retrieve tenants for validation.");
                return;
            }

            TenantDTO matchedTenant = tenantsResult.getData().stream()
                    .filter(t -> t.getAadhaarNumber() != null && t.getAadhaarNumber().equalsIgnoreCase(targetAadhaar))
                    .findFirst()
                    .orElse(null);

            if (matchedTenant == null) {
                toastMessageLiveData.setValue("Tenant with Aadhaar '" + targetAadhaar + "' does not exist.");
                return;
            }
            int tenantId = matchedTenant.getId();

            // 3. Update DTO fields
            currentTenancy.setRoomId(roomId);
            currentTenancy.setTenantId(tenantId);
            currentTenancy.setStartDate(startDate.trim());
            currentTenancy.setEndDate(endDate != null ? endDate.trim() : "");

            // 4. Update via Service
            OperationResult<Void> result = tenancyService.update(currentTenancy);
            if (result.isSuccess()) {
                toastMessageLiveData.setValue("Tenancy updated successfully!");
                tenancyLiveData.setValue(null); // Clear selection after update
            } else {
                toastMessageLiveData.setValue(result.getMessage() != null ? result.getMessage() : "Failed to update tenancy.");
            }
        } catch (Exception e) {
            toastMessageLiveData.setValue("Error: " + e.getMessage());
        }
    }
}