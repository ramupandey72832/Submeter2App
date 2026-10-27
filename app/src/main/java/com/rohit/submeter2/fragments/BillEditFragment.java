package com.rohit.submeter2.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rohit.submeter2.databinding.FragmentBillEditBinding;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.presentation.viewmodel.BaseViewModel;
import com.submeter.presentation.viewmodel.BillViewModel;
import com.submeter.service.ServiceManager;

import java.util.Locale;

public class BillEditFragment extends Fragment {

    private static final String ARG_BILL_DTO = "selected_bill_dto";

    private FragmentBillEditBinding binding;
    private BillViewModel viewModel;
    private BillDTO selectedBill;

    private final BaseViewModel.StateListener stateListener = () -> {
        if (getActivity() != null) {
            getActivity().runOnUiThread(this::handleViewModelState);
        }
    };

    public static BillEditFragment newInstance(BillDTO bill) {
        BillEditFragment fragment = new BillEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BILL_DTO, bill);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedBill = (BillDTO) getArguments().getSerializable(ARG_BILL_DTO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBillEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewModel();
        setupListeners();
        populateUi();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null) {
            viewModel.removeStateListener(stateListener);
        }
        binding = null;
    }

    //region Initialization & Setup

    private void initializeViewModel() {
        viewModel = new BillViewModel(
                ServiceManager.getBillingService(),
                ServiceManager.getMeterReadingService()
        );
        viewModel.addStateListener(stateListener);
    }

    private void setupListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> navigateBack());
        binding.btnSaveBillChanges.setOnClickListener(v -> saveBillChanges());

        TextWatcher calculationTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { recalculateTotals(); }
        };

        binding.etEditCurrentReading.addTextChangedListener(calculationTextWatcher);
        binding.etEditRatePerUnit.addTextChangedListener(calculationTextWatcher);
        binding.etEditFixedCharges.addTextChangedListener(calculationTextWatcher);
        binding.etEditExtraCharges.addTextChangedListener(calculationTextWatcher);
    }

    private void populateUi() {
        if (selectedBill == null) return;

        binding.tvReadOnlyBillId.setText(String.format(Locale.US, "Bill #%d", selectedBill.getId()));
        binding.tvReadOnlyTenant.setText(String.format(Locale.US, "%s (%s)", selectedBill.getTenantName(), selectedBill.getRoomNumber()));
        binding.tvReadOnlyMeter.setText(selectedBill.getMeterSerialNumber());
        binding.tvReadOnlyPrevReading.setText(String.format(Locale.US, "%.2f kWh", (double) selectedBill.getPreviousReading()));

        binding.etEditCurrentReading.setText(String.valueOf(selectedBill.getCurrentReading()));
        binding.etEditRatePerUnit.setText(String.valueOf(selectedBill.getRatePerUnit()));
        binding.etEditFixedCharges.setText(String.valueOf(selectedBill.getFixedCharge()));
        binding.etEditTotalAmount.setText(String.valueOf(selectedBill.getTotalAmount()));
        binding.etEditNote.setText(selectedBill.getNote());
        binding.etEditExtraCharges.setText(String.valueOf(selectedBill.getExtraCharge()));
    }

    //endregion

    //region Business Logic & Calculations

    private void recalculateTotals() {
        try {
            double currentReading = parseDoubleSafely(binding.etEditCurrentReading.getText().toString());
            double ratePerUnit = parseDoubleSafely(binding.etEditRatePerUnit.getText().toString());
            double fixedCharges = parseDoubleSafely(binding.etEditFixedCharges.getText().toString());
            double extraCharges = parseDoubleSafely(binding.etEditExtraCharges.getText().toString());
            double previousReading = selectedBill != null ? selectedBill.getPreviousReading() : 0.0;

            double consumedUnits = Math.max(0.0, currentReading - previousReading);
            double calculatedTotal = (consumedUnits * ratePerUnit) + fixedCharges + extraCharges;

            binding.etEditTotalAmount.setText(String.format(Locale.US, "%.2f", calculatedTotal));
        } catch (NumberFormatException ignored) {}
    }

    private void saveBillChanges() {
        if (selectedBill == null) return;

        String currentInput = binding.etEditCurrentReading.getText().toString().trim();
        String rateInput = binding.etEditRatePerUnit.getText().toString().trim();
        String fixedInput = binding.etEditFixedCharges.getText().toString().trim();
        String totalInput = binding.etEditTotalAmount.getText().toString().trim();
        String extraInput = binding.etEditExtraCharges.getText().toString().trim();
        String note = binding.etEditNote.getText().toString().trim();

        if (currentInput.isEmpty() || rateInput.isEmpty() || fixedInput.isEmpty() || totalInput.isEmpty()) {
            Toast.makeText(getContext(), "All computational metric fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        long previousReading = selectedBill.getPreviousReading();
        long currentReading;
        double ratePerUnit, fixedCharges, extraCharges, totalAmount;

        try {
            currentReading = Long.parseLong(currentInput);
            ratePerUnit = Double.parseDouble(rateInput);
            fixedCharges = Double.parseDouble(fixedInput);
            extraCharges = Double.parseDouble(extraInput);
            totalAmount = Double.parseDouble(totalInput);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numeric values.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentReading < previousReading) {
            Toast.makeText(getContext(), "Error: Current reading cannot drop below the previous reading value.", Toast.LENGTH_LONG).show();
            return;
        }

        viewModel.updateBill(selectedBill.getId(), currentReading, previousReading, ratePerUnit, fixedCharges, extraCharges, totalAmount, note);
    }

    //endregion

    //region UI Feedback & Navigation

    private void handleViewModelState() {
        if (viewModel.getErrorMessage() != null) {
            Toast.makeText(getContext(), viewModel.getErrorMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (viewModel.isOperationSuccess()) {
            Toast.makeText(getContext(), "Bill specifications adjusted cleanly.", Toast.LENGTH_SHORT).show();
            navigateBack();
        }
    }

    private void navigateBack() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    private double parseDoubleSafely(String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return 0.0;
        return Double.parseDouble(trimmed);
    }

    //endregion
}