package com.rohit.submeter2.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.android_ui_templete1.templates.nav_activity.bottom_nav_activity.BottomNavActivityConstant;
import com.application.baselibrary.media.MediaStoreHelper;
import com.application.baselibrary.ui.utils.ToastMessage;
import com.rohit.submeter2.adapters.BillHistoryAdapter;
import com.rohit.submeter2.databinding.FragmentBillBinding;
import com.rohit.submeter2.utils.pdf.BillPdfEngine;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.presentation.viewmodel.BillViewModel;
import com.submeter.service.ServiceManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class BillFragment extends Fragment {

    private ToastMessage toastMessage;
    private FragmentBillBinding binding;

    private BillHistoryAdapter adapter;
    private List<BillDTO> bills; // Linked directly inside the layout view adapter
    private BillViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toastMessage = new ToastMessage(requireContext());

        // Initialize view display collection mapping pointers
        bills = new ArrayList<>();

        // Establish the Layout Manager mechanics first
        binding.rvBills.setLayoutManager(new LinearLayoutManager(getContext()));

        // Wire layout interactions up to the matching events handler adapter
        adapter = new BillHistoryAdapter(bills, new BillHistoryAdapter.OnBillClickListener() {
            @Override
            public void onShowBill(BillDTO bill) {
                try {
                    String fileName = "Thermal_Bill_" + bill.getId() + System.currentTimeMillis() +".pdf";

                    // 1. Query if it exists already
                    android.net.Uri fileUri = MediaStoreHelper.checkDownloadFileExistence(requireContext(), fileName);

                    if (fileUri != null) {
                        // File exists already! Open it directly
                        android.widget.Toast.makeText(requireContext(), "Opening existing bill slip...", android.widget.Toast.LENGTH_SHORT).show();
                        viewGeneratedBill(fileUri);
                    } else {
                        // 2. File does not exist. Create it and grab the exact returned Uri
                        android.net.Uri freshUri = BillPdfEngine.generateSingleBillSlip(requireContext(), bill, fileName);

                        if (freshUri != null) {
                            viewGeneratedBill(freshUri);
                        } else {
                            android.widget.Toast.makeText(requireContext(), "Error initializing PDF generation file stream.", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    toastMessage.showError("BillFragment : PDF Generation Failure", e);
                }
            }

            @Override
            public void onShareBill(BillDTO bill) {
                try {
                    String filename = "Thermal_Bill_" + bill.getId() + System.currentTimeMillis() +".pdf";
                    BillPdfEngine.generateSingleBillSlip(requireContext(), bill,filename);

                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File sharedFile = new File(downloadsDir, filename);

                    if (!sharedFile.exists()) {
                        Toast.makeText(requireContext(), "Error: Reference file could not be found to share.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String authority = requireContext().getPackageName() + ".fileprovider";
                    Uri fileUri = FileProvider.getUriForFile(requireContext(), authority, sharedFile);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("application/pdf");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Billing Report: " + bill.getRoomNumber());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Attached is the monthly utility billing report");

                    shareIntent.setClipData(android.content.ClipData.newRawUri("", fileUri));
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent chooserIntent = Intent.createChooser(shareIntent, "Share Monthly PDF Via:");
                    chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    requireContext().startActivity(chooserIntent);

                } catch (Exception e) {
                    toastMessage.showError("BillFragment : Share Command Failure", e);
                }
            }

            @Override
            public void onEditBill(BillDTO bill) {
                // Replaced inline placeholder comment with structured safe transaction navigation sequence
                Fragment editBillFragment = BillEditFragment.newInstance(bill);

                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out,
                                android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right
                        )
                        .replace( BottomNavActivityConstant.MAIN_CONTAINER , editBillFragment) // Maps over layout screen viewport
                        .addToBackStack(null) // Restores back stack button navigation traces
                        .commit();
            }
        });

        binding.rvBills.setAdapter(adapter);

        viewModel = new BillViewModel(ServiceManager.getBillingService(),
                ServiceManager.getMeterReadingService());

        // Attach listener handles driving runtime state updates securely
        viewModel.addStateListener(new BillViewModel.StateListener() {
            @Override
            public void onStateChanged() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> renderUiState());
                }
            }
        });

        // Add real-time text listener to search layout bars
        binding.etSearchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Pass filtration actions straight to the business layer logic holder
                viewModel.filterBills(editable.toString());
            }
        });

        // Trigger asynchronous retrieval loading sweep
        viewModel.fetchBills();
    }

    private void renderUiState() {

        // 2. Intercept and project failures cleanly through central ErrorUtils mapping layers
        if (viewModel.getErrorMessage() != null) {
            toastMessage.showError("BillFragment : VM ERROR MSG "+viewModel.getErrorMessage(),
                    new Exception(viewModel.getErrorMessage()));

        }

        // 3. Sync matching items to adapter cache elements natively upon success flag triggers
        if (viewModel.isOperationSuccess() || viewModel.getFilteredBillList() != null) {
            bills.clear();
            bills.addAll(viewModel.getFilteredBillList());
            adapter.notifyDataSetChanged();
        }
    }

    private void viewGeneratedBill(@NonNull android.net.Uri fileUri) {
        try {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/pdf");

            // CRUCIAL: Grant temporary read permissions to whatever external PDF reader handles this intent
            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(android.content.Intent.createChooser(intent, "Open Bill via"));
        } catch (android.content.ActivityNotFoundException e) {
            toastMessage.showError("Bill Fragment : No PDF viewer app found on device", e);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.removeStateListener(null); // Evict active listener callbacks to lock down context leaks
        binding = null;
    }
}