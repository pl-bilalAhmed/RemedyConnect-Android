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
            String physicianIDstr = userData.get("physicianID");
            Integer physicianIDint = Integer.parseInt(physicianIDstr);
            return physicianIDint != 0;
        }
        else return false;
    }

    public void logOut() {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        sp.edit().clear().commit();
    }

    public void StoreDataOnLogin(int physicianID, int practiceID, String token) {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("physicianID", physicianID);
        editor.putInt("practiceID", practiceID);
        editor.putString("token", token);
        editor.commit();
    }

    public void StorePhysicians(String physiciansJSON) {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("physicians", physiciansJSON);
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

    public void StoreTimezoneOffset(Integer offset) {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("timezoneOffset", offset);
        editor.commit();
    }

    /**
     * Retrieves data from the shared preferences, where the user data is stored.
     *
     * @return A hashmap with the following keys: physicianID, practiceID, deviceID, token, name,
     *         physicians.
     */
    public HashMap<String, String> RetrieveData() {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        int physicianID = sp.getInt("physicianID", 0);
        int practiceID = sp.getInt("practiceID", 0);
        String deviceID = sp.getString("deviceID", "");
        String token = sp.getString("token", "");
        String name = sp.getString("name", "");
        String physicians = sp.getString("physicians", "");

        HashMap<String, String> userData = new HashMap<String, String>(6);
        userData.put("physicianID", Integer.toString(physicianID));
        userData.put("practiceID", Integer.toString(practiceID));
        userData.put("deviceID", deviceID);
        userData.put("token", token);
        userData.put("name", name);
        userData.put("physicians", physicians);
        return userData;
    }

}
