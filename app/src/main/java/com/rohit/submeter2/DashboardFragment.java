package com.rohit.submeter2;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.application.baselibrary.ui.utils.NavigationUtils;
import com.rohit.submeter2.adapters.PendingBillsAdapter;
import com.rohit.submeter2.databinding.FragmentDashboardBinding;
import com.rohit.submeter2.fragments.AllTablesDataFragment;
import com.rohit.submeter2.fragments.ConfigurationFragment;
import com.rohit.submeter2.fragments.QrScanRoomFragment;
import com.rohit.submeter2.fragments.RoomManagementFragment;
import com.rohit.submeter2.fragments.SubmeterAllocationFragment;
import com.rohit.submeter2.fragments.SubmeterManagementFragment;
import com.rohit.submeter2.fragments.TenancyFragment;
import com.rohit.submeter2.fragments.TenantManagementFragment;
import com.rohit.submeter2.fragments.mpin.MpinVerificationDialogFragment;
import com.rohit.submeter2.fragments.mpin.SetupMpinFragment;
import com.rohit.submeter2.utils.ConfigManager;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.presentation.viewmodel.BaseViewModel;
import com.submeter.presentation.viewmodel.DashboardViewModel;
import com.submeter.service.ServiceManager;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements MpinVerificationDialogFragment.MpinVerificationListener {

    private FragmentDashboardBinding binding;
    private PendingBillsAdapter billAdapter;
    private List<BillDTO> pendingBills;
    private BillDTO selectedBill;
    private DashboardViewModel viewModel;

    private BaseViewModel.StateListener stateListener;
    private String lastShownError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvActiveDatabaseName.setText("CURRENT DB IN USE: " + ConfigManager.getDbUrl(requireContext()));

        pendingBills = new ArrayList<>();
        binding.rvPendingBills.setLayoutManager(new LinearLayoutManager(getContext()));

        billAdapter = new PendingBillsAdapter(pendingBills, new PendingBillsAdapter.OnPayClickListener() {
            @Override
            public void onPayClick(BillDTO bill) {
                selectedBill = bill;
                String message = "Bill No: " + bill.getId() + "  Room: " + bill.getRoomNumber() +
                        " Tenant: " + bill.getTenantName() + " Amount: ₹" + bill.getTotalAmount();

                MpinVerificationDialogFragment dialog = MpinVerificationDialogFragment.newInstance(message);
                dialog.show(getChildFragmentManager(), "MpinVerifyDialog");
            }
        });

        billAdapter = new PendingBillsAdapter(pendingBills, new PendingBillsAdapter.OnPayClickListener() {
            @Override
            public void onPayClick(BillDTO bill) {
                selectedBill = bill;

                showPaymentInputDialog(bill);
            }
        });
        binding.rvPendingBills.setAdapter(billAdapter);

        // Instantiate isolated ViewModel with operational dependencies explicitly injected
        viewModel = new DashboardViewModel(
                ServiceManager.getBillService(),
                ServiceManager.getPaymentService()
        );


        // Secure state modification triggers across context layers
        stateListener = () -> {
            Activity activity = getActivity();

            if (activity != null && binding != null) {
                activity.runOnUiThread(this::renderDashboardMatrics);
            }
        };

        viewModel.addStateListener(stateListener);

        dashboardButtons();

        // Search text filtration listeners
        binding.etSearchBills.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (billAdapter != null) {
                    billAdapter.filter(s.toString());
                }
            }
        });

        // Fire metrics evaluation fetch loops
        viewModel.loadDashboardSummary();
//        viewModel.performDatabaseCheck();  // Run only if one want to check or alter
    }

    /**
     * Single source of truth evaluation analyzer driving visual elements from layout values
     */

    private void renderDashboardMatrics() {
        if (binding == null || viewModel == null) {
            return;
        }

        // Update controls according to loading state.
        binding.btnScanMeter.setEnabled(!viewModel.isLoading());

        // Display an error only once until the error changes or clears.
        String errorMessage = viewModel.getErrorMessage();

        if (errorMessage != null && !errorMessage.isBlank()) {
            if (!errorMessage.equals(lastShownError)) {
                Toast.makeText(
                        requireContext(),
                        errorMessage,
                        Toast.LENGTH_SHORT
                ).show();

                lastShownError = errorMessage;
            }
        } else {
            lastShownError = null;
        }

        // Render the dashboard metrics.
        binding.totalUnits.setText(
                String.format(
                        java.util.Locale.US,
                        "%.2f",
                        viewModel.getTotalUnits()
                )
        );

        binding.totalBills.setText(
                String.valueOf(viewModel.getTotalBillsCount())
        );

        binding.totalRevenue.setText(
                String.format(
                        java.util.Locale.US,
                        "%.2f",
                        viewModel.getTotalBilledAmount()
                )
        );

        // Refresh pending bills.
        pendingBills.clear();
        pendingBills.addAll(viewModel.getPendingBills());

        billAdapter.notifyDataSetChanged();
    }


    @Override
    public void onMpinVerified(boolean isSuccess) {
        if (isSuccess && selectedBill != null) {
            String currentDate = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault()).format(new java.util.Date());

            viewModel.payBill(
                    selectedBill.getId(),
                    pendingPaidAmount,
                    currentDate,
                    pendingPaymentNote
            );
            selectedBill = null;
        } else {
            Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
        }
    }

    void dashboardButtons() {
        // Click listeners driving routing destinations
        binding.dashboardSection.roomManagement.setOnClickListener(v ->
                NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new RoomManagementFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout));
//
        binding.dashboardSection.addEditTenant.setOnClickListener(v ->
                NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new TenantManagementFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout));
//
        binding.dashboardSection.addUpdateSubmeter.setOnClickListener(v ->
                NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new SubmeterManagementFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout));
//
//
        binding.dashboardSection.submeterAllocation.setOnClickListener(v ->
                NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new SubmeterAllocationFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout));
//
        binding.dashboardSection.addEditTenancy.setOnClickListener(v ->
                NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new TenancyFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout));
//
        binding.btnScanMeter.setOnClickListener(v ->
                NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new QrScanRoomFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout));
//
        binding.dashboardSection.changeMPin.setOnClickListener(v ->
                NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new SetupMpinFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout));


        binding.dashboardSection.dbTables.setOnClickListener(v->{
            NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new AllTablesDataFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout);
        });

//        binding.dashboardSection.databaseMigration.setOnClickListener(v->{
//            NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new DatabaseMigrationFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout);
//        });

        binding.dashboardSection.databaseConfiguration.setOnClickListener(v ->
                NavigationUtils.replaceFragmentWithBackStack(requireActivity(), new ConfigurationFragment(), com.application.android_ui_templete1.R.id.bottom_nav_activity_main_frame_layout));

    }

    @Override
    public void onDestroyView() {
        if (viewModel != null && stateListener != null) {
            viewModel.removeStateListener(stateListener);
            stateListener = null;
        }

        binding = null;
        super.onDestroyView();
    }

    private void showPaymentInputDialog(BillDTO bill) {
        // You can inflate a custom layout containing EditText fields for amount and note,
        // or prompt the user. Here is the structure using an AlertDialog wrapper:
        android.view.View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_payment_input, null);
        android.widget.EditText etAmount = dialogView.findViewById(R.id.etPaymentAmount);
        android.widget.EditText etNote = dialogView.findViewById(R.id.etPaymentNote);

        // Pre-fill with remaining balance
        etAmount.setText(String.valueOf(bill.getBalance() > 0 ? bill.getBalance() : bill.getTotalAmount()));

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Make Payment - Room " + bill.getRoomNumber())
                .setView(dialogView)
                .setPositiveButton("Verify MPIN & Pay", (dialog, which) -> {
                    String amountStr = etAmount.getText().toString().trim();
                    String note = etNote.getText().toString().trim();

                    if (amountStr.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double paidAmount = Double.parseDouble(amountStr);
                    if (paidAmount <= 0) {
                        Toast.makeText(getContext(), "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Store temporary payment details to use upon successful MPIN verification
                    // (You can store these in class-level fields: pendingPaidAmount, pendingPaymentNote)
                    this.pendingPaidAmount = (long) paidAmount; // or double depending on your payBill signature
                    this.pendingPaymentNote = note.isEmpty() ? "Bill Payment" : note;

                    String message = "Paying ₹" + paidAmount + " for Bill No: " + bill.getId();
                    MpinVerificationDialogFragment dialogFragment = MpinVerificationDialogFragment.newInstance(message);
                    dialogFragment.show(getChildFragmentManager(), "MpinVerifyDialog");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private long pendingPaidAmount;
    private String pendingPaymentNote;
}