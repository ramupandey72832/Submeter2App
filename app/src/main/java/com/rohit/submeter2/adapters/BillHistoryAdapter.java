package com.rohit.submeter2.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rohit.submeter2.R;
import com.rohit.submeter2.databinding.BillItemBinding;
import com.submeter.commonlib.dto.BillDTO;
import com.submeter.commonlib.dto.PaymentDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BillHistoryAdapter extends RecyclerView.Adapter<BillHistoryAdapter.BillViewHolder> {

    private List<BillDTO> filteredBills;
    private List<BillDTO> allBills;
    private final OnBillClickListener clickListener;

    public interface OnBillClickListener {
        void onShowBill(BillDTO bill);
        void onShareBill(BillDTO bill);
        void onEditBill(BillDTO bill);
    }

    public BillHistoryAdapter(List<BillDTO> billList, OnBillClickListener clickListener) {
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
                boolean matchesId = String.valueOf(item.getId()).contains(filterPattern);

                if (matchesRoom || matchesTenant || matchesId) {
                    filteredBills.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BillItemBinding binding = BillItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BillViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        BillDTO currentBill = filteredBills.get(position);
        if (currentBill == null) return;
        Context context = holder.itemView.getContext();

        // Safe Text Bindings
        holder.binding.tvBillNumber.setText("Bill #" + currentBill.getId());
        holder.binding.tvRoomAndName.setText("(Room " + currentBill.getRoomNumber() + ", " + currentBill.getTenantName() + ")");
        holder.binding.tvDate.setText(currentBill.getBillingDate() != null ? currentBill.getBillingDate() : "");
        holder.binding.tvAmount.setText("₹" + currentBill.getTotalAmount());

        // TODO: Pass actual payment history list from your ViewModel/Repository if available
        List<PaymentDTO> paymentsOfBillId = Collections.emptyList();
        double balance = calculateBalance(currentBill, paymentsOfBillId);

        holder.binding.balance.setText("₹" + balance);
        if (balance <= 0) {
            holder.binding.balance.setTextColor(ContextCompat.getColor(context, R.color.green_text));
            holder.binding.balance.setBackgroundColor(Color.parseColor("#D4EDDA")); // Light green
        } else {
            holder.binding.balance.setTextColor(ContextCompat.getColor(context, R.color.red_text));
            holder.binding.balance.setBackgroundColor(Color.parseColor("#F8D7DA")); // Light red/pink
        }

        // Action listeners
        holder.binding.btnViewReceipt.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onShowBill(currentBill);
            }
        });

        holder.binding.btnEditBill.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onEditBill(currentBill);
            }
        });

        holder.binding.btnShareBill.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onShareBill(currentBill);
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
        return filteredBills != null ? filteredBills.size() : 0;
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        final BillItemBinding binding;

        public BillViewHolder(@NonNull BillItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}