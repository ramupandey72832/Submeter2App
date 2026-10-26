package com.rohit.submeter2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import java.util.List;

/**
 * Reusable inner adapter taking a simple list of String names
 */
public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.SimpleViewHolder> {
    private final List<String> names;

    public SimpleListAdapter(List<String> names) {
        this.names = names;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        String name = names.get(position);
        holder.tvName.setText(name);

        int textColor = MaterialColors.getColor(
                holder.itemView, com.google.android.material.R.attr.colorOnSurface
        );
        holder.tvName.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(android.R.id.text1);
        }
    }
}