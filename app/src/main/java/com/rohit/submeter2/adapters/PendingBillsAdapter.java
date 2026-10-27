package com.rohit.submeter2.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.submeter2.R;
import com.rohit.submeter2.databinding.PendingBillItemBinding;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.dto.PaymentDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PendingBillsAdapter extends RecyclerView.Adapter<PendingBillsAdapter.PendingBillViewHolder> {

    private List<BillDTO> filteredBills;
    private List<BillDTO> allBills;
    private final OnPayClickListener clickListener;

    public interface OnPayClickListener {
        void onPayClick(BillDTO bill);
    }

    public PendingBillsAdapter(List<BillDTO> billList, OnPayClickListener clickListener) {
        this.filteredBills = billList != null ? billList : new ArrayList<>();
        this.allBills = new ArrayList<>(this.filteredBills);
        this.clickListener = clickListener;
    }

    public void updateData(List<BillDTO> newList) {
        this.filteredBills = newList != null ? newList : new ArrayList<>();
        this.allBills = new ArrayList<>(this.filteredBills);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        filteredBills.clear();
        if (text == null || text.isEmpty()) {
            filteredBills.addAll(allBills);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (BillDTO item : allBills) {
                boolean matchesRoom = item.getRoomNumber() != null && item.getRoomNumber().toLowerCase().contains(filterPattern);
                boolean matchesTenant = item.getTenantName() != null && item.getTenantName().toLowerCase().contains(filterPattern);

                if (matchesRoom || matchesTenant) {
                    filteredBills.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PendingBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PendingBillItemBinding binding = PendingBillItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PendingBillViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingBillViewHolder holder, int position) {
        BillDTO selectedBill = filteredBills.get(position);
        Context context = holder.itemView.getContext();

        holder.binding.tvRoomNumber.setText(selectedBill.getRoomNumber());
        holder.binding.tvTenantName.setText("Tenant: " + selectedBill.getTenantName());
        holder.binding.tvBillAmount.setText("₹" + selectedBill.getTotalAmount());
        holder.binding.tvBillMeta.setText("Bill No.: " + selectedBill.getId() + "  •  " + selectedBill.getBillingDate());

        List<PaymentDTO> paymentsOfBillId = Collections.emptyList();
        double balance = calculateBalance(selectedBill, paymentsOfBillId);

        if (balance == 0) {
            holder.binding.btnStatusBadge.setText("Paid");
            holder.binding.btnStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.green_text));
            holder.binding.btnStatusBadge.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_background))
            );
        } else {
            holder.binding.btnStatusBadge.setText("Pay");
            holder.binding.btnStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.red_text));
            holder.binding.btnStatusBadge.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_background))
            );
        }

        holder.binding.btnStatusBadge.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPayClick(selectedBill);
            }
        });
    }

    public double calculateBalance(BillDTO bill, List<PaymentDTO> payments) {
        if (bill == null) return 0.0;

        if (payments == null || payments.isEmpty()) {
            return bill.getTotalAmount();
        }

        double totalPaid = payments.stream()
                .filter(payment -> payment != null && payment.getBillId() == bill.getId())
                .mapToDouble(PaymentDTO::getAmountPaid)
                .sum() / 100.0;

        return bill.getTotalAmount() - totalPaid;
    }

    @Override
    public int getItemCount() {
        // FIXED: Must return filteredBills size so RecyclerView matches search results safely!
        return filteredBills != null ? filteredBills.size() : 0;
    }

    static class PendingBillViewHolder extends RecyclerView.ViewHolder {
        final PendingBillItemBinding binding;

        public PendingBillViewHolder(@NonNull PendingBillItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}