package com.effinghamministorage.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {

    private static final String TAG = "TokenManager";
    private static final String PREFS_NAME = "ems_secure_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_PHONE = "tenant_phone";

    private TokenManager() {}

    private static SharedPreferences getEncryptedPrefs(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.w(TAG, "Failed to create encrypted prefs, falling back to plain SharedPreferences", e);
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveToken(Context context, String token) {
        getEncryptedPrefs(context)
                .edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public static String getToken(Context context) {
        return getEncryptedPrefs(context)
                .getString(KEY_TOKEN, null);
    }

    public static void savePhone(Context context, String phone) {
        getEncryptedPrefs(context)
                .edit()
                .putString(KEY_PHONE, phone)
                .apply();
    }

    public static String getPhone(Context context) {
        return getEncryptedPrefs(context)
                .getString(KEY_PHONE, null);
    }

    public static void clearToken(Context context) {
        getEncryptedPrefs(context)
                .edit()
                .remove(KEY_TOKEN)
                .remove(KEY_PHONE)
                .apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getToken(context) != null;
    }
}
