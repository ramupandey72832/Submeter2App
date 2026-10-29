package com.rohit.submeter2.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import com.google.android.material.textview.MaterialTextView;


public class TableDataAdapter extends RecyclerView.Adapter<TableDataAdapter.RowViewHolder> {

    private final List<String> tableHeaders = new ArrayList<>();
    private final List<List<String>> tableRows = new ArrayList<>();

    public void updateData(List<String> headers, List<List<String>> rows) {
        this.tableHeaders.clear();
        this.tableHeaders.addAll(headers);
        this.tableRows.clear();
        this.tableRows.addAll(rows);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tableHeaders.isEmpty() ? 0 : tableRows.size() + 1;
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int padH = (int) (4 * context.getResources().getDisplayMetrics().density);
        int padV = (int) (10 * context.getResources().getDisplayMetrics().density);

        LinearLayout row = new LinearLayout(context);
        row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(padH, padV, padH, padV);

        return new RowViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        Context context = holder.rowContainer.getContext();
        holder.rowContainer.removeAllViews();

        boolean isHeader = (position == 0);
        List<String> rowData = isHeader ? tableHeaders : tableRows.get(position - 1);

        // Handle alternating background colors cleanly using framework attributes
        int bgAttr = isHeader ? com.google.android.material.R.attr.colorSurfaceVariant :
                (position % 2 == 1 ? com.google.android.material.R.attr.colorSurfaceContainerLow : android.R.attr.colorBackground);

        android.util.TypedValue typedValue = new android.util.TypedValue();
        context.getTheme().resolveAttribute(bgAttr, typedValue, true);
        holder.itemView.setBackgroundColor(typedValue.data);

        int cellWidth = (int) (140 * context.getResources().getDisplayMetrics().density);
        int cellMargin = (int) (8 * context.getResources().getDisplayMetrics().density);

        for (String cellText : rowData) {
            // MaterialTextView handles styles, sizes, and contrast modes out of the box
            MaterialTextView cell = new MaterialTextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(cellWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(cellMargin, 0, cellMargin, 0);
            cell.setLayoutParams(lp);

            cell.setText(cellText != null ? cellText : "NULL");
            cell.setTextAppearance(isHeader ?
                    com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle2 :
                    com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2);

            holder.rowContainer.addView(cell);
        }
    }

    static class RowViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout rowContainer;
        RowViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rowContainer = (LinearLayout) itemView;
        }
    }
}
