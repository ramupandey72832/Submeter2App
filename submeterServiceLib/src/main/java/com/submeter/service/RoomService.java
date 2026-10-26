package com.submeter.service;

import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.commonlib.response.OperationResult;

public interface RoomService {
    public OperationResult<RoomDTO> findByRoomNumber(
            String roomNumber);
}
