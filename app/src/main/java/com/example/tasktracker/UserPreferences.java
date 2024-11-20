package com.example.tasktracker;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String PREFERENCES_NAME = "UserPreferences";
    private static final String ROLE_KEY = "userRole";
    private static final String EMAIL_KEY = "userEmail";

    public static void saveUserEmail(Context context, String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL_KEY, email);
        editor.apply();
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL_KEY, ""); // Default is -1 if no role is saved
    }

    public static void saveUserRole(Context context, int role) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ROLE_KEY, role);
        editor.apply();
    }

    public static int getUserRole(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(ROLE_KEY, -1); // Default is -1 if no role is saved
    }

    public static void clearPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
