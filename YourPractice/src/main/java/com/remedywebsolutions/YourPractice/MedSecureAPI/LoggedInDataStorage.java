package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.remedywebsolutions.YourPractice.Data;

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

    public void StoreDataOnLogin(int physicianID, int practiceID, String token,String userName) {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("physicianID", physicianID);
        editor.putInt("practiceID", practiceID);
        editor.putString("token", token);
        editor.putString("username", userName);
        editor.commit();
    }

    public void StorePinTimeout(int timeout) {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("pinTimeout", timeout);
        editor.commit();
    }
    public int GetPinTimeout() {
        SharedPreferences sp = context.getSharedPreferences(prefKey, Activity.MODE_PRIVATE);
        int to = sp.getInt("pinTimeout", 0);
        if(to <=0)
        {
            return 60;
        }
        return to;
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
        int timezoneOffset = sp.getInt("timezoneOffset", 0);
        String deviceID = sp.getString("deviceID", "");
        String token = sp.getString("token", "");
        String name = sp.getString("name", "");
        String physicians = sp.getString("physicians", "");
        String username = sp.getString("username", "");
        HashMap<String, String> userData = new HashMap<String, String>(7);
        userData.put("physicianID", Integer.toString(physicianID));
        userData.put("practiceID", Integer.toString(practiceID));
        userData.put("deviceID", deviceID);
        userData.put("token", token);
        userData.put("name", name);
        userData.put("physicians", physicians);
        userData.put("timezoneOffset", Integer.toString(timezoneOffset));
        userData.put("username", username);
        return userData;
    }

}
