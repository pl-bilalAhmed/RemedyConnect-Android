package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.os.Build;
import com.octo.android.robospice.request.SpiceRequest;
import com.pushio.manager.PushIOManager;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterDeviceRequest extends SpiceRequest<String> {
    // @TODO This is now just a plain copy of the info in the MedSecureConnection.java, let's clean this up later
    static final String base = "https://MedSecureAPI.com/";
    static final String auth = "Basic em9sdGFuOnpvbHRhbjE=";
    static final String api_key = "SSB3aWxsIG1ha2UgbXkgQVBJIHNlY3VyZQ%3d%3d";
    static final String token = "j2w%2bjHHs%2bF8fkvr7Vj5DlPuYg8VqXvOhbtaG4WaOqxA%3d";
    static final String api_token_post = "apikey=" + api_key + "&token=" + token;
    static final String charset = "UTF-8";
    static final int readTimeout = 10000;
    static final int connectTimeout = 15000;

    private String userId;
    private String username;

    public RegisterDeviceRequest(String userId, String username) {
        super(String.class);
        this.userId = userId;
        this.username = username;
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

        MedSecureConnection ms = new MedSecureConnection();
        String deviceId = ms.getPushIOHash(username);

        String urlToDownload = base +
                "api/Physician/InsertPhysicianMobileDevice?physicianID=" + userId + "&" +
                "deviceID=" + deviceId + "&" +
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

        String result = IOUtils.toString(urlConnection.getInputStream(), charset);
        urlConnection.disconnect();

        return deviceId;
    }
}
