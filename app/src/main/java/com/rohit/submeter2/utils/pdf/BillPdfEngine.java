package com.rohit.submeter2.utils.pdf;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.submeter.commonlib.dto.BillDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class BillPdfEngine {

    private static final int SLIP_WIDTH = 220;
    private static final int SLIP_HEIGHT = 340;
    private static final int A4_WIDTH = 595;
    private static final int A4_HEIGHT = 842;

    public static Uri generateSingleBillSlip(@NonNull Context context, @NonNull BillDTO targetBill, @NonNull String outputFileName) {
        PdfDocument pdfDoc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(SLIP_WIDTH, SLIP_HEIGHT, 1).create();
        PdfDocument.Page page = pdfDoc.startPage(pageInfo);

        ReceiptCanvasRenderer.draw(page.getCanvas(), targetBill, 0, 0, SLIP_WIDTH, SLIP_HEIGHT, 13.0f, 10.0f, 13, false);
        pdfDoc.finishPage(page);

        Uri targetUri = MediaStoreUtil.createTargetUri(context, outputFileName);
        if (targetUri == null) {
            pdfDoc.close();
            return null;
        }

        try (OutputStream out = context.getContentResolver().openOutputStream(targetUri)) {
            if (out != null) {
                pdfDoc.writeTo(out);
                Toast.makeText(context, "Bill Slip Generated Successfully", Toast.LENGTH_LONG).show();
                return targetUri;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pdfDoc.close();
        }
        return null;
    }

    public static void generateBulkInvoicesGridPdf(@NonNull Context context, @Nullable List<BillDTO> bills, @NonNull String outputFileName) {
        if (bills == null || bills.isEmpty()) {
            Toast.makeText(context, "No bills provided to batch generate", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument pdfDoc = new PdfDocument();
        final int COLS = 2;
        final int MAX_PER_PAGE = 8;
        final int SLOT_WIDTH = A4_WIDTH / COLS;
        final int SLOT_HEIGHT = A4_HEIGHT / 4;

        PdfDocument.Page currentPage = null;
        int pageCount = 0;

        for (int i = 0; i < bills.size(); i++) {
            if (i % MAX_PER_PAGE == 0) {
                if (currentPage != null) pdfDoc.finishPage(currentPage);
                pageCount++;
                currentPage = pdfDoc.startPage(new PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pageCount).create());
            }

            int subIndex = i % MAX_PER_PAGE;
            float x = (subIndex % COLS) * SLOT_WIDTH;
            float y = (subIndex / COLS) * SLOT_HEIGHT;

            ReceiptCanvasRenderer.draw(currentPage.getCanvas(), bills.get(i), x, y, SLOT_WIDTH, SLOT_HEIGHT, 12.0f, 9.5f, 11, true);
        }

        if (currentPage != null) pdfDoc.finishPage(currentPage);

        Uri targetUri = MediaStoreUtil.createTargetUri(context, outputFileName);
        if (targetUri == null) {
            pdfDoc.close();
            return;
        }

        try (OutputStream out = context.getContentResolver().openOutputStream(targetUri)) {
            if (out != null) {
                pdfDoc.writeTo(out);
                Toast.makeText(context, "Batch Invoices Grid Document Exported", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pdfDoc.close();
        }
    }

    public static void generateBillingSummaryReport(@NonNull Context context, @Nullable List<BillDTO> bills, @NonNull String startDate, @NonNull String endDate) {
        if (bills == null || bills.isEmpty()) return;

        String outputFileName = "Billing_Report_" + startDate + "_to_" + endDate + ".pdf";
        Uri targetUri = MediaStoreUtil.createTargetUri(context, outputFileName);
        if (targetUri == null) return;

        Document doc = new Document(PageSize.A4.rotate());
        try (OutputStream out = context.getContentResolver().openOutputStream(targetUri)) {
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("METER BILLING & UTILITY REPORT", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
            Paragraph subtitle = new Paragraph("Generated Range: " + startDate + " to " + endDate, new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC));
            subtitle.setSpacingAfter(20);
            doc.add(subtitle);

            PdfPTable table = new PdfPTable(11);
            table.setWidthPercentage(100);

            String[] headers = {"ID", "Room", "Tenant Name", "Bill Date", "Serial No.", "Prev", "Curr", "Rate", "Fixed", "Total", "Status"};
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            for (String h : headers) {
                table.addCell(createCell(h, headerFont, Element.ALIGN_CENTER));
            }

            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
            for (BillDTO b : bills) {
                table.addCell(createCell(String.valueOf(b.getId()), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createCell(b.getRoomNumber(), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createCell(b.getTenantName(), bodyFont, Element.ALIGN_LEFT));
                table.addCell(createCell(b.getBillingDate(), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createCell(b.getMeterSerialNumber(), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createCell(String.valueOf(b.getPreviousReading()), bodyFont, Element.ALIGN_RIGHT));
                table.addCell(createCell(String.valueOf(b.getCurrentReading()), bodyFont, Element.ALIGN_RIGHT));
                table.addCell(createCell(String.valueOf(b.getRatePerUnit()), bodyFont, Element.ALIGN_RIGHT));
                table.addCell(createCell(String.valueOf(b.getFixedCharge()), bodyFont, Element.ALIGN_RIGHT));
                table.addCell(createCell(String.valueOf(b.getTotalAmount()), bodyFont, Element.ALIGN_RIGHT));
                table.addCell(createCell(Double.toString(b.getBalance()), bodyFont, Element.ALIGN_CENTER));
            }

            doc.add(table);
            Toast.makeText(context, "Billing summary report compiled successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (doc.isOpen()) doc.close();
        }
    }

    private static PdfPCell createCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
    }
}