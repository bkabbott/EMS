package com.effinghamministorage.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREFS_NAME = "ems_prefs";
    private static final String KEY_TOKEN = "auth_token";

    private TokenManager() {}

    public static void saveToken(Context context, String token) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null);
    }

    public static void clearToken(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_TOKEN)
                .apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getToken(context) != null;
    }
}
