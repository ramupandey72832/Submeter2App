package com.rohit.submeter2.utils;

import android.app.DatePickerDialog;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UiDateUtils {
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private static final SimpleDateFormat DB_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String formatDateForDisplay(Calendar calendar) {
        return DISPLAY_FORMAT.format(calendar.getTime());
    }

    public static String formatDateForDb(Calendar calendar) {
        return DB_FORMAT.format(calendar.getTime());
    }

    public static void showDatePicker(Context context, Calendar calendar, DatePickerDialog.OnDateSetListener listener) {
        if (context == null) return;
        new DatePickerDialog(
                context,
                listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /**
     * Converts a date string in "dd-MM-yyyy" format to a Calendar instance.
     *
     * @param dateStr The date string (e.g., "01-01-2026")
     * @return Calendar instance set to the parsed date, or null if invalid/empty.
     */
    public static Calendar parseDateToCalendar(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            Date date = sdf.parse(dateStr.trim());
            if (date != null) {
                calendar.setTime(date);
                return calendar;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // You can log or handle the parsing error here
        }

        return null;
    }
}
