package com.submeter.presentation.viewmodel;

import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.RoomService;

public class RoomViewModel extends BaseViewModel {

    private final RoomService roomService;

    private RoomDTO room;

    public RoomViewModel(RoomService roomService) {
        this.roomService = roomService;
    }

    public RoomDTO getRoom() {
        return room;
    }

    public void loadRoom(String roomNumber) {

        setLoading(true);
        setErrorMessage(null);
        setOperationSuccess(false);

        room = null;

        try {
            OperationResult<RoomDTO> result =
                    roomService.findByRoomNumber(roomNumber);

            if (result.isSuccess()) {

                room = result.getData();

                setOperationSuccess(true);

            } else {

                setErrorMessage(result.getMessage());
                setOperationSuccess(false);
            }

        } catch (Exception e) {

            // Handle unexpected errors not converted by the service.
            setErrorMessage("An unexpected error occurred.");
            setOperationSuccess(false);

        } finally {

            setLoading(false);
            notifyStateChanged();
        }
    }
}