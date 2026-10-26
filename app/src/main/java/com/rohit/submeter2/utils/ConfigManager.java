package com.rohit.submeter2.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rohit.submeter2.R;
import com.example.rrallutilitylib.util.CryptoHelper;

public class ConfigManager {
    public static final String PUBLIC_PREFS = "db_public_config";
    private static final String KEY_TUTORIAL_SEEN = "isTutorialSeen";

    // --- Concrete Wrapper Methods (Recommended for specific features) ---

    public static boolean isTutorialSeen(Context context) {
        return getBoolean(context, KEY_TUTORIAL_SEEN, false);
    }

    public static void setTutorialSeen(Context context, boolean seen) {
        setBoolean(context, KEY_TUTORIAL_SEEN, seen);
    }

    public static String getDbUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE);
        String dbPath = context.getFilesDir().getAbsolutePath() + "/" + context.getString(R.string.default_db_name);
        String defaultUrl = context.getString(R.string.jdbc_prefix) + dbPath;
        return prefs.getString("db_url", defaultUrl);
    }

    public static String getDbDriver(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE);
        String defaultDriver = context.getString(R.string.default_db_driver);
        return prefs.getString("db_driver", defaultDriver);
    }

    public static String getSecretKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE);
        String defaultSecretKey = CryptoHelper.MY_SECRET_KEY;
        return prefs.getString("crypt_secret_key", defaultSecretKey);
    }

    public static void saveConfig(Context context, String url, String driver, String secretKey) {
        context.getSharedPreferences(PUBLIC_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString("db_url", url)
                .putString("db_driver", driver)
                .putString("crypt_secret_key", secretKey)
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