package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.SubmeterAllocationDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.SubmeterAllocationDao;
import com.submeter.data.entities.SubmeterAllocation;
import com.submeter.service.SubmeterAllocationService;

import java.util.List;
import java.util.Optional;

public class SubmeterAllocationServiceImpl implements SubmeterAllocationService {

    private static final String TAG = "SubmeterAllocationServiceImpl";
    private final SubmeterAllocationDao allocationDao;

    public SubmeterAllocationServiceImpl(SubmeterAllocationDao allocationDao) {
        this.allocationDao = allocationDao;
    }

    @Override
    public OperationResult<SubmeterAllocationDTO> getActiveMeterByRoomId(int roomId) {
        if (roomId <= 0) {
            return OperationResult.failure("Invalid Room ID.");
        }

        try {
            List<SubmeterAllocation> activeAllocations = allocationDao.getActiveAllocationsByRoomId(roomId);
            if (activeAllocations == null || activeAllocations.isEmpty()) {
                return OperationResult.failure("No active submeter allocation found for this room.");
            }

            // Return the first active allocation (or handle multiple if required by your UI logic)
            SubmeterAllocation allocation = activeAllocations.get(0);
            SubmeterAllocationDTO dto = mapEntityToDto(allocation);

            return OperationResult.success(dto, "Active submeter found for room.");
        } catch (Exception e) {
            return OperationResult.failure("Error fetching active meter: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<SubmeterAllocationDTO> getActiveMeterByMeterId(int meterId) {
        if (meterId <= 0) {
            return OperationResult.failure("Invalid Meter ID.");
        }

        try {
            List<SubmeterAllocation> activeAllocations = allocationDao.getActiveAllocationsByMeterId(meterId);
            if (activeAllocations == null || activeAllocations.isEmpty()) {
                return OperationResult.failure("This meter is not currently active in any room.");
            }

            SubmeterAllocation allocation = activeAllocations.get(0);
            SubmeterAllocationDTO dto = mapEntityToDto(allocation);

            return OperationResult.success(dto, "Active allocation found for meter.");
        } catch (Exception e) {
            return OperationResult.failure("Error fetching active allocation: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> add(SubmeterAllocationDTO dto) {
        if (dto == null) {
            return OperationResult.failure("Allocation details cannot be null.");
        }
        if (dto.getMeterId() <= 0 || dto.getRoomId() <= 0) {
            return OperationResult.failure("Valid Meter ID and Room ID are required.");
        }
        if (dto.getStartingDate() == null || dto.getStartingDate().isBlank()) {
            return OperationResult.failure("Starting date is required.");
        }

        try {
            SubmeterAllocation allocation = new SubmeterAllocation();
            allocation.setMeterId(dto.getMeterId());
            allocation.setRoomId(dto.getRoomId());
            allocation.setStartingDate(dto.getStartingDate().trim());
            allocation.setEndingDate(dto.getEndingDate() != null ? dto.getEndingDate().trim() : "");

            int generatedId = allocationDao.insertAllocation(allocation);
            if (generatedId > 0) {
                return OperationResult.success(null, "Submeter allocated successfully!");
            } else {
                return OperationResult.failure("Failed to save submeter allocation.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to allocate submeter: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<SubmeterAllocationDTO> getById(int id) {
        if (id <= 0) {
            return OperationResult.failure("Invalid Allocation ID.");
        }

        try {
            Optional<SubmeterAllocation> optional = allocationDao.getAllocationById(id);
            if (optional.isEmpty()) {
                return OperationResult.failure("Allocation not found with ID: " + id);
            }

            SubmeterAllocationDTO dto = mapEntityToDto(optional.get());
            return OperationResult.success(dto, "Allocation retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Unable to retrieve allocation: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> update(SubmeterAllocationDTO dto) {
        if (dto == null || dto.getId() <= 0) {
            return OperationResult.failure("Valid allocation details with ID are required for update.");
        }

        try {
            SubmeterAllocation allocation = new SubmeterAllocation();
            allocation.setId(dto.getId());
            allocation.setMeterId(dto.getMeterId());
            allocation.setRoomId(dto.getRoomId());
            allocation.setStartingDate(dto.getStartingDate() != null ? dto.getStartingDate().trim() : "");
            allocation.setEndingDate(dto.getEndingDate() != null ? dto.getEndingDate().trim() : "");

            boolean success = allocationDao.updateAllocation(allocation);
            if (success) {
                return OperationResult.success(null, "Allocation updated successfully!");
            } else {
                return OperationResult.failure("Failed to update allocation.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to update allocation: " + e.getMessage());
        }
    }

    private SubmeterAllocationDTO mapEntityToDto(SubmeterAllocation entity) {
        SubmeterAllocationDTO dto = new SubmeterAllocationDTO();
        dto.setId(entity.getId());
        dto.setMeterId(entity.getMeterId());
        dto.setRoomId(entity.getRoomId());
        dto.setStartingDate(entity.getStartingDate());
        dto.setEndingDate(entity.getEndingDate());
        return dto;
    }
}