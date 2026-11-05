package com.submeter.service;

import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.commonlib.response.OperationResult;

public interface RoomService {
    public OperationResult<RoomDTO> getByRoomNumber(
            String roomNumber);
    public OperationResult<Void> addRoom(RoomDTO roomDTO);
    public OperationResult<RoomDTO> getRoomById(int roomId);
    public OperationResult<Void> update(RoomDTO roomDTO);
}
