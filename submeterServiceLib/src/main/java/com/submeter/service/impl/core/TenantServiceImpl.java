package com.submeter.service.impl.core;


import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.TenantDao;
import com.submeter.data.entities.Tenant;
import com.submeter.service.TenantService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TenantServiceImpl implements TenantService {

    private static final String TAG = "TenantServiceImpl";
    private final TenantDao tenantDao;

    public TenantServiceImpl(TenantDao tenantDao) {
        this.tenantDao = tenantDao;
    }

    @Override
    public OperationResult<TenantDTO> getByTenantId(int tenantId) {
        if (tenantId <= 0) {
            return OperationResult.failure("Invalid Tenant ID.");
        }

        try {
            Optional<Tenant> optional = tenantDao.getById(tenantId);
            if (optional.isEmpty()) {
                return OperationResult.failure("Tenant not found with ID: " + tenantId);
            }

            TenantDTO dto = mapEntityToDto(optional.get());
            return OperationResult.success(dto, "Tenant retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving tenant: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<TenantDTO> getByTenantAadhaar(String aadhaar) {

        if (aadhaar== null || aadhaar.trim().isEmpty()) {
            return OperationResult.failure("Invalid Tenant aadhaar.");
        }

        try {
            Optional<Tenant> optional = tenantDao.getByAadhaar(aadhaar);
            if (optional.isEmpty()) {
                return OperationResult.failure("Tenant not found with Aadhaar: " + aadhaar);
            }

            TenantDTO dto = mapEntityToDto(optional.get());
            return OperationResult.success(dto, "Tenant retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving tenant: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> add(TenantDTO tenantDTO) {
        if (tenantDTO == null) {
            return OperationResult.failure("Tenant details cannot be null.");
        }
        if (tenantDTO.getName() == null || tenantDTO.getName().trim().isEmpty()) {
            return OperationResult.failure("Tenant name is required.");
        }

        try {
            // Check if a tenant with the same Aadhaar already exists (if Aadhaar is provided)
            String aadhaar = tenantDTO.getAadhaarNumber();
            if (aadhaar != null && !aadhaar.trim().isEmpty()) {
                Optional<Tenant> existing = tenantDao.getByAadhaar(aadhaar.trim());
                if (existing.isPresent()) {
                    return OperationResult.failure("A tenant with this identity number already exists.");
                }
            }

            Tenant tenant = mapDtoToEntity(tenantDTO);
            boolean success = tenantDao.insert(tenant);

            if (success) {
                return OperationResult.success(null, "Tenant added successfully!");
            } else {
                return OperationResult.failure("Failed to save tenant.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to add tenant: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<TenantDTO>> getAll() {
        try {
            List<Tenant> tenants = tenantDao.getAll();
            List<TenantDTO> dtos = new ArrayList<>();

            if (tenants != null) {
                for (Tenant tenant : tenants) {
                    dtos.add(mapEntityToDto(tenant));
                }
            }

            return OperationResult.success(dtos, "Tenants retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving tenants list: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> update(TenantDTO tenantDTO) {
        if (tenantDTO == null || tenantDTO.getId() <= 0) {
            return OperationResult.failure("Valid tenant details with ID are required for update.");
        }
        if (tenantDTO.getName() == null || tenantDTO.getName().trim().isEmpty()) {
            return OperationResult.failure("Tenant name cannot be empty.");
        }

        try {
            Tenant tenant = mapDtoToEntity(tenantDTO);
            boolean success = tenantDao.update(tenant);

            if (success) {
                return OperationResult.success(null, "Tenant updated successfully!");
            } else {
                return OperationResult.failure("Failed to update tenant.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to update tenant: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> delete(TenantDTO tenantDTO) {
        if (tenantDTO == null || tenantDTO.getId() <= 0) {
            return OperationResult.failure("Valid tenant details with ID are required for deletion.");
        }

        try {
            boolean success = tenantDao.delete(tenantDTO.getId());
            if (success) {
                return OperationResult.success(null, "Tenant deleted successfully!");
            } else {
                return OperationResult.failure("Failed to delete tenant.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to delete tenant: " + e.getMessage());
        }
    }

    private TenantDTO mapEntityToDto(Tenant entity) {
        TenantDTO dto = new TenantDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setAadhaarNumber(entity.getAadhaarNumber());
        dto.setAddress(entity.getAddress());
        return dto;
    }

    private Tenant mapDtoToEntity(TenantDTO dto) {
        Tenant entity = new Tenant();
        entity.setId(dto.getId());
        entity.setName(dto.getName() != null ? dto.getName().trim() : "");
        entity.setPhoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber().trim() : "");
        entity.setAadhaarNumber(dto.getAadhaarNumber() != null ? dto.getAadhaarNumber().trim() : "");
        entity.setAddress(dto.getAddress() != null ? dto.getAddress().trim() : "");
        return entity;
    }
}