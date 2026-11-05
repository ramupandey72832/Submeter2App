package com.submeter.service.impl.core;

import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.RoomDao;
import com.submeter.data.entities.Room;
import com.submeter.service.RoomService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoomServiceImpl implements RoomService {
    private static final String TAG = "RoomServiceImpl";

    private final RoomDao roomDao;

    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Override
    public OperationResult<Void> addRoom(RoomDTO roomDTO) {
        if (roomDTO == null) {
            return OperationResult.failure("Room details cannot be null.");
        }
        if (roomDTO.getRoomNumber() == null || roomDTO.getRoomNumber().isBlank()) {
            return OperationResult.failure("Please enter a room number.");
        }
        if (roomDTO.getRoomType() == null || roomDTO.getRoomType().isBlank()) {
            return OperationResult.failure("Please enter a room type.");
        }

        try {
            Room room = new Room();
            room.setRoomNumber(roomDTO.getRoomNumber().trim());
            room.setRoomType(roomDTO.getRoomType().trim());

            boolean success = roomDao.insertRoom(room);
            if (success) {
                return OperationResult.success(null, "Room added successfully!");
            } else {
                return OperationResult.failure("Failed to add room to database.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to save room: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<RoomDTO> getRoomById(int roomId) {
        if (roomId <= 0) {
            return OperationResult.failure("Invalid Room ID.");
        }

        try {
            Optional<Room> roomOptional = roomDao.getRoomById(roomId);

            if (roomOptional.isEmpty()) {
                return OperationResult.failure("Room not found with ID: " + roomId);
            }

            Room room = roomOptional.get();
            RoomDTO roomDTO = new RoomDTO(
                    room.getId(),
                    room.getRoomNumber(),
                    room.getRoomType()
            );

            return OperationResult.success(roomDTO, "Room found successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Unable to retrieve room details: " + e.getMessage());
        }
    }

    @Override
    public OperationResult<RoomDTO> getByRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.isBlank()) {
            return OperationResult.failure("Please enter a room number.");
        }

        try {
            Optional<Room> roomOptional = roomDao.getRoomByNumber(roomNumber.trim());

            if (roomOptional.isEmpty()) {
                return OperationResult.failure("Room not found.");
            }

            Room room = roomOptional.get();
            RoomDTO roomDTO = new RoomDTO(
                    room.getId(),
                    room.getRoomNumber(),
                    room.getRoomType()
            );

            return OperationResult.success(roomDTO, "Room found successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Unable to retrieve room details.");
        }
    }

    @Override
    public OperationResult<Void> update(RoomDTO roomDTO) {
        if (roomDTO == null) {
            return OperationResult.failure("Room details cannot be null.");
        }
        if (roomDTO.getRoomNumber() == null || roomDTO.getRoomNumber().isBlank()) {
            return OperationResult.failure("Room number cannot be empty.");
        }
        if (roomDTO.getRoomType() == null || roomDTO.getRoomType().isBlank()) {
            return OperationResult.failure("Room type cannot be empty.");
        }

        try {
            Room room = new Room();
            room.setId(roomDTO.getId());
            room.setRoomNumber(roomDTO.getRoomNumber().trim());
            room.setRoomType(roomDTO.getRoomType().trim());

            boolean success = roomDao.updateRoom(room);
            if (success) {
                return OperationResult.success(null, "Room updated successfully!");
            } else {
                return OperationResult.failure("Failed to update room.");
            }
        } catch (Exception e) {
            return OperationResult.failure("Unable to update room: " + e.getMessage());
        }
    }

    // Additional helper matching your DAO's getAllRooms capability
    public OperationResult<List<RoomDTO>> getAllRooms() {
        try {
            List<Room> rooms = roomDao.getAllRooms();
            List<RoomDTO> roomDTOs = rooms.stream()
                    .map(room -> new RoomDTO(room.getId(), room.getRoomNumber(), room.getRoomType()))
                    .collect(Collectors.toList());

            return OperationResult.success(roomDTOs, "Rooms retrieved successfully.");
        } catch (Exception e) {
            return OperationResult.failure("Unable to retrieve rooms list: " + e.getMessage());
        }
    }
}