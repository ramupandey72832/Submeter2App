package com.rohit.submeter2.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.android_ui_templete1.templates.nav_activity.bottom_nav_activity.BottomNavActivityConstant;
import com.application.baselibrary.ui.utils.NavigationUtils;
import com.application.baselibrary.ui.utils.ToastMessage;
import com.rohit.submeter2.databinding.FragmentBillCreateBinding;
import com.rohit.submeter2.fragments.mpin.MpinVerificationDialogFragment;
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
    private BillDTO billDetails;
    private  BillViewModel viewmodel;
    //TODO USE VIEW MODEL

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
            billDetails = (BillDTO) getArguments().getSerializable(ARG_BILL_DETAILS_DTO);
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

        if (billDetails == null) {
            toastMessage.showError("Invalid bill information details", new Exception("Missing DTO"));
            return;
        }

        setupClickListeners();
        displayBillDetails();

        viewmodel = new BillViewModel(ServiceManager.getBillService(),ServiceManager.getMeterReadingService());
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btnGenerateSendBill.setOnClickListener(v -> {
            String mpinMessage = String.format(Locale.getDefault(),
                    "Meter Serial: %s | Current: %.2f | Rate: %.2f | Fixed: %.2f | Extra: %.2f | Note: %s",
                    billDetails.getMeterSerialNumber(), billDetails.getCurrentReading(),
                    billDetails.getRatePerUnit(), billDetails.getFixedCharge(),
                    billDetails.getExtraCharge(), billDetails.getNote());

            MpinVerificationDialogFragment dialog = MpinVerificationDialogFragment.newInstance(mpinMessage);
            dialog.show(getChildFragmentManager(), "MpinVerifyDialog");
        });
    }

    private void displayBillDetails() {
        binding.tvRoomNumberLabel.setText(billDetails.getRoomNumber());
        binding.tvTenantName.setText(billDetails.getTenantName());

        binding.tvPreviousReading.setText(String.valueOf(billDetails.getPreviousReading()));
        binding.tvCurrentReading.setText(String.valueOf(billDetails.getCurrentReading()));
        binding.tvUnitsConsumed.setText(String.valueOf(billDetails.getUnitsConsumed()));
        binding.tvUnitRate.setText(String.valueOf(billDetails.getRatePerUnit()));

        String calcFormula = String.format(Locale.getDefault(), "%.2f KWh * %.2f + %.2f",
                billDetails.getUnitsConsumed(), billDetails.getRatePerUnit(), billDetails.getFixedCharge());
        binding.tvCalculation.setText(calcFormula);

        binding.tvFixedCharges.setText(String.valueOf(billDetails.getFixedCharge()));
        binding.tvExtraCharges.setText(String.valueOf(billDetails.getExtraCharge()));
        binding.tvNotes.setText(billDetails.getNote());
        binding.tvTotalAmount.setText(String.valueOf(billDetails.getTotalAmount()));
    }

    @Override
    public void onMpinVerified(boolean isSuccess) {
        if (isSuccess) {
            addMeterReadingAndGenerateBill();
        }
    }

    private void addMeterReadingAndGenerateBill() {
        boolean hasError = false;
        try {
            // Note: If database execution tracks reading values precisely, cast to long or change db schema to double
            viewmodel.addMeterReadingAndGenerateBill(
                    billDetails.getRoomNumber(),
                    (long) billDetails.getCurrentReading(),
                    billDetails.getRatePerUnit(),
                    billDetails.getFixedCharge(),
                    billDetails.getExtraCharge(),
                    billDetails.getNote()
            );
        } catch (Exception e) {
            hasError = true;
//            ErrorUtils.handleDatabaseException("Error: ", e, ui);
            toastMessage.showError("Error: "+e.getMessage());
        } finally {
            if (!hasError) {
                toastMessage.showSuccess("Bill Generated Successfully", new Exception(""));
                NavigationUtils.replaceFragmentWithBackStack(
                        requireActivity(),
                        new BillFragment(),
                        BottomNavActivityConstant.MAIN_CONTAINER
                );
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clean up layout reference to avoid memory leaks
    }
}
