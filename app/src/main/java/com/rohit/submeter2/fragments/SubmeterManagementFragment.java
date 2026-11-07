package com.rohit.submeter2.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rohit.submeter2.databinding.FragmentSubmeterManagementBinding;
import com.submeter.commonlib.dto.SubmeterDTO;
import com.submeter.presentation.viewmodel.SubmeterManagementViewModel;

public class SubmeterManagementFragment extends Fragment {
    public static String TAG = "SubmeterManagementFragment";

    private SubmeterManagementViewModel viewModel;
    private FragmentSubmeterManagementBinding binding;

    private SubmeterDTO currentSelectedSubmeter = null;

    public SubmeterManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout using View Binding
        binding = FragmentSubmeterManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SubmeterManagementViewModel.class);

        // Click Listeners using binding references
        binding.btnSaveSubmeter.setOnClickListener(v ->
                viewModel.saveSubmeter(
                        binding.etAddMeterSerialNumber.getText().toString(),
                        binding.etAddInitialReading.getText().toString(),
                        binding.etAddNote.getText().toString()
                )
        );

        binding.btnSearchById.setOnClickListener(v ->
                viewModel.searchSubmeterById(binding.etSearchById.getText().toString())
        );

        binding.btnSearchBySerial.setOnClickListener(v ->
                viewModel.searchSubmeterBySerial(binding.etSearchBySerial.getText().toString())
        );

        binding.btnUpdateSubmeter.setOnClickListener(v ->
                viewModel.updateSubmeter(
                        currentSelectedSubmeter,
                        binding.etEditMeterSerialNumber.getText().toString(),
                        binding.etEditInitialReading.getText().toString(),
                        binding.etEditNote.getText().toString()
                )
        );

        // --- OBSERVE VIEWMODEL LIVEDATA ---

        viewModel.getSubmeterLiveData().observe(getViewLifecycleOwner(), submeter -> {
            currentSelectedSubmeter = submeter;
            if (submeter != null) {
                binding.etEditId.setText(String.valueOf(submeter.getId()));
                binding.etEditMeterSerialNumber.setText(submeter.getMeterSerialNumber());
                binding.etEditInitialReading.setText(String.valueOf(submeter.getInitialReading()));
                binding.etEditNote.setText(submeter.getNote());

                binding.etEditId.setEnabled(false); // ID remains uneditable
                binding.etEditMeterSerialNumber.setEnabled(true);
                binding.etEditInitialReading.setEnabled(true);
                binding.etEditNote.setEnabled(true);
                binding.btnUpdateSubmeter.setEnabled(true);
            } else {
                disableEditForm();
            }
        });

        viewModel.getToastMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Log.e(TAG, " " + message);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                // Clear fields on successful actions
                if (message.contains("added successfully")) {
                    binding.etAddMeterSerialNumber.setText("");
                    binding.etAddInitialReading.setText("");
                    binding.etAddNote.setText("");
                } else if (message.contains("updated successfully")) {
                    binding.etSearchById.setText("");
                    binding.etSearchBySerial.setText("");
                }
            }
        });
    }

    private void disableEditForm() {
        binding.etEditId.setText("");
        binding.etEditMeterSerialNumber.setText("");
        binding.etEditInitialReading.setText("");
        binding.etEditNote.setText("");
        binding.etEditId.setEnabled(false);
        binding.etEditMeterSerialNumber.setEnabled(false);
        binding.etEditInitialReading.setEnabled(false);
        binding.etEditNote.setEnabled(false);
        binding.btnUpdateSubmeter.setEnabled(false);
        currentSelectedSubmeter = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear reference to prevent memory leaks in fragments
    }
}