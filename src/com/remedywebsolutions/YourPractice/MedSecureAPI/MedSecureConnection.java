package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import com.pushio.manager.PushIOManager;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class MedSecureConnection  {
    // @TODO: check for internet connection

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

    // Push.IO related -------------------------------------------------------------------------------------------------
    public String getPushIOHash(String username) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = new byte[0];
        try {
            hash = digest.digest(username.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash.toString();
    }

    // Starts async login API task
    public void startAsyncLogin(String username, String password) {
        AsyncLoginAPITask task = new AsyncLoginAPITask();
        task.execute(new String[] { username, password });
    }

    // Async login API task
    public class AsyncLoginAPITask extends AsyncTask<String, Void, String> {
        String username;
        String password;

        @Override
        protected String doInBackground(String... params) {
            username = params[0];
            password = params[1];
            return getPhysicianID(username, password);
        }

        @Override
        protected void onPostExecute(String ownID) {
            // @TODO If successful, store credentials
            if (ownID.equals("0")) {
                Toast.makeText(context.getApplicationContext(),
                        "Bad username / password, please try again.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context.getApplicationContext(),
                        "Your physician ID is: " + ownID, Toast.LENGTH_LONG).show();
                new AsyncDeviceInsertTask().execute(new String[] {
                        ownID,
                        getPushIOHash(username),
                });
            }

        }
    }

    public void startAsyncLogout() {

    }

    public boolean stringIsInt(String str) {
        return str.matches("-?\\d+"); // Not the most elegant solution, but will do...
    }

    public class AsyncDeviceInsertTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return insertPhysicianMobileDevice(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (stringIsInt(result)) {
                Toast.makeText(context.getApplicationContext(),
                        "Successfully added device for in-app notifications.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context.getApplicationContext(),
                        "Couldn't add device for in-app notifications.", Toast.LENGTH_LONG).show();
            }
        }
    }
    // API calls -------------------------------------------------------------------------------------------------------

    // Method for fetching practice info
    public String getPractice(int practiceID) {
        try {
            return httpGetURL(base + "api/Practice/GetPractice?practiceID=" + practiceID + "&" +
                    api_token_post);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Method for fetching physician ID
    public String getPhysicianID(String username, String password) {
        try {
            return httpGetURL(base + "api/Physician/GetPhysicianID?username=" + username + "&" +
                    "password=" + password + "&" +
                    api_token_post);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Method for inserting mobile device for physician
    public String insertPhysicianMobileDevice(String physicianID, String deviceID) {
        try {
            return httpGetURL(base + "api/Physician/InsertPhysicianMobileDevice?physicianID=" + physicianID + "&" +
                    "deviceID=" + deviceID + "&" +
                    api_token_post);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Utilities -------------------------------------------------------------------------------------------------------

    private String readStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
        StringBuilder out = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            out.append(line);
        }
        return out.toString();
    }


    // @TODO Somehow we should check for the HTTP response type; typically, 400 - Bad request will be thrown on bad ones.
    private String httpURLWithMethod(String URLToDownload, String method) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(URLToDownload);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(readTimeout);
            connection.setConnectTimeout(connectTimeout);
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", auth);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();

            is = connection.getInputStream();

            String content = readStreamToString(is);
            return content;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String httpGetURL(String URLToDownload) throws IOException {
        return httpURLWithMethod(URLToDownload, "GET");
    }
}
