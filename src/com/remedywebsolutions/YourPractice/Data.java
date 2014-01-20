package com.remedywebsolutions.YourPractice;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.io.File;

@SuppressWarnings("FinalStaticMethod")
public final class Data {
    public static final String ROOT = "https://cms.pediatricweb.com/mobile-app";
    public static final String PREFS_NAME = "PWPrefs";

    @SuppressWarnings("FinalStaticMethod")
    public static final boolean isDataAvailable(Context context) {
        boolean data_available = true;
        File file = context.getFileStreamPath("index.xml");
        if (!file.exists()) {
            data_available = false;
        }

        return data_available;
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final String GetFeedRoot(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("feedRoot", "");
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final String GetDesignPack(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("designPack", "");
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final void SetFeedRoot(Context context, String feedRoot) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("feedRoot", feedRoot);
        editor.commit();
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final void SetDesignPack(Context context, String designPack) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("designPack", designPack);
        editor.commit();
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final String SearchRootForPracticeName(String practiceName) {
        return ROOT + "?search=" + practiceName;
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final String SearchRootForPracticeLocation(Location location) {
        return ROOT + "?lon=" + location.getLongitude() + "&lat=" + location.getLatitude();
    }
}
