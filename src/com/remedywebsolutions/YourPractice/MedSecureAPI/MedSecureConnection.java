package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.*;
import java.net.*;

public class MedSecureConnection  {
    // Configuration
    static final String base = "https://MedSecureAPI.com/";
    static final String auth = "Basic em9sdGFuOnpvbHRhbjE=";
    static final String api_key = "SSB3aWxsIG1ha2UgbXkgQVBJIHNlY3VyZQ%3d%3d";
    static final String token = "j2w%2bjHHs%2bF8fkvr7Vj5DlPuYg8VqXvOhbtaG4WaOqxA%3d";
    static final String api_token_post = "apikey=" + api_key + "&token=" + token;
    static final String charset = "UTF-8";
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

    // Starts sample async API task
    public void startAsyncGetPractice(Integer practiceID) {
        SampleAsyncAPITask task = new SampleAsyncAPITask();
        task.execute(new String[] { practiceID.toString() });
    }

    // Sample async API task
    public class SampleAsyncAPITask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return getPractice(Integer.parseInt(params[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }

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

    // Utilities
    private String readStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
        StringBuilder out = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            out.append(line);
        }
        return out.toString();
    }

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
