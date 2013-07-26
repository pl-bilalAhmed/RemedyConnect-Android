package com.newpush.greenwoodpediatrics;


import android.content.Context;
import android.content.SharedPreferences;

public class Data {
    public static final String ROOT = "https://cms.pediatricweb.com/mobile-app";
    public static final String PREFS_NAME = "PWPrefs";

    public static final String GetFeedRoot(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("feedRoot", "");
    }

    public static final void SetFeedRoot(Context context, String feedRoot) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("feedRoot", feedRoot);
        editor.commit();
    }

    public static final String SearchRootForPracticeName(String practiceName) {
        return ROOT + "?search=" + practiceName;
    }
}
