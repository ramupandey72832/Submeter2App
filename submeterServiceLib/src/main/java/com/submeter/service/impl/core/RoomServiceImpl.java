package com.submeter.service.impl.core;


import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.data.dao.RoomDao;

import com.submeter.data.entities.Room;
import com.submeter.service.RoomService;

import java.util.Optional;

public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;

    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
    }


    public OperationResult<RoomDTO> findByRoomNumber(String roomNumber) {

        if (roomNumber == null || roomNumber.isBlank()) {
            return OperationResult.failure(
                    "Please enter a room number."
            );
        }

        try {
            Optional<Room> roomOptional =
                    roomDao.getRoomByNumber(roomNumber.trim());

            if (roomOptional.isEmpty()) {
                return OperationResult.failure(
                        "Room not found."
                );
            }

            Room room = roomOptional.get();

            RoomDTO roomDTO = new RoomDTO(
                    room.getId(),
                    room.getRoomNumber(),
                    room.getRoomType()
            );

            return OperationResult.success(
                    roomDTO,
                    "Room found successfully."
            );

        } catch (Exception e) {
            // Log the exception using your application's logger.
            return OperationResult.failure(
                    "Unable to retrieve room details."
            );
        }
    }

    void add(String roomNumber, String roomType){}
    void getById(int roomId){}
    void updateById(int roomId, String roomNumber, String roomType){}
    void getAll(){}
}
