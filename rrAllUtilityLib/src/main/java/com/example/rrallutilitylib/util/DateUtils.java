package com.example.rrallutilitylib.util;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    // Format displayed to users in the UI
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    // Format required for SQLite sorting and range queries
    private static final SimpleDateFormat DB_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * Converts a user-entered or picker-selected date string into the database storage format.
     */
    public static String toDatabaseFormat(String displayDateStr) {
        try {
            Date date = DISPLAY_FORMAT.parse(displayDateStr);
            return date != null ? DB_FORMAT.format(date) : displayDateStr;
        } catch (ParseException e) {
            // Fallback or return as-is if already formatted
            return displayDateStr;
        }
    }

    /**
     * Converts a database date string into the user-friendly UI display format.
     */
    public static String toDisplayFormat(String dbDateStr) {
        try {
            Date date = DB_FORMAT.parse(dbDateStr);
            return date != null ? DISPLAY_FORMAT.format(date) : dbDateStr;
        } catch (ParseException e) {
            return dbDateStr;
        }
    }

    /**
     * Formats a raw java.util.Date object into the database format.
     */
    public static String formatDateForDb(Date date) {
        return DB_FORMAT.format(date);
    }


}