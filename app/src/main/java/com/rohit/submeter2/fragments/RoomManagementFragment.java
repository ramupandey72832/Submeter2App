package com.rohit.submeter2.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rohit.submeter2.databinding.FragmentRoomManagementBinding;
import com.submeter.commonlib.dto.RoomDTO;
import com.submeter.presentation.viewmodel.RoomManagementViewModel;

public class RoomManagementFragment extends Fragment {
    private static final String TAG = "RoomManagementFragment";
    private FragmentRoomManagementBinding binding;

    // Store currently selected RoomDTO instance
    private RoomDTO currentSelectedRoom = null;

    private RoomManagementViewModel viewModel;

    public RoomManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout using View Binding
        binding = FragmentRoomManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RoomManagementViewModel.class);

        // Set Click Listeners using binding references
        binding.btnSaveRoom.setOnClickListener(v -> saveRoom());
        binding.btnSearchById.setOnClickListener(v -> searchRoomById());
        binding.btnSearchByRoomNumber.setOnClickListener(v -> searchRoomByRoomNumber());
        binding.btnUpdateRoom.setOnClickListener(v -> updateRoom());

        // --- OBSERVE VIEWMODEL LIVEDATA ---

        viewModel.getRoomLiveData().observe(getViewLifecycleOwner(), room -> {
            currentSelectedRoom = room;
            if (room != null) {
                populateEditForm(room);
                Toast.makeText(getContext(), "Room found!", Toast.LENGTH_SHORT).show();
            } else {
                disableEditForm();
            }
        });

        viewModel.getToastMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Log.e(TAG, "Unable to save room: " + message);

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                // Clear input fields on successful actions
                if (message.contains("added successfully")) {
                    binding.etAddRoomNumber.setText("");
                    binding.etAddRoomType.setText("");
                } else if (message.contains("updated successfully")) {
                    binding.etSearchById.setText("");
                    binding.etSearchByRoomNumber.setText("");
                    disableEditForm();
                }
            }
        });
    }

    // --- 1. ADD ROOM HANDLER ---
    private void saveRoom() {
        String roomNum = binding.etAddRoomNumber.getText().toString().trim();
        String roomType = binding.etAddRoomType.getText().toString().trim();

        if (TextUtils.isEmpty(roomNum) || TextUtils.isEmpty(roomType)) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.addRoom(roomNum, roomType);
    }

    // --- 2A. SEARCH BY ID HANDLER ---
    private void searchRoomById() {
        String idStr = binding.etSearchById.getText().toString().trim();

        if (TextUtils.isEmpty(idStr)) {
            Toast.makeText(getContext(), "Enter Room ID to search", Toast.LENGTH_SHORT).show();
            return;
        }

        int roomId = Integer.parseInt(idStr);
        viewModel.searchRoomById(roomId);
    }

    // --- 2B. SEARCH BY ROOM NUMBER HANDLER ---
    private void searchRoomByRoomNumber() {
        String roomNum = binding.etSearchByRoomNumber.getText().toString().trim();

        if (TextUtils.isEmpty(roomNum)) {
            Toast.makeText(getContext(), "Enter Room Number to search", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.searchRoomByRoomNumber(roomNum);
    }

    // --- Helper to populate form fields upon successful search ---
    private void populateEditForm(RoomDTO room) {
        binding.etEditRoomId.setText(String.valueOf(room.getId()));
        binding.etEditRoomNumber.setText(room.getRoomNumber());
        binding.etEditRoomType.setText(room.getRoomType());

        // Keep ID disabled so primary key isn't mistakenly edited, enable editable fields
        binding.etEditRoomId.setEnabled(false);
        binding.etEditRoomNumber.setEnabled(true);
        binding.etEditRoomType.setEnabled(true);
        binding.btnUpdateRoom.setEnabled(true);
    }

    // --- 3. UPDATE ROOM HANDLER ---
    private void updateRoom() {
        if (currentSelectedRoom == null) return;

        String updatedNum = binding.etEditRoomNumber.getText().toString().trim();
        String updatedType = binding.etEditRoomType.getText().toString().trim();

        if (TextUtils.isEmpty(updatedNum) || TextUtils.isEmpty(updatedType)) {
            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSelectedRoom.setRoomNumber(updatedNum);
        currentSelectedRoom.setRoomType(updatedType);

        viewModel.updateRoom(currentSelectedRoom);
    }

    private void disableEditForm() {
        binding.etEditRoomId.setText("");
        binding.etEditRoomNumber.setText("");
        binding.etEditRoomType.setText("");
        binding.etEditRoomId.setEnabled(false);
        binding.etEditRoomNumber.setEnabled(false);
        binding.etEditRoomType.setEnabled(false);
        binding.btnUpdateRoom.setEnabled(false);
        currentSelectedRoom = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear reference to prevent memory leaks in fragments
    }
}