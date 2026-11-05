package com.rohit.submeter2.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rohit.submeter2.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

// Import your auto-generated View Binding class based on your XML file name:
// fragment_tenancy.xml -> FragmentTenancyBinding
import com.rohit.submeter2.databinding.FragmentTenancyBinding;
import com.submeter.commonlib.dto.TenancyDTO;
import com.submeter.presentation.viewmodel.TenancyViewModel;

public class TenancyFragment extends Fragment {

    private TenancyViewModel viewModel;
    private FragmentTenancyBinding binding;

    private TenancyDTO currentSelectedTenancy = null;

    public TenancyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTenancyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TenancyViewModel.class);

        // --- Click Listeners (Using View Binding) ---
        binding.btnSaveTenancy.setOnClickListener(v ->
                viewModel.saveTenancy(
                        binding.etAddRoomNumber.getText().toString(),
                        binding.etAddTenantAadhaar.getText().toString(),
                        binding.etAddStartDate.getText().toString(),
                        binding.etAddEndDate.getText().toString()
                )
        );

        binding.btnSearchById.setOnClickListener(v ->
                viewModel.searchTenancyById(binding.etSearchById.getText().toString())
        );

        binding.btnUpdateTenancy.setOnClickListener(v ->
                viewModel.updateTenancy(
                        currentSelectedTenancy,
                        binding.etEditRoomNumber.getText().toString(),
                        binding.etEditTenantAadhaar.getText().toString(),
                        binding.etEditStartDate.getText().toString(),
                        binding.etEditEndDate.getText().toString()
                )
        );

        // --- OBSERVE VIEWMODEL LIVEDATA ---

        viewModel.getTenancyLiveData().observe(getViewLifecycleOwner(), tenancy -> {
            currentSelectedTenancy = tenancy;
            if (tenancy != null) {
                binding.etEditId.setText(String.valueOf(tenancy.getId()));
                binding.etEditRoomNumber.setText(String.valueOf(tenancy.getRoomId()));
                binding.etEditTenantAadhaar.setText(String.valueOf(tenancy.getTenantId()));
                binding.etEditStartDate.setText(tenancy.getStartDate());
                binding.etEditEndDate.setText(tenancy.getEndDate());

                binding.etEditId.setEnabled(false); // ID remains uneditable
                binding.etEditRoomNumber.setEnabled(true);
                binding.etEditTenantAadhaar.setEnabled(true);
                binding.etEditStartDate.setEnabled(true);
                binding.etEditEndDate.setEnabled(true);
                binding.btnUpdateTenancy.setEnabled(true);
            } else {
                disableEditForm();
            }
        });

        viewModel.getToastMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                // Clear input fields on successful actions
                if (message.contains("allocated successfully")) {
                    binding.etAddRoomNumber.setText("");
                    binding.etAddTenantAadhaar.setText("");
                    binding.etAddStartDate.setText("");
                    binding.etAddEndDate.setText("");
                } else if (message.contains("updated successfully")) {
                    binding.etSearchById.setText("");
                }
            }
        });
    }

    private void disableEditForm() {
        binding.etEditId.setText("");
        binding.etEditRoomNumber.setText("");
        binding.etEditTenantAadhaar.setText("");
        binding.etEditStartDate.setText("");
        binding.etEditEndDate.setText("");
        binding.etEditId.setEnabled(false);
        binding.etEditRoomNumber.setEnabled(false);
        binding.etEditTenantAadhaar.setEnabled(false);
        binding.etEditStartDate.setEnabled(false);
        binding.etEditEndDate.setEnabled(false);
        binding.btnUpdateTenancy.setEnabled(false);
        currentSelectedTenancy = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}