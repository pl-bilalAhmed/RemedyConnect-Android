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

    public LoggedInDataStorage(Context context) {
        this.context = context;
    }

    public boolean isLoggedIn() {
        HashMap<String, String> userData = this.RetrieveData();
        if (userData.containsKey("physicianID")) {
            return Integer.parseInt(userData.get("physicianID")) != 0;
        }
        else return false;
    }

    public void StoreDataOnLogin(int physicianID, String token) {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("physicianID", physicianID);
        editor.putString("token", token);
        editor.commit();
    }

    public void StoreName(String name) {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", name);
        editor.commit();
    }

    public void StoreDeviceId(String deviceId) {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("deviceId", deviceId);
        editor.commit();
    }

    public HashMap<String, String> RetrieveData() {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        int physicianID = sp.getInt("physicianID", 0);
        String deviceID = sp.getString("deviceID", "");
        String token = sp.getString("token", "");
        String name = sp.getString("name", "");

        HashMap<String, String> userData = new HashMap<String, String>(3);
        userData.put("phyisicianID", Integer.toString(physicianID));
        userData.put("deviceID", deviceID);
        userData.put("token", token);
        userData.put("name", name);
        return userData;
    }

}
