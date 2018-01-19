package com.remedywebsolutions.YourPractice;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import java.io.File;

@SuppressWarnings("FinalStaticMethod")
public final class Data {
    public static final String ROOT = "https://cms.pediatricweb.com/mobile-app";
    public static final String PREFS_NAME = "PWPrefs";
    public static final String mProviderMode = "provider";
    public static final String mPatientMode = "patient";


    public static final String KEY_SHOULD_UPDATE_DATE = "update"; // If data should be updated on MainMenuActivity
    public static final String KEY_IS_BACKGROUND = "isForeground";
    public static final String KEY_TIME_BACKGROUND = "LeftTime"; //
    public static final String KEY_CURRENTPAGE = "CurrentPage"; //


    public static final String ADMIN_URL = "admin.remedyoncall.com";
    //   public static final String ADMIN_URL = "webteleservicestest.remedyconnect.com";

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
    public static final boolean IsPinSet(Context context) {
        if (com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock() != null) {
            return com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().isPasswordLocked();
        }
        return false;
    }


    public static final void setCurrentPage(Context context, String currentPage) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_CURRENTPAGE, currentPage);
        editor.commit();
    }

    public static final String getCurentPage(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String currentPage = settings.getString(KEY_CURRENTPAGE, "");
        return currentPage;
    }


    public static final void setShouldRefreshData(Context context, boolean shouldUpdate) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(KEY_SHOULD_UPDATE_DATE, shouldUpdate);
        editor.commit();
    }

    public static final boolean shouldRefreshData(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        boolean shouldUpdate = settings.getBoolean(KEY_SHOULD_UPDATE_DATE, false);
        return shouldUpdate;
    }

    public static final void setBackrgound(Context context, boolean isBackground) {
        Log.i(MainActivity.TAG, "Setting isBackground = " + isBackground);
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(KEY_IS_BACKGROUND, isBackground);
        editor.commit();
    }

    public static final boolean isBackground(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        boolean result = settings.getBoolean(KEY_IS_BACKGROUND, false);
        return result;
    }

    public static final void setBackgroundTime(Context context, long time) {
        Log.i(MainActivity.TAG, "Setting background time = " + time);
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(KEY_TIME_BACKGROUND, time);
        editor.commit();
    }

    public static final long getBackgroundTime(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        long result = settings.getLong(KEY_TIME_BACKGROUND, 0);
        return result;
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
        if (settings.contains("mode")) {
            return settings.getString("mode", "").equals(mProviderMode);
        } else {
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
