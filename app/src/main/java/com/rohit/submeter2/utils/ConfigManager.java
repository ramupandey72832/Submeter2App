package com.rohit.submeter2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rohit.submeter2.R;
import com.example.rrallutilitylib.util.CryptoHelper;

public class ConfigManager {
    public static final String PUBLIC_PREFS = "db_public_config";
    private static final String KEY_TUTORIAL_SEEN = "isTutorialSeen";
    private static final String KEY_MPIN = "KEY_MPIN";
    private static  final String KEY_DB_URL = "db_url";
    private static  final String KEY_DB_DRIVER = "db_driver";
    private static  final String KEY_CRYPTO_SECRET_KEY = "crypto_secret_key";


    //TODO Make KEYS FOR DBURL , DRIVER , SECRET KEY

    public static boolean isTutorialSeen(Context context) {
        return getBoolean(context, KEY_TUTORIAL_SEEN, false);
    }

    public static void setTutorialSeen(Context context, boolean seen) {
        setBoolean(context, KEY_TUTORIAL_SEEN, seen);
    }

    public static boolean isMPinConfigured(Context context) {
        String savedMpin = context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE)
                .getString(KEY_MPIN, null);
        return savedMpin != null && !savedMpin.trim().isEmpty();
    }

    public static String getMPin(Context context) {
        return context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE)
                .getString(KEY_MPIN, null);
    }

    // Optional: If you also save/update the MPIN elsewhere
    public static void saveMPin(Context context, String mpin) {
        context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_MPIN, mpin)
                .apply();
    }

    public static String getDbUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE);
        String dbPath = context.getFilesDir().getAbsolutePath() + "/" + context.getString(R.string.default_db_name);
        String defaultUrl = context.getString(R.string.jdbc_prefix) + dbPath;
        return prefs.getString(KEY_DB_URL, defaultUrl);
    }

    public static String getDbDriver(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE);
        String defaultDriver = context.getString(R.string.default_db_driver);
        return prefs.getString(KEY_DB_DRIVER, defaultDriver);
    }

    public static String getSecretKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE);
        String defaultSecretKey = CryptoHelper.MY_SECRET_KEY;
        return prefs.getString(KEY_CRYPTO_SECRET_KEY, defaultSecretKey);
    }

    public static void saveConfig(Context context, String url, String driver, String secretKey) {
        context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_DB_URL, url)
                .putString(KEY_DB_DRIVER, driver)
                .putString(KEY_CRYPTO_SECRET_KEY, secretKey)
                .apply();
    }

    public static boolean getBoolean(Context context, String keyName, boolean defaultValue) {
        return context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE)
                .getBoolean(keyName, defaultValue);
    }

    // Generic Boolean Setter
    public static void setBoolean(Context context, String keyName, boolean value) {
        context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(keyName, value)
                .apply();
    }
}