package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;
import com.pushio.manager.PushIOManager;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MedSecureConnection  {
    // Configuration
    static final String base = "https://MedSecureAPI.com/";
    static final String auth = "Basic em9sdGFuOnpvbHRhbjE=";
    static final String api_key = "SSB3aWxsIG1ha2UgbXkgQVBJIHNlY3VyZQ%3d%3d";
    static final String token = "j2w%2bjHHs%2bF8fkvr7Vj5DlPuYg8VqXvOhbtaG4WaOqxA%3d";
    static final String api_token_post = "apikey=" + api_key + "&token=" + token;
    static final String charset = "UTF-8";
    static final String prefKey = "RemedyWebSolutionsYourPractice";
    static final int readTimeout = 10000;
    static final int connectTimeout = 15000;

    // Fields
    private Context context;

    // Setters / getters
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // Public interface

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

    //public getUsername

    // Push.IO related -------------------------------------------------------------------------------------------------
    public String getPushIOHash(String username) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = new byte[0];
        String result = "";
        try {
            hash = digest.digest(username.getBytes("UTF-8"));
            result = URLEncoder.encode(hash.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
