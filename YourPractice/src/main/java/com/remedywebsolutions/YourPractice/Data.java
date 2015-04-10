package com.remedywebsolutions.YourPractice;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import java.io.File;

@SuppressWarnings("FinalStaticMethod")
public final class Data {
    public static final String ROOT = "https://cms.pediatricweb.com/mobile-app";
    public static final String PREFS_NAME = "PWPrefs";
    public static final String mProviderMode = "provider";
    public static final String mPatientMode = "patient";

   // public static final String ADMIN_URL = "https://admin.remedyoncall.com";
    public static final String ADMIN_URL = "webteleservicestest.remedyconnect.com";

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
    public static final void SetProviderAppMode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mode", mProviderMode);
        editor.commit();
    }
    @SuppressWarnings("FinalStaticMethod")
    public static final void SetPatientAppMode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mode", mPatientMode);
        editor.commit();
    }
    @SuppressWarnings("FinalStaticMethod")
    public static final void ClearAppMode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("mode");
        editor.commit();
    }
    @SuppressWarnings("FinalStaticMethod")
    public static final Boolean IsRegistered(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        return settings.contains("registered");
    }

    @SuppressWarnings("FinalStaticMethod")
       public static final void SetRegistered(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("registered", "true");
        editor.commit();
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final void ClearRegistered(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.remove("registered");
        editor.commit();
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final Boolean AppModeSelected(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        return settings.contains("mode");
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final Boolean IsProviderMode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        if(settings.contains("mode")) {
            return settings.getString("mode", "").equals(mProviderMode);
        }
        else
        {
            return false;
        }
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
