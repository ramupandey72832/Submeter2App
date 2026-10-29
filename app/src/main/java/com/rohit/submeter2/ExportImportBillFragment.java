package com.rohit.submeter2;


import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.baselibrary.ui.utils.ToastMessage;
import com.rohit.submeter2.adapters.PreviousBillDocumentAdapter;
import com.rohit.submeter2.databinding.FragmentExportImportBillBinding;
import com.submeter.presentation.viewmodel.BaseViewModel;
import com.submeter.presentation.viewmodel.ExportImportBillViewModel;
import com.submeter.service.ServiceManager;
import com.rohit.submeter2.utils.pdf.BillPdfEngine;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExportImportBillFragment extends Fragment {

    private ToastMessage toastMessage;
    private FragmentExportImportBillBinding binding;

    private final Calendar billingStartCalendar = Calendar.getInstance();
    private final Calendar billingEndCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private ExportImportBillViewModel viewModel;
    private BaseViewModel.StateListener viewModelStateListener;

    private ActivityResultLauncher<String> exportBillingRangeLauncher;
    private ActivityResultLauncher<String> databaseBackupLauncher;
    private ActivityResultLauncher<String> databaseRestoreLauncher;

    public ExportImportBillFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerDocumentLaunchers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExportImportBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toastMessage = new ToastMessage(getContext());

        viewModel = new ExportImportBillViewModel(ServiceManager.getExportImportService());

        observeViewModelState();
        initializeDatePickers();
        setupClickListeners();

        viewModel.loadHistoricalThreeMonthReports();
    }

    private void registerDocumentLaunchers() {
        exportBillingRangeLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/vnd.ms-excel"),
                uri -> {
                    if (uri != null && viewModel.getGeneratedExcelReportBytes() != null) {
                        writeBytesToDestination(uri, viewModel.getGeneratedExcelReportBytes(), "Failed to write billing range report");
                    }
                }
        );

        databaseBackupLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/vnd.ms-excel"),
                uri -> {
                    if (uri != null && viewModel.getGeneratedBackupBytes() != null) {
                        String absolutePath = getAbsolutePathFromUri(uri);
                        binding.tvDatabasePath.setText("Exported File Path: " + absolutePath);
                        writeBytesToDestination(uri, viewModel.getGeneratedBackupBytes(), "Failed to write database backup");
                    }
                }
        );

        databaseRestoreLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && getContext() != null) {
                        try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri)) {
                            if (inputStream != null) {
                                byte[] fileBytes = readBytesFromStream(inputStream);
                                viewModel.executeFullExcelImport(fileBytes);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Failed to read database import file", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void observeViewModelState() {
        viewModelStateListener = () -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::renderUiState);
            }
        };
        viewModel.addStateListener(viewModelStateListener);
    }

    private void initializeDatePickers() {
        binding.tvStartDate.setText(dateFormat.format(billingStartCalendar.getTime()));
        binding.tvEndDate.setText(dateFormat.format(billingEndCalendar.getTime()));
    }

    private void setupClickListeners() {
        binding.btnStartDate.setOnClickListener(v -> showDatePickerDialog(billingStartCalendar, (view1, year, month, dayOfMonth) -> {
            billingStartCalendar.set(year, month, dayOfMonth);
            binding.tvStartDate.setText(dateFormat.format(billingStartCalendar.getTime()));
        }));

        binding.btnEndDate.setOnClickListener(v -> showDatePickerDialog(billingEndCalendar, (view1, year, month, dayOfMonth) -> {
            billingEndCalendar.set(year, month, dayOfMonth);
            binding.tvEndDate.setText(dateFormat.format(billingEndCalendar.getTime()));
        }));

        binding.btnExportSelectedRangeBill.setOnClickListener(v -> {
            Date startDate = billingStartCalendar.getTime();
            Date endDate = billingEndCalendar.getTime();

            if (startDate.after(endDate)) {
                Toast.makeText(requireContext(), "Start date cannot be after end date", Toast.LENGTH_LONG).show();
                return;
            }

            String startStr = dateFormat.format(startDate);
            String endStr = dateFormat.format(endDate);

            viewModel.exportBillsByRange(startStr, endStr);

            // Also trigger PDF generation
            if (!viewModel.getRangeFilteredBills().isEmpty()) {
                exportBillingRangeLauncher.launch("Billing_Report_" + startStr + "_to_" + endStr + ".xls");
                BillPdfEngine.generateBillingSummaryReport(requireContext(), viewModel.getRangeFilteredBills(), startStr, endStr);
            }
        });

        binding.btnGetLatestReadableBillDocument.setOnClickListener(v -> {
            viewModel.getLatestBills();
            if (!viewModel.getLatestMonthlyBills().isEmpty()) {
                BillPdfEngine.generateBulkInvoicesGridPdf(getContext(), viewModel.getLatestMonthlyBills(), "All_Latest_Monthly_Bill.pdf");
            }
        });

        binding.btnExportAllTableToExcel.setOnClickListener(v -> {
            viewModel.executeFullExcelBackup();
            databaseBackupLauncher.launch("Full_Database_Backup.xls");
        });

        binding.btnImportAllTableFromExcel.setOnClickListener(v -> databaseRestoreLauncher.launch("application/vnd.ms-excel"));
    }

    private void renderUiState() {
        boolean isLoading = viewModel.isLoading();

        binding.btnExportSelectedRangeBill.setEnabled(!isLoading);
        binding.btnGetLatestReadableBillDocument.setEnabled(!isLoading);
        binding.btnExportAllTableToExcel.setEnabled(!isLoading);
        binding.btnImportAllTableFromExcel.setEnabled(!isLoading);

        if (viewModel.getSuccessMessage() != null) {
            toastMessage.showSuccess(viewModel.getSuccessMessage());
        }

        if (viewModel.getErrorMessage() != null) {
            toastMessage.showError("Reporting Failure", new Exception(viewModel.getErrorMessage()));
        }

        if (viewModel.getGroupedPreviousReports() != null && !viewModel.getGroupedPreviousReports().isEmpty()) {
            PreviousBillDocumentAdapter reportAdapter = new PreviousBillDocumentAdapter(getContext(), viewModel.getGroupedPreviousReports());
            binding.rvPreviousReports.setAdapter(reportAdapter);
        }
    }

    private void showDatePickerDialog(Calendar calendar, DatePickerDialog.OnDateSetListener dateSetListener) {
        if (getContext() == null) return;
        new DatePickerDialog(
                getContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void writeBytesToDestination(Uri uri, byte[] dataBytes, String errorMessage) {
        if (uri != null && getContext() != null) {
            try (OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    outputStream.write(dataBytes);
                    outputStream.flush();
                    Toast.makeText(getContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] readBytesFromStream(InputStream inputStream) throws Exception {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private String getAbsolutePathFromUri(Uri uri) {
        if (uri == null) return null;
        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            String[] splitPath = docId.split(":");
            if ("primary".equalsIgnoreCase(splitPath[0])) {
                return Environment.getExternalStorageDirectory() + "/" + (splitPath.length > 1 ? splitPath[1] : "");
            }
        }
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return uri.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null && viewModelStateListener != null) {
            viewModel.removeStateListener(viewModelStateListener);
        }
        binding = null;
    }
}