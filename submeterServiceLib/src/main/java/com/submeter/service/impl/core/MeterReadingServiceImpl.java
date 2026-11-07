package com.submeter.service.impl.core;


import com.submeter.commonlib.dto.MeterReadingDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.MeterReadingDao;
import com.submeter.data.entities.MeterReading;
import com.submeter.service.MeterReadingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MeterReadingServiceImpl implements MeterReadingService {

    private static final String TAG = "MeterReadingServiceImpl";
    private final MeterReadingDao meterReadingDao;

    public MeterReadingServiceImpl(MeterReadingDao meterReadingDao) {
        this.meterReadingDao = meterReadingDao;
    }

    @Override
    public OperationResult<MeterReadingDTO> getLatestByMeterId(int meterId) {
        if (meterId <= 0) {
            return OperationResult.failure("Invalid Meter ID.");
        }

        try {
            Optional<MeterReading> optional = meterReadingDao.getLatestReadingByMeterId(meterId);
            if (optional.isEmpty()) {
                return OperationResult.failure("No meter readings found for Meter ID: " + meterId);
            }

            MeterReadingDTO dto = mapEntityToDto(optional.get());
            return OperationResult.success(dto, "Latest meter reading retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving latest reading: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<MeterReadingDTO> getById(int id) {
        if (id <= 0) {
            return OperationResult.failure("Invalid Reading ID.");
        }

        try {
            Optional<MeterReading> optional = meterReadingDao.getById(id);
            if (optional.isEmpty()) {
                return OperationResult.failure("Meter reading not found with ID: " + id);
            }

            MeterReadingDTO dto = mapEntityToDto(optional.get());
            return OperationResult.success(dto, "Meter reading retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving meter reading: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<MeterReadingDTO> getBySubmeterId(int meterId) {
        // Delegates or maps to getLatestByMeterId or returns the list/first item depending on service design
        return getLatestByMeterId(meterId);
    }

    @Override
    public OperationResult<Void> update(MeterReadingDTO meterReadingDTO) {
        if (meterReadingDTO == null || meterReadingDTO.getId() <= 0) {
            return OperationResult.failure("Valid reading details with ID are required for update.");
        }
        if (meterReadingDTO.getReadingValue() < 0) {
            return OperationResult.failure("Reading value cannot be negative.");
        }

        try {
            MeterReading reading = mapDtoToEntity(meterReadingDTO);
            boolean success = meterReadingDao.updateReading(reading);

            if (success) {
                return OperationResult.success(null, "Meter reading updated successfully!");
            } else {
                return OperationResult.failure("Failed to update meter reading.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to update meter reading: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> add(MeterReadingDTO meterReadingDTO) {
        if (meterReadingDTO == null) {
            return OperationResult.failure("Reading details cannot be null.");
        }
        if (meterReadingDTO.getMeterId() <= 0) {
            return OperationResult.failure("Valid Meter ID is required.");
        }
        if (meterReadingDTO.getReadingValue() < 0) {
            return OperationResult.failure("Reading value cannot be negative.");
        }
        if (meterReadingDTO.getDate() == null || meterReadingDTO.getDate().trim().isEmpty()) {
            return OperationResult.failure("Reading date is required.");
        }

        try {
            MeterReading reading = mapDtoToEntity(meterReadingDTO);
            int generatedId = meterReadingDao.insert(reading);

            if (generatedId > 0) {
                return OperationResult.success(null, "Meter reading added successfully!");
            } else {
                return OperationResult.failure("Failed to save meter reading.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to add meter reading: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> delete(int meterReadingId) {
        if (meterReadingId <= 0) {
            return OperationResult.failure("Valid Reading ID is required for deletion.");
        }

        try {
            boolean success = meterReadingDao.deleteReading(meterReadingId);
            if (success) {
                return OperationResult.success(null, "Meter reading deleted successfully!");
            } else {
                return OperationResult.failure("Failed to delete meter reading.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to delete meter reading: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<MeterReadingDTO>> getAll() {
        try {
            List<MeterReading> readings = meterReadingDao.getAllReadings();
            List<MeterReadingDTO> dtos = new ArrayList<>();

            if (readings != null) {
                for (MeterReading reading : readings) {
                    dtos.add(mapEntityToDto(reading));
                }
            }

            return OperationResult.success(dtos, "Meter readings retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Error retrieving meter readings list: " + e.getMessage());
        }
    }

    private MeterReadingDTO mapEntityToDto(MeterReading entity) {
        MeterReadingDTO dto = new MeterReadingDTO();
        dto.setId(entity.getId());
        dto.setMeterId(entity.getMeterId());
        dto.setReadingValue(entity.getReadingValue());
        dto.setDate(entity.getDate());
        dto.setImageUrlOrPath(entity.getImageUrlOrPath());
        return dto;
    }

    private MeterReading mapDtoToEntity(MeterReadingDTO dto) {
        MeterReading entity = new MeterReading();
        entity.setId(dto.getId());
        entity.setMeterId(dto.getMeterId());
        entity.setReadingValue(dto.getReadingValue());
        entity.setDate(dto.getDate() != null ? dto.getDate().trim() : "");
        entity.setImageUrlOrPath(dto.getImageUrlOrPath() != null ? dto.getImageUrlOrPath().trim() : "");
        return entity;
    }
}