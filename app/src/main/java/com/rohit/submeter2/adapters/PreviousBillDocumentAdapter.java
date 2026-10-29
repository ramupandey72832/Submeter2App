package com.rohit.submeter2.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.submeter2.databinding.ItemDocumentDownloadBinding;
import com.rohit.submeter2.utils.pdf.BillPdfEngine;
import com.submeter.commonlib.dto.BillDTO;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreviousBillDocumentAdapter extends RecyclerView.Adapter<PreviousBillDocumentAdapter.DocumentDownloadViewHolder> {

    private final Context context;
    private final List<String> monthKeys;
    private final Map<String, List<BillDTO>> groupedData;

    public PreviousBillDocumentAdapter(Context context, Map<String, List<BillDTO>> groupedData) {
        this.context = context;
        this.groupedData = groupedData;

        // Convert map keys to a list so RecyclerView can index them (0, 1, 2)
        this.monthKeys = new ArrayList<>(groupedData.keySet());
    }

    @NonNull
    @Override
    public DocumentDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocumentDownloadBinding binding = ItemDocumentDownloadBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PreviousBillDocumentAdapter.DocumentDownloadViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentDownloadViewHolder holder, int position) {
        String monthKey = monthKeys.get(position); // e.g., "2026-03"
        List<BillDTO> monthBills = groupedData.get(monthKey);

        // Convert "2026-03" to a clean title like "March 2026_Report.pdf"
        String cleanDisplayTitle = formatMonthTitle(monthKey) + "_Report.pdf";
        holder.binding.tvLabel.setText(cleanDisplayTitle);

        // Click listener for the Download action button
        holder.binding.btnDownload.setOnClickListener(v -> {
            // Call your export helper, sending ONLY this month's bills
            BillPdfEngine.generateBillingSummaryReport(context, monthBills, monthKey, monthKey);
        });

        // Click listener for the Share action button
        holder.binding.btnShare.setOnClickListener(v -> {
            BillPdfEngine.generateBillingSummaryReport(context, monthBills, monthKey, monthKey);

            // Locate the generated file inside the public Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String filename = "Billing_Report_" + monthKey + "_to_" + monthKey + ".pdf";
            File sharedFile = new File(downloadsDir, filename);

            if (!sharedFile.exists()) {
                Toast.makeText(context, "Error: Reference file could not be found to share.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert the File reference to a secure Content URI
            String authority = context.getPackageName() + ".fileprovider";
            Uri fileUri = FileProvider.getUriForFile(context, authority, sharedFile);

            // Construct the Standard Android Send Intent Channel
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Billing Report: " + formatMonthTitle(monthKey));
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Attached is the monthly utility billing report for " + formatMonthTitle(monthKey));

            // --- CRITICAL PERMISSION SECURING STEPS ---
            // Explicitly set ClipData so the Android Chooser knows exactly what file is being bound
            shareIntent.setClipData(android.content.ClipData.newRawUri("", fileUri));

            // Add the standard flags to the main intent stream
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // ------------------------------------------

            // Create the Chooser Intent
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share Monthly PDF Via:");

            // Grant read permissions directly to the chooser lifecycle container
            chooserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Fire the intent sheet UI
            context.startActivity(chooserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return monthKeys.size();
    }

    // Helper to format "2026-03" into "March 2026"
    private String formatMonthTitle(String monthKey) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            Date date = inputFormat.parse(monthKey);
            return date != null ? outputFormat.format(date) : monthKey;
        } catch (Exception e) {
            return monthKey;
        }
    }

    static class DocumentDownloadViewHolder extends RecyclerView.ViewHolder {
        final ItemDocumentDownloadBinding binding;

        public DocumentDownloadViewHolder(@NonNull ItemDocumentDownloadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
