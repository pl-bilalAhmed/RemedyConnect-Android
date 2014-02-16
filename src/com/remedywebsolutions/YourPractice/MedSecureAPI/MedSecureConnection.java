package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.widget.Toast;
import com.pushio.manager.PushIOManager;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Class for handling connections with the MedSecure API.
 *
 * All implementation details (endpoints, connection settings etc.) should be here.
 */
public class MedSecureConnection  {
    static final String base = "https://MedSecureAPI.com/api/";
    static final String auth = "Basic em9sdGFuOnpvbHRhbjE=";
    static final String api_key = "SSB3aWxsIG1ha2UgbXkgQVBJIHNlY3VyZQ%3d%3d";
    static final String token = "j2w%2bjHHs%2bF8fkvr7Vj5DlPuYg8VqXvOhbtaG4WaOqxA%3d";
    static final String charset = "UTF-8";
    static final String prefKey = "RemedyWebSolutionsYourPractice";
    static final int readTimeout = 10000;
    static final int connectTimeout = 15000;

    // Fields
    private Context context;
    private Uri.Builder uriBuilder;
    private String method;

    /**
     * Constructor.
     */
    public MedSecureConnection() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
        uriBuilder = Uri.parse(base).buildUpon();
    }

    /**
     * Builds up base URI for the call.
     *
     * @param controllerName The name of the controller in the API.
     * @param functionName The name of the function in the API.
     * @param method The HTTP method for the function.
     */
    public void buildBaseURI(String controllerName, String functionName, String method) {
        uriBuilder.appendPath(controllerName);
        uriBuilder.appendPath(functionName);
        this.method = method;
    }

    /**
     * Adds a parameter to the call.
     *
     * @param parameterName The key of the parameter.
     * @param parameterValue The value of the parameter.
     */
    public void addParameter(String parameterName, String parameterValue) {
        uriBuilder.appendQueryParameter(parameterName, parameterValue);
    }

    /**
     * Adds default API parameters to the call.
     */
    private void addAPIParameters() {
        uriBuilder.appendQueryParameter("apikey", api_key);
        uriBuilder.appendQueryParameter("token", token);
    }

    /**
     * Initializes a HTTP connection with the default settings.
     *
     * @return The initialized HTTP connection.
     * @throws IOException
     */
    public HttpURLConnection initConnection() throws IOException {
        addAPIParameters();
        HttpURLConnection connection = (HttpURLConnection) new URL(uriBuilder.build().toString()).openConnection();
        connection.setReadTimeout(readTimeout);
        connection.setConnectTimeout(connectTimeout);
        connection.setRequestMethod(method);
        connection.setDoInput(true);
        connection.setRequestProperty("Authorization", auth);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    /**
     * Returns the entire response as a string.
     *
     * @param connection The connection to get the result.
     * @return The result.
     * @throws IOException
     */
    public static String getStringResult(HttpURLConnection connection) throws IOException {
        return IOUtils.toString(connection.getInputStream(), charset);
    }


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


}
