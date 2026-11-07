package com.submeter.service.impl.core;


import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.TenancyDao;
import com.submeter.data.entities.Tenancy;
import com.submeter.service.TenancyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TenancyServiceImpl implements TenancyService {

    private static final String TAG = "TenancyServiceImpl";
    private final TenancyDao tenancyDao;

    public TenancyServiceImpl(TenancyDao tenancyDao) {
        this.tenancyDao = tenancyDao;
    }

    @Override
    public OperationResult<Void> add(TenancyDTO tenancyDTO) {
        if (tenancyDTO == null) {
            return OperationResult.failure("Tenancy details cannot be null.");
        }
        if (tenancyDTO.getRoomId() <= 0) {
            return OperationResult.failure("Valid Room ID is required.");
        }
        if (tenancyDTO.getTenantId() <= 0) {
            return OperationResult.failure("Valid Tenant ID is required.");
        }
        if (tenancyDTO.getStartDate() == null || tenancyDTO.getStartDate().trim().isEmpty()) {
            return OperationResult.failure("Start date is required.");
        }

        try {
            // Optional: Ensure no other active tenancy exists for this room before adding a new one
            Optional<Tenancy> activeTenancy = tenancyDao.getActiveTenancyByRoomId(tenancyDTO.getRoomId());
            if (activeTenancy.isPresent()) {
                return OperationResult.failure("This room already has an active tenancy.");
            }

            Tenancy tenancy = mapDtoToEntity(tenancyDTO);
            boolean success = tenancyDao.add(tenancy);

            if (success) {
                return OperationResult.success(null, "Tenancy added successfully!");
            } else {
                return OperationResult.failure("Failed to save tenancy.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to add tenancy: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<TenancyDTO> getActiveTenancyByRoomId(int roomId) {
        if (roomId <= 0) {
            return OperationResult.failure("Invalid Room ID.");
        }

        try {
            Optional<Tenancy> optional = tenancyDao.getActiveTenancyByRoomId(roomId);
            if (optional.isEmpty()) {
                return OperationResult.failure("No active tenancy found for Room ID: " + roomId);
            }

            TenancyDTO dto = mapEntityToDto(optional.get());
            return OperationResult.success(dto, "Active tenancy retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving active tenancy: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<TenancyDTO> getActiveTenancyByRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return OperationResult.failure("Room number cannot be empty.");
        }

        try {
            Optional<Tenancy> optional = tenancyDao.getActiveTenancyByRoomNumber(roomNumber.trim());
            if (optional.isEmpty()) {
                return OperationResult.failure("No active tenancy found for room number: " + roomNumber);
            }

            TenancyDTO dto = mapEntityToDto(optional.get());
            return OperationResult.success(dto, "Active tenancy retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving active tenancy: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> update(TenancyDTO tenancyDTO) {
        if (tenancyDTO == null || tenancyDTO.getId() <= 0) {
            return OperationResult.failure("Valid tenancy details with ID are required for update.");
        }

        try {
            Tenancy tenancy = mapDtoToEntity(tenancyDTO);
            boolean success = tenancyDao.update(tenancy);

            if (success) {
                return OperationResult.success(null, "Tenancy updated successfully!");
            } else {
                return OperationResult.failure("Failed to update tenancy.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to update tenancy: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<TenancyDTO> getById(int tenancyId) {
        if (tenancyId <= 0) {
            return OperationResult.failure("Invalid Tenancy ID.");
        }

        try {
            Optional<Tenancy> optional = tenancyDao.getById(tenancyId);
            if (optional.isEmpty()) {
                return OperationResult.failure("Tenancy not found with ID: " + tenancyId);
            }

            TenancyDTO dto = mapEntityToDto(optional.get());
            return OperationResult.success(dto, "Tenancy retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving tenancy: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<TenancyDTO>> getByRoomId(int roomId) {
        if (roomId <= 0) {
            return OperationResult.failure("Invalid Room ID.");
        }

        try {
            List<Tenancy> tenancies = tenancyDao.getByRoomId(roomId);
            List<TenancyDTO> dtos = new ArrayList<>();

            if (tenancies != null) {
                for (Tenancy tenancy : tenancies) {
                    dtos.add(mapEntityToDto(tenancy));
                }
            }

            return OperationResult.success(dtos, "Tenancies retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving tenancies: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<TenancyDTO>> getByRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return OperationResult.failure("Room number cannot be empty.");
        }

        try {
            List<Tenancy> tenancies = tenancyDao.getByRoomNumber(roomNumber.trim());
            List<TenancyDTO> dtos = new ArrayList<>();

            if (tenancies != null) {
                for (Tenancy tenancy : tenancies) {
                    dtos.add(mapEntityToDto(tenancy));
                }
            }

            return OperationResult.success(dtos, "Tenancies retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving tenancies: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> endTenancy(TenancyDTO tenancyDTO) {
        if (tenancyDTO == null || tenancyDTO.getId() <= 0) {
            return OperationResult.failure("Valid tenancy details with ID are required to end tenancy.");
        }
        if (tenancyDTO.getEndDate() == null || tenancyDTO.getEndDate().trim().isEmpty()) {
            return OperationResult.failure("End date is required to close a tenancy.");
        }

        try {
            Tenancy tenancy = mapDtoToEntity(tenancyDTO);
            boolean success = tenancyDao.endTenancy(tenancy);

            if (success) {
                return OperationResult.success(null, "Tenancy ended successfully!");
            } else {
                return OperationResult.failure("Failed to end tenancy.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to end tenancy: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<TenancyDTO>> getAll() {
        try {
            List<Tenancy> tenancies = tenancyDao.getAll();
            List<TenancyDTO> dtos = new ArrayList<>();

            if (tenancies != null) {
                for (Tenancy tenancy : tenancies) {
                    dtos.add(mapEntityToDto(tenancy));
                }
            }

            return OperationResult.success(dtos, "All tenancies retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving tenancies list: " + e.getMessage());
        }
    }

    private TenancyDTO mapEntityToDto(Tenancy entity) {
        TenancyDTO dto = new TenancyDTO();
        dto.setId(entity.getId());
        dto.setRoomId(entity.getRoomId());
        dto.setTenantId(entity.getTenantId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        return dto;
    }

    private Tenancy mapDtoToEntity(TenancyDTO dto) {
        Tenancy entity = new Tenancy();
        entity.setId(dto.getId());
        entity.setRoomId(dto.getRoomId());
        entity.setTenantId(dto.getTenantId());
        entity.setStartDate(dto.getStartDate() != null ? dto.getStartDate().trim() : "");
        entity.setEndDate(dto.getEndDate() != null ? dto.getEndDate().trim() : "");
        return entity;
    }
}