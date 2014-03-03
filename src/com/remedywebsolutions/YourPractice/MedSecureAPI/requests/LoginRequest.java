package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.responses.LoginResponse;

import java.net.HttpURLConnection;

public class LoginRequest extends SpiceRequest<LoginResponse> {
    private String username;
    private String password;
    private Context context;

    public LoginRequest(String username, String password, Context context) {
        super(LoginResponse.class);
        this.username = username;
        this.password = password;
        this.context = context;
    }

    @Override
    public LoginResponse loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Users", "Login", "GET");
        msc.addParameter("UserName", username);
        msc.addParameter("Password", password);
        HttpURLConnection connection = msc.initConnection(false);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        LoginResponse loginResponse = mapper.readValue(response, LoginResponse.class);
        return loginResponse;
    }
}
