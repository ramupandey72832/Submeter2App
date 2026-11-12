package com.rohit.submeter2.utils.pdf;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import com.submeter.commonlib.dto.BillDTO;

import java.util.Locale;

public class ReceiptCanvasRenderer {

    public static void draw(@NonNull Canvas canvas, @NonNull BillDTO bill,
                            float startX, float startY, float width, float height,
                            float titleSize, float textSize, int leading, boolean drawBounds) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        final int sidePadding = 12;
        final float rightEdge = startX + width - sidePadding;
        float currentY = startY + 20;

        // Header: Room Number
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        paint.setTextSize(titleSize);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Room Number: " + bill.getRoomNumber(), startX + (width / 2f), currentY, paint);

        // Sub-details Header Section
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(textSize);

        currentY += leading + 4;

        // Line 1: Meter No. (Left) & Tenant Name (Right/Split or Half-way)
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        canvas.drawText("Meter No. : " + bill.getMeterSerialNumber(), startX + sidePadding, currentY, paint);

        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        canvas.drawText("Tenant: " + bill.getTenantName(), startX + (width / 2f) + 5, currentY, paint);

        // Line 2: Bill Date & Bill No. (Bold ID)
        currentY += leading + 2;
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        canvas.drawText("Bill Date : " + bill.getBillingDate(), startX + sidePadding, currentY, paint);

        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        canvas.drawText("Bill No.  : " + bill.getId(), startX + (width / 2f) + 5, currentY, paint);

        currentY += 6;

        // Divider
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        paint.setStrokeWidth(1.0f);
        canvas.drawLine(startX + sidePadding, currentY, rightEdge, currentY, paint);
        currentY += 12;

        // Readings
        double unitsConsumed = bill.getCurrentReading() - bill.getPreviousReading();
        drawRow(canvas, paint, "Current Reading", String.valueOf((int) bill.getCurrentReading()), startX + sidePadding, rightEdge, currentY);
        currentY += leading;
        drawRow(canvas, paint, "Previous Reading", String.valueOf((int) bill.getPreviousReading()), startX + sidePadding, rightEdge, currentY);
        currentY += leading;
        drawRow(canvas, paint, "Units Consumed", String.valueOf((int) unitsConsumed), startX + sidePadding, rightEdge, currentY);
        currentY += 4;

        canvas.drawLine(startX + sidePadding, currentY, rightEdge, currentY, paint);
        currentY += 12;

        // Charges & Totals
        String amtFormat = "%.2f";
        drawRow(canvas, paint, "Rate per Unit", String.format(Locale.getDefault(), amtFormat, bill.getRatePerUnit()), startX + sidePadding, rightEdge, currentY);
        currentY += leading;
        drawRow(canvas, paint, "Fixed Charges", String.format(Locale.getDefault(), amtFormat, bill.getFixedCharge()), startX + sidePadding, rightEdge, currentY);
        currentY += leading + 3;
        drawRow(canvas, paint, "Extra Charges", String.format(Locale.getDefault(), amtFormat, bill.getExtraCharge()), startX + sidePadding, rightEdge, currentY);
        currentY += leading + 3;

        // Previous Balance (Total Balance minus Current Bill Due)
        double prevBalance = bill.getTotalBillDue() - bill.getCurrentBillDue();
        drawRow(canvas, paint, "Previous Balance", String.format(Locale.getDefault(), amtFormat, prevBalance), startX + sidePadding, rightEdge, currentY);
        currentY += leading + 3;

        // Total Amount (Bold)
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        drawRow(canvas, paint, "Total Amount", String.format(Locale.getDefault(), amtFormat, bill.getTotalBillDue()), startX + sidePadding, rightEdge, currentY);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        currentY += 8;

        canvas.drawLine(startX + sidePadding, currentY, rightEdge, currentY, paint);
        currentY += 12;

        // Notes Section (Guaranteed consistent vertical space whether text is present or empty)
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC));
        paint.setTextAlign(Paint.Align.LEFT); // <-- Add this line to force left alignment

        String noteText = (bill.getNote() != null && !bill.getNote().trim().isEmpty())
                ? "Notes: " + bill.getNote()
                : "Notes: -";
        canvas.drawText(noteText, startX + sidePadding, currentY, paint);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));

        currentY += leading + 6;
        canvas.drawLine(startX + sidePadding, currentY, rightEdge, currentY, paint);

        // Optional grid boundary lines for multi-item pages
        if (drawBounds) {
            paint.setColor(Color.LTGRAY);
            paint.setStrokeWidth(0.5f);
            canvas.drawLine(startX, startY + height, startX + width, startY + height, paint);
            canvas.drawLine(startX + width, startY, startX + width, startY + height, paint);
        }
    }

    private static void drawRow(Canvas canvas, Paint paint, String label, String value, float leftX, float rightX, float y) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(label, leftX, y, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(value, rightX, y, paint);
    }
}