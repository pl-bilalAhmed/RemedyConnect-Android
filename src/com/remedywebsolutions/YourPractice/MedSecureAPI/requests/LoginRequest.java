package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.net.Uri;
import android.os.Build;
import com.octo.android.robospice.request.SpiceRequest;
import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;

public class LoginRequest extends SpiceRequest<String> {
    // @TODO This is now just a plain copy of the info in the MedSecureConnection.java, let's clean this up later
    static final String base = "https://MedSecureAPI.com/";
    static final String auth = "Basic em9sdGFuOnpvbHRhbjE=";
    static final String api_key = "SSB3aWxsIG1ha2UgbXkgQVBJIHNlY3VyZQ%3d%3d";
    static final String token = "j2w%2bjHHs%2bF8fkvr7Vj5DlPuYg8VqXvOhbtaG4WaOqxA%3d";
    static final String api_token_post = "apikey=" + api_key + "&token=" + token;
    static final String charset = "UTF-8";
    static final int readTimeout = 10000;
    static final int connectTimeout = 15000;

    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        super(String.class);
        this.username = username;
        this.password = password;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        // We should use this technique after the first testing:
        // With Uri.Builder class we can build our url is a safe manner
        /*
        Uri.Builder uriBuilder = Uri.parse(
                "http://robospice-sample.appspot.com/reverse").buildUpon();
        uriBuilder.appendQueryParameter("word", "my name is");

        String url = uriBuilder.build().toString();
        */

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }

        String urlToDownload = base +
                "api/Physician/GetPhysicianID?username=" + username + "&" +
                "password=" + password + "&" +
                api_token_post;
        String method = "GET";

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlToDownload)
                .openConnection();
        urlConnection.setReadTimeout(readTimeout);
        urlConnection.setConnectTimeout(connectTimeout);
        urlConnection.setRequestMethod(method);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty("Authorization", auth);
        urlConnection.setRequestProperty("Content-Type", "application/json");

        String physicianID = IOUtils.toString(urlConnection.getInputStream(), charset);
        urlConnection.disconnect();

        return physicianID;
    }
}
