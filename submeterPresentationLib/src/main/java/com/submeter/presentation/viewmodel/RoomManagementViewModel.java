package com.submeter.presentation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.commonlib.response.OperationResult;
import com.submeter.service.RoomService;
import com.submeter.service.ServiceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomManagementViewModel extends ViewModel {

    private final RoomService roomService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MutableLiveData<RoomDTO> roomLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

    // Constructor injection for RoomService
    public RoomManagementViewModel(RoomService roomService) {
        this.roomService = roomService;
    }

    public RoomManagementViewModel() {
        this.roomService = ServiceManager.getRoomService();
    }

    public LiveData<RoomDTO> getRoomLiveData() {
        return roomLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    // --- Asynchronous Add Room ---
    public void addRoom(String roomNumber, String roomType) {
        executorService.execute(() -> {
            try {
                RoomDTO roomDTO = new RoomDTO(0, roomNumber, roomType);
                OperationResult<Void> result = roomService.addRoom(roomDTO);

                if (result != null && result.isSuccess()) {
                    toastMessageLiveData.postValue(result.getMessage() != null ? result.getMessage() : "Room added successfully!");
                } else {
                    String errorMsg = (result != null && result.getMessage() != null) ? result.getMessage() : "Failed to add room.";
                    toastMessageLiveData.postValue(errorMsg);
                }
            } catch (Exception e) {
                toastMessageLiveData.postValue("Failed to add room: " + e.getMessage());
            }
        });
    }

    // --- Asynchronous Search By ID ---
    public void searchRoomById(int id) {
        executorService.execute(() -> {
            try {
                OperationResult<RoomDTO> result = roomService.getRoomById(id);

                if (result != null && result.isSuccess()) {
                    RoomDTO room = result.getData();
                    roomLiveData.postValue(room);
                    if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                        toastMessageLiveData.postValue(result.getMessage());
                    }
                } else {
                    roomLiveData.postValue(null);
                    String errorMsg = (result != null && result.getMessage() != null) ? result.getMessage() : "No room found with ID: " + id;
                    toastMessageLiveData.postValue(errorMsg);
                }
            } catch (Exception e) {
                roomLiveData.postValue(null);
                toastMessageLiveData.postValue("Error searching room: " + e.getMessage());
            }
        });
    }

    // --- Asynchronous Search By Room Number ---
    public void searchRoomByRoomNumber(String roomNumber) {
        executorService.execute(() -> {
            try {
                OperationResult<RoomDTO> result = roomService.getByRoomNumber(roomNumber);

                if (result != null && result.isSuccess()) {
                    RoomDTO room = result.getData();
                    roomLiveData.postValue(room);
                    if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                        toastMessageLiveData.postValue(result.getMessage());
                    }
                } else {
                    roomLiveData.postValue(null);
                    String errorMsg = (result != null && result.getMessage() != null) ? result.getMessage() : "No room found with Number: " + roomNumber;
                    toastMessageLiveData.postValue(errorMsg);
                }
            } catch (Exception e) {
                roomLiveData.postValue(null);
                toastMessageLiveData.postValue("Error searching room: " + e.getMessage());
            }
        });
    }

    // --- Asynchronous Update Room ---
    public void updateRoom(RoomDTO roomDTO) {
        if (roomDTO == null) return;

        executorService.execute(() -> {
            try {
                OperationResult<Void> result = roomService.update(roomDTO);

                if (result != null && result.isSuccess()) {
                    toastMessageLiveData.postValue(result.getMessage() != null ? result.getMessage() : "Room updated successfully!");
                } else {
                    String errorMsg = (result != null && result.getMessage() != null) ? result.getMessage() : "Failed to update room.";
                    toastMessageLiveData.postValue(errorMsg);
                }
            } catch (Exception e) {
                toastMessageLiveData.postValue("Failed to update room: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}