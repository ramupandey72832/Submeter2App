package com.rohit.submeter2.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.android_ui_templete1.templates.nav_activity.bottom_nav_activity.BottomNavActivityConstant;

import com.application.baselibrary.ui.utils.NavigationUtils;
import com.rohit.submeter2.databinding.FragmentMeterRecordingBinding;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.presentation.viewmodel.MeterReadingViewModel;
import com.submeter.service.ServiceManager;
import com.submeter.presentation.viewmodel.MeterReadingUiState;
import com.submeter.service.ServiceManager;

public class MeterRecordingFragment extends Fragment {

    private FragmentMeterRecordingBinding binding;
    private MeterReadingViewModel viewModel;
    private MeterReadingUiState currentLoadedState;

    public MeterRecordingFragment() {
        // Required empty public constructor
    }

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
        // Ensure UI manipulations are posted back onto the Main Thread safely
        requireActivity().runOnUiThread(() -> {
            // A. Manage Loading Indicator Overlay
            if (viewModel.isLoading()) {
                binding.btnSearch.setEnabled(false);
                // If you have a ProgressBar widget: binding.progressBar.setVisibility(View.VISIBLE);
                return;
            }
            binding.btnSearch.setEnabled(true);

            // B. Manage Operational Errors
            if (viewModel.getErrorMessage() != null) {
                Toast.makeText(getContext(), viewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
                setInitialInputLockdown();
                return;
            }

            // C. Populated Loaded Metrics
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
            Toast.makeText(getContext(), "Please scan or look up a room record first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String roomNumber = binding.etRoomNumber.getText().toString().trim();
        String submeterSerialNumber = binding.etMeterSerialNumber.getText().toString().trim();
        String currentStr = binding.etCurrentMeterReading.getText().toString().trim();
        String fixedStr = binding.etFixCharge.getText().toString().trim();
        String rateStr = binding.etRatePerUnit.getText().toString().trim();
        String extraStr = binding.etExtraCharge.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();

        if (roomNumber.isEmpty() || submeterSerialNumber.isEmpty() || currentStr.isEmpty() || fixedStr.isEmpty() || rateStr.isEmpty()) {
            Toast.makeText(getContext(), "Please populate all fields completely.", Toast.LENGTH_SHORT).show();
            return;
        }

        long currentMeterReading = Long.parseLong(currentStr);
        long previousMeterReading = currentLoadedState.getPreviousReadingValue();
        double fixedCharge = Double.parseDouble(fixedStr);
        double ratePerUnit = Double.parseDouble(rateStr);
        double extraCharge = extraStr.isEmpty() ? 0.0 : Double.parseDouble(extraStr);

        if (currentMeterReading < previousMeterReading) {
            binding.etCurrentMeterReading.setError("Current reading cannot be lower than the previous reading (" + previousMeterReading + ")!");
            return;
        }

        // DTO Instantiation via the clean Lombok interface patterns
        BillDTO billDTO = BillDTO.builder()
                .roomNumber(roomNumber)
                .tenantId(currentLoadedState.getTenantId())
                .tenantName(currentLoadedState.getTenantName())
                .meterSerialNumber(submeterSerialNumber)
                .previousReadingId(currentLoadedState.getPreviousReadingId())
                .previousReading(previousMeterReading)
                .currentReadingId(0) // Will be generated post insert query execution
                .currentReading(currentMeterReading)
                .ratePerUnit(ratePerUnit)
                .fixedCharge(fixedCharge)
                .extraCharge(extraCharge)
                .note(note)
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

        // Form Fields that are strictly Read-Only for user reference display
        binding.etMeterSerialNumber.setEnabled(false);
        binding.etLastMeterReading.setEnabled(false);
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
