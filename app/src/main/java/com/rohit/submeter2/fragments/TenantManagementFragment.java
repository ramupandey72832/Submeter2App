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

import com.rohit.submeter2.databinding.FragmentTenantManagementBinding;
import com.submeter.commonlib.dto.TenantDTO;
import com.submeter.presentation.viewmodel.TenantManagementViewModel;

public class TenantManagementFragment extends Fragment {

    private TenantManagementViewModel viewModel;
    private FragmentTenantManagementBinding binding;

    private TenantDTO currentSelectedTenant = null;

    public TenantManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout using View Binding
        binding = FragmentTenantManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(TenantManagementViewModel.class);

        // Click Listeners using binding references
        binding.btnSaveTenant.setOnClickListener(v ->
                viewModel.saveTenant(
                        binding.etAddName.getText().toString(),
                        binding.etAddPhoneNumber.getText().toString(),
                        binding.etAddAadhaarNumber.getText().toString(),
                        binding.etAddAddress.getText().toString()
                )
        );

        binding.btnSearchById.setOnClickListener(v ->
                viewModel.searchTenantById(binding.etSearchById.getText().toString())
        );

        binding.btnSearchByAadhaar.setOnClickListener(v ->
                viewModel.searchTenantByAadhaar(binding.etSearchByAadhaar.getText().toString())
        );

        binding.btnUpdateTenant.setOnClickListener(v ->
                viewModel.updateTenant(
                        currentSelectedTenant,
                        binding.etEditName.getText().toString(),
                        binding.etEditPhoneNumber.getText().toString(),
                        binding.etEditAadhaarNumber.getText().toString(),
                        binding.etEditAddress.getText().toString()
                )
        );

        // --- OBSERVE VIEWMODEL LIVEDATA ---

        viewModel.getTenantLiveData().observe(getViewLifecycleOwner(), tenant -> {
            currentSelectedTenant = tenant;
            if (tenant != null) {
                binding.etEditId.setText(String.valueOf(tenant.getId()));
                binding.etEditName.setText(tenant.getName());
                binding.etEditPhoneNumber.setText(tenant.getPhoneNumber());
                binding.etEditAadhaarNumber.setText(tenant.getAadhaarNumber());
                binding.etEditAddress.setText(tenant.getAddress());

                binding.etEditId.setEnabled(false); // ID remains uneditable
                binding.etEditName.setEnabled(true);
                binding.etEditPhoneNumber.setEnabled(true);
                binding.etEditAadhaarNumber.setEnabled(true);
                binding.etEditAddress.setEnabled(true);
                binding.btnUpdateTenant.setEnabled(true);
            } else {
                disableEditForm();
            }
        });

        viewModel.getToastMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                // Clear input fields on successful actions
                if (message.contains("added successfully")) {
                    binding.etAddName.setText("");
                    binding.etAddPhoneNumber.setText("");
                    binding.etAddAadhaarNumber.setText("");
                    binding.etAddAddress.setText("");
                } else if (message.contains("updated successfully")) {
                    binding.etSearchById.setText("");
                    binding.etSearchByAadhaar.setText("");
                }
            }
        });
    }

    private void disableEditForm() {
        binding.etEditId.setText("");
        binding.etEditName.setText("");
        binding.etEditPhoneNumber.setText("");
        binding.etEditAadhaarNumber.setText("");
        binding.etEditAddress.setText("");
        binding.etEditId.setEnabled(false);
        binding.etEditName.setEnabled(false);
        binding.etEditPhoneNumber.setEnabled(false);
        binding.etEditAadhaarNumber.setEnabled(false);
        binding.etEditAddress.setEnabled(false);
        binding.btnUpdateTenant.setEnabled(false);
        currentSelectedTenant = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear reference to prevent memory leaks in fragments
    }
}