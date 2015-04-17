package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LogoutResponse;

import java.net.HttpURLConnection;

public class LogoutRequest extends SpiceRequest<LogoutResponse> {
    private String username;

    private Context context;

    public LogoutRequest(String username,Context context) {
        super(LogoutResponse.class);
        this.username = username;

        this.context = context;
    }

    @Override
    public LogoutResponse loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Users", "Logout", "POST");
        msc.addParameter("UserName", username);

        HttpURLConnection connection = msc.initConnection(false);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        LogoutResponse res = new LogoutResponse();
        return  res;
    }
}
