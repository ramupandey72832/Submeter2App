package com.rohit.submeter2.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.android_ui_templete1.templates.nav_activity.bottom_nav_activity.BottomNavActivityConstant;
import com.application.baselibrary.ui.utils.NavigationUtils;
import com.application.baselibrary.ui.utils.ToastMessage;
import com.example.rrallutilitylib.util.DateUtils;
import com.rohit.submeter2.databinding.FragmentBillCreateBinding;
import com.rohit.submeter2.fragments.mpin.MpinVerificationDialogFragment;
import com.rohit.submeter2.utils.UiDateUtils;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.presentation.viewmodel.BillViewModel;
import com.submeter.service.ServiceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Locale;

public class BillCreateFragment extends Fragment implements MpinVerificationDialogFragment.MpinVerificationListener {

    private static final String ARG_BILL_DETAILS_DTO = "arg_bill_details_dto";

    private FragmentBillCreateBinding binding;
    private ToastMessage toastMessage;
    private BillDTO billDTO;
    private BillViewModel viewmodel;

    public BillCreateFragment() {
        // Required empty public constructor
    }

    public static BillCreateFragment newInstance(BillDTO dto) {
        BillCreateFragment fragment = new BillCreateFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BILL_DETAILS_DTO, dto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            billDTO = (BillDTO) getArguments().getSerializable(ARG_BILL_DETAILS_DTO);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBillCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toastMessage = new ToastMessage(requireContext());

        if (billDTO == null) {
            toastMessage.showError("Invalid bill information details", new Exception("Missing DTO"));
            return;
        }

        // Initialize ViewModel first
        viewmodel = new BillViewModel(ServiceManager.getBillService(), ServiceManager.getMeterReadingService());

        setupObservers();
        setupClickListeners();
        displayBillDetails();
    }

    private void setupObservers() {
        // Observe operation success to navigate back and show success toast safely
        viewmodel.getOperationSuccessLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "Bill Generated Successfully", Toast.LENGTH_SHORT).show();
                NavigationUtils.replaceFragmentWithBackStack(
                        requireActivity(),
                        new BillFragment(),
                        BottomNavActivityConstant.MAIN_CONTAINER
                );
            }
        });

        // Observe general toast messages from ViewModel
        viewmodel.getToastMessageLiveData().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe error messages separately from success flows
        viewmodel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                toastMessage.showError("Error: " + error, new Exception(error));
            }
        });
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnGenerateSendBill.setOnClickListener(v -> {
            String mpinMessage = String.format(Locale.getDefault(),
                    "Meter Serial: %s | Current: %d | Rate: %.2f | Fixed: %.2f | Extra: %.2f | Note: %s",
                    billDTO.getMeterSerialNumber(), billDTO.getCurrentReading(),
                    billDTO.getRatePerUnit(), billDTO.getFixedCharge(),
                    billDTO.getExtraCharge(), billDTO.getNote());

            MpinVerificationDialogFragment dialog = MpinVerificationDialogFragment.newInstance(mpinMessage);
            dialog.show(getChildFragmentManager(), "MpinVerifyDialog");
        });
    }

    private void displayBillDetails() {
        binding.tvRoomNumberLabel.setText(billDTO.getRoomNumber());
        binding.tvTenantName.setText(billDTO.getTenantName());

        binding.tvPreviousReading.setText(String.valueOf(billDTO.getPreviousReading()));
        binding.tvCurrentReading.setText(String.valueOf(billDTO.getCurrentReading()));
        binding.tvUnitsConsumed.setText(String.valueOf(billDTO.getUnitsConsumed()));
        binding.tvUnitRate.setText(String.valueOf(billDTO.getRatePerUnit()));

        String calcFormula = String.format(Locale.getDefault(), "%d KWh * %.2f + %.2f",
                billDTO.getUnitsConsumed(), billDTO.getRatePerUnit(), billDTO.getFixedCharge());
        binding.tvCalculation.setText(calcFormula);

        binding.tvFixedCharges.setText(String.valueOf(billDTO.getFixedCharge()));
        binding.tvExtraCharges.setText(String.valueOf(billDTO.getExtraCharge()));
        binding.tvNotes.setText(billDTO.getNote());
        binding.tvTotalAmount.setText(String.valueOf(billDTO.getTotalAmount()));
    }

    @Override
    public void onMpinVerified(boolean isSuccess) {
        if (isSuccess) {
            addMeterReadingAndGenerateBill();
        }
    }

    private void addMeterReadingAndGenerateBill() {
        try {
            viewmodel.addMeterReadingAndGenerateBill(
                    billDTO.getRoomNumber(),
                    billDTO.getTenantName(),
                    billDTO.getTenantId(),
                    billDTO.getMeterId(),
                    billDTO.getMeterSerialNumber(),
                    billDTO.getCurrentReading(),
                    billDTO.getPreviousReadingId(),
                    billDTO.getPreviousReading(),
                    billDTO.getRatePerUnit(),
                    billDTO.getFixedCharge(),
                    billDTO.getExtraCharge(),
                    DateUtils.toDatabaseFormat(billDTO.getBillingDate()),
                    billDTO.getNote()
            );
        } catch (Exception e) {
            toastMessage.showError("Error: " + e.getMessage(), e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}