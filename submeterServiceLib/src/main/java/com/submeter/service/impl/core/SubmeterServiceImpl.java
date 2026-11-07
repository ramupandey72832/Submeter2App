package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.SubmeterDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.SubmeterDao;
import com.submeter.data.entities.Submeter;
import com.submeter.service.SubmeterService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubmeterServiceImpl implements SubmeterService {

    private static final String TAG = "SubmeterServiceImpl";
    private final SubmeterDao submeterDao;

    public SubmeterServiceImpl(SubmeterDao submeterDao) {
        this.submeterDao = submeterDao;
    }

    @Override
    public OperationResult<Void> add(SubmeterDTO submeterDTO) {
        if (submeterDTO == null) {
            return OperationResult.failure("Submeter details cannot be null.");
        }
        if (submeterDTO.getMeterSerialNumber() == null || submeterDTO.getMeterSerialNumber().isBlank()) {
            return OperationResult.failure("Please enter a meter serial number.");
        }

        try {
            Submeter submeter = new Submeter();
            submeter.setMeterSerialNumber(submeterDTO.getMeterSerialNumber().trim());
            submeter.setInitialReading(submeterDTO.getInitialReading());
            submeter.setNote(submeterDTO.getNote());

            int generatedId = submeterDao.insertSubmeter(submeter);
            if (generatedId > 0) {
                return OperationResult.success(null, "Submeter added successfully!");
            } else {
                return OperationResult.failure("Failed to add submeter to database.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to save submeter: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<Void> updateById(SubmeterDTO submeterDTO) {
        if (submeterDTO == null) {
            return OperationResult.failure("Submeter details cannot be null.");
        }
        if (submeterDTO.getId() <= 0) {
            return OperationResult.failure("Invalid Submeter ID for update.");
        }
        if (submeterDTO.getMeterSerialNumber() == null || submeterDTO.getMeterSerialNumber().isBlank()) {
            return OperationResult.failure("Meter serial number cannot be empty.");
        }

        try {
            Submeter submeter = new Submeter();
            submeter.setId(submeterDTO.getId());
            submeter.setMeterSerialNumber(submeterDTO.getMeterSerialNumber().trim());
            submeter.setInitialReading(submeterDTO.getInitialReading());
            submeter.setNote(submeterDTO.getNote());

            boolean success = submeterDao.updateSubmeter(submeter);
            if (success) {
                return OperationResult.success(null, "Submeter updated successfully!");
            } else {
                return OperationResult.failure("Failed to update submeter.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to update submeter: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<SubmeterDTO> getById(int id) {
        if (id <= 0) {
            return OperationResult.failure("Invalid Submeter ID.");
        }

        try {
            Optional<Submeter> submeterOptional = submeterDao.getSubmeterById(id);
            if (submeterOptional.isEmpty()) {
                return OperationResult.failure("Submeter not found with ID: " + id);
            }

            Submeter sub = submeterOptional.get();
            SubmeterDTO dto = new SubmeterDTO(
                    sub.getId(),
                    sub.getMeterSerialNumber(),
                    sub.getInitialReading(),
                    sub.getNote()
            );

            return OperationResult.success(dto, "Submeter found successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Unable to retrieve submeter details: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<SubmeterDTO> getBySerialNumber(String serial) {
        if (serial == null || serial.isBlank()) {
            return OperationResult.failure("Meter serial number cannot be empty.");
        }

        try {
            Optional<Submeter> submeterOptional = submeterDao.getSubmeterBySerialNumber(serial);
            if (submeterOptional.isEmpty()) {
                return OperationResult.failure("Submeter not found with Serial: " + serial);
            }

            Submeter sub = submeterOptional.get();
            SubmeterDTO dto = new SubmeterDTO(
                    sub.getId(),
                    sub.getMeterSerialNumber(),
                    sub.getInitialReading(),
                    sub.getNote()
            );

            return OperationResult.success(dto, "Submeter found successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Unable to retrieve submeter details: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<List<SubmeterDTO>> getAll() {
        try {
            List<Submeter> submeters = submeterDao.getAllSubmeters();
            List<SubmeterDTO> dtos = submeters.stream()
                    .map(sub -> new SubmeterDTO(
                            sub.getId(),
                            sub.getMeterSerialNumber(),
                            sub.getInitialReading(),
                            sub.getNote()
                    ))
                    .collect(Collectors.toList());

            return OperationResult.success(dtos, "Submeters retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Unable to retrieve submeters list: " + e.getMessage());
        }
    }
}
