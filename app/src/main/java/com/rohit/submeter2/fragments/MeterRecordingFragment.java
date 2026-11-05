package com.rohit.submeter2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.android_ui_templete1.templates.nav_activity.bottom_nav_activity.BottomNavActivityConstant;
import com.application.baselibrary.ui.utils.NavigationUtils;
import com.rohit.submeter2.databinding.FragmentMeterRecordingBinding;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.presentation.viewmodel.MeterReadingUiState;
import com.submeter.presentation.viewmodel.MeterReadingViewModel;
import com.submeter.service.ServiceManager;

public class MeterRecordingFragment extends Fragment {

    private FragmentMeterRecordingBinding binding;
    private MeterReadingViewModel viewModel;
    private MeterReadingUiState currentLoadedState;

    public MeterRecordingFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMeterRecordingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new MeterReadingViewModel(
                ServiceManager.getRoomService(),
                ServiceManager.getMeterReadingService(),
                ServiceManager.getTenancyService(),
                ServiceManager.getSubmeterAllocationService(),
                ServiceManager.getTenantService(),
                ServiceManager.getSubmeterService()
        );

        viewModel.addStateListener(this::renderUiState);

        if (getArguments() != null && getArguments().containsKey("ARG_QR_DATA")) {
            binding.etRoomNumber.setText(getArguments().getString("ARG_QR_DATA"));
        }

        setInitialInputLockdown();

        binding.btnSearch.setOnClickListener(v -> {
            String roomNumber = binding.etRoomNumber.getText().toString().trim();
            viewModel.getLatestMeterReadingByRoomNumber(roomNumber);
        });

        binding.btnNext.setOnClickListener(v -> validateAndNavigate());
    }

    private void renderUiState() {
        if (getActivity() == null || binding == null) return;

        requireActivity().runOnUiThread(() -> {
            // 1. Loading State
            boolean isLoading = viewModel.isLoading();
            binding.btnSearch.setEnabled(!isLoading);
            if (isLoading) return;

            // 2. Error State
            String errorMsg = viewModel.getErrorMessage();
            if (errorMsg != null) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                viewModel.clearError(); // <-- CLEAR IT SO IT ONLY SHOWS ONCE
                setInitialInputLockdown();
                return;
            }

            // 3. Success State
            if (viewModel.isOperationSuccess() && viewModel.getUiState() != null) {
                this.currentLoadedState = viewModel.getUiState();

                binding.etMeterSerialNumber.setText(currentLoadedState.getSerialNumber());
                binding.etLastMeterReading.setText(String.valueOf(currentLoadedState.getPreviousReadingValue()));
                binding.etRatePerUnit.setText(String.valueOf(currentLoadedState.getDefaultRate()));
                binding.etFixCharge.setText(String.valueOf(currentLoadedState.getDefaultFixedCharge()));

                toggleInputs(true);
            }
        });
    }

    private void validateAndNavigate() {
        if (currentLoadedState == null) {
            Toast.makeText(getContext(), "Please search a room first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String roomNumber = binding.etRoomNumber.getText().toString().trim();
        String currentStr = binding.etCurrentMeterReading.getText().toString().trim();

        if (currentStr.isEmpty()) {
            binding.etCurrentMeterReading.setError("Enter current reading");
            return;
        }

        long currentReading = Long.parseLong(currentStr);
        long previousReading = currentLoadedState.getPreviousReadingValue();

        if (currentReading < previousReading) {
            binding.etCurrentMeterReading.setError("Cannot be lower than previous (" + previousReading + ")");
            return;
        }

        BillDTO billDTO = BillDTO.builder()
                .roomNumber(roomNumber)
                .tenantId(currentLoadedState.getTenantId())
                .tenantName(currentLoadedState.getTenantName())
                .meterSerialNumber(binding.etMeterSerialNumber.getText().toString().trim())
                .previousReadingId(currentLoadedState.getPreviousReadingId())
                .previousReading(previousReading)
                .currentReading(currentReading)
                .ratePerUnit(Double.parseDouble(binding.etRatePerUnit.getText().toString()))
                .fixedCharge(Double.parseDouble(binding.etFixCharge.getText().toString()))
                .build();

        Fragment targetFragment = BillCreateFragment.newInstance(billDTO);
        NavigationUtils.replaceFragmentWithBackStack(requireActivity(), targetFragment, BottomNavActivityConstant.MAIN_CONTAINER);
    }

    private void setInitialInputLockdown() {
        toggleInputs(false);
        this.currentLoadedState = null;
        binding.etMeterSerialNumber.setText("");
        binding.etLastMeterReading.setText("");
        binding.etCurrentMeterReading.setText("");
    }

    private void toggleInputs(boolean isEnabled) {
        binding.etRatePerUnit.setEnabled(isEnabled);
        binding.etFixCharge.setEnabled(isEnabled);
        binding.etExtraCharge.setEnabled(isEnabled);
        binding.etNote.setEnabled(isEnabled);
        binding.btnNext.setEnabled(isEnabled);
        binding.etCurrentMeterReading.setEnabled(isEnabled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null) {
            viewModel.removeStateListener(this::renderUiState);
        }
        binding = null;
    }
}