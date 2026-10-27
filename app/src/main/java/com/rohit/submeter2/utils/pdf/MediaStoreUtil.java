package com.rohit.submeter2.utils.pdf;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

public class MediaStoreUtil {

    private static final String MIME_PDF = "application/pdf";

    @NonNull
    public static Uri getCollectionUri() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? MediaStore.Downloads.EXTERNAL_CONTENT_URI
                : MediaStore.Files.getContentUri("external");
    }

    public static void deleteExistingFile(@NonNull Context context, @NonNull String fileName) {
        try {
            ContentResolver resolver = context.getContentResolver();
            resolver.delete(getCollectionUri(), MediaStore.MediaColumns.DISPLAY_NAME + " = ?", new String[]{fileName});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static Uri createTargetUri(@NonNull Context context, @NonNull String fileName) {
        deleteExistingFile(context, fileName);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, MIME_PDF);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        } else {
            File legacyFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            values.put(MediaStore.MediaColumns.DATA, legacyFile.getAbsolutePath());
        }

        return context.getContentResolver().insert(getCollectionUri(), values);
    }
}