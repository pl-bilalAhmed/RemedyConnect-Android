package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Stores data for logged in users.
 */
public class LoggedInDataStorage {
    private Context context;
    static final String prefKey = "RemedyWebSolutionsYourPractice";

    // Setters / getters
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static boolean isLoggedIn() {
        return false;
    }

    // Storing data about users currently logged in ---------------------------------------------------------------------
    public void fetchUserDataForStoring() {

    }

    public void StoreData() {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("physicianID", 0);
        editor.putString("deviceID", "");
        editor.putString("name", "");
        editor.commit();
    }

    public HashMap<String, String> RetrieveData() {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        int myIntValue = sp.getInt("your_int_key", -1);

        HashMap<String, String> userData = new HashMap<String, String>(5);
        return userData;
    }

}
