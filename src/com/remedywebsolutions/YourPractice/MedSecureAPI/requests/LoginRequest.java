package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.net.Uri;
import android.os.Build;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;

public class LoginRequest extends SpiceRequest<String> {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        super(String.class);
        this.username = username;
        this.password = password;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection();
        msc.buildBaseURI("Physician", "GetPhysicianID", "GET");
        msc.addParameter("username", username);
        msc.addParameter("password", password);
        HttpURLConnection connection = msc.initConnection();
        String physicianID = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        return physicianID;
    }
}
