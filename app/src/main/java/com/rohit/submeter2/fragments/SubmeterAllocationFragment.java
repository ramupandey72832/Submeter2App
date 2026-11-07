package com.rohit.submeter2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rohit.submeter2.databinding.FragmentSubmeterAllocationBinding;
import com.submeter.commonlib.dto.SubmeterAllocationDTO;
import com.submeter.presentation.viewmodel.SubmeterAllocationViewModel;

public class SubmeterAllocationFragment extends Fragment {

    private SubmeterAllocationViewModel viewModel;

    // View Binding variable
    private FragmentSubmeterAllocationBinding binding;

    private SubmeterAllocationDTO currentSelectedAllocation = null;

    public SubmeterAllocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentSubmeterAllocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(SubmeterAllocationViewModel.class);

        // --- Click Listeners (Using View Binding directly) ---
        binding.btnSaveAllocation.setOnClickListener(v ->
                viewModel.saveAllocation(
                        binding.etAddMeterSerialNumber.getText().toString(),
                        binding.etAddRoomNumber.getText().toString(),
                        binding.etAddStartingDate.getText().toString(),
                        binding.etAddEndingDate.getText().toString()
                )
        );

        binding.btnSearchById.setOnClickListener(v ->
                viewModel.searchAllocationById(binding.etSearchById.getText().toString())
        );

        binding.btnUpdateAllocation.setOnClickListener(v ->
                viewModel.updateAllocation(
                        currentSelectedAllocation,
                        binding.etEditMeterSerialNumber.getText().toString(),
                        binding.etEditRoomNumber.getText().toString(),
                        binding.etEditStartingDate.getText().toString(),
                        binding.etEditEndingDate.getText().toString()
                )
        );

        // --- OBSERVE VIEWMODEL LIVEDATA ---

        viewModel.getAllocationLiveData().observe(getViewLifecycleOwner(), allocation -> {
            currentSelectedAllocation = allocation;
            if (allocation != null) {
                binding.etEditId.setText(String.valueOf(allocation.getId()));
                binding.etEditMeterSerialNumber.setText(String.valueOf(allocation.getMeterSerialNumber()));
                binding.etEditRoomNumber.setText(String.valueOf(allocation.getRoomNumber()));
                binding.etEditStartingDate.setText(allocation.getStartingDate());
                binding.etEditEndingDate.setText(allocation.getEndingDate());

                binding.etEditId.setEnabled(false); // ID remains uneditable
                binding.etEditMeterSerialNumber.setEnabled(true);
                binding.etEditRoomNumber.setEnabled(true);
                binding.etEditStartingDate.setEnabled(true);
                binding.etEditEndingDate.setEnabled(true);
                binding.btnUpdateAllocation.setEnabled(true);
            } else {
                disableEditForm();
            }
        });

        viewModel.getToastMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                // Clear input fields on successful actions
                if (message.contains("allocated successfully")) {
                    binding.etAddMeterSerialNumber.setText("");
                    binding.etAddRoomNumber.setText("");
                    binding.etAddStartingDate.setText("");
                    binding.etAddEndingDate.setText("");
                } else if (message.contains("updated successfully")) {
                    binding.etSearchById.setText("");
                }
            }
        });
    }

    private void disableEditForm() {
        binding.etEditId.setText("");
        binding.etEditMeterSerialNumber.setText("");
        binding.etEditRoomNumber.setText("");
        binding.etEditStartingDate.setText("");
        binding.etEditEndingDate.setText("");
        binding.etEditId.setEnabled(false);
        binding.etEditMeterSerialNumber.setEnabled(false);
        binding.etEditRoomNumber.setEnabled(false);
        binding.etEditStartingDate.setEnabled(false);
        binding.etEditEndingDate.setEnabled(false);
        binding.btnUpdateAllocation.setEnabled(false);
        currentSelectedAllocation = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Prevent memory leaks in Fragments by setting binding to null
        binding = null;
    }
}