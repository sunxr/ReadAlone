package com.github.baby.owspace.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    public static int getPerfInt(Context context, final String key,
                                 final int defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }

    public static void setPrefInt(Context context, final String key,
                                  final int value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putInt(key, value).apply();
    }

    public static void setPrefString(Context context, final String key,
                                     final String value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).apply();
    }

    public static String getPerfString(Context context, final String key,
                                       final String defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

}
