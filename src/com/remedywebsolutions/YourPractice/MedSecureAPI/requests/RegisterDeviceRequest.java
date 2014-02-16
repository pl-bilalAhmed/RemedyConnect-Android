package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;

import java.net.HttpURLConnection;

public class RegisterDeviceRequest extends SpiceRequest<String> {
    private String userId;
    private String username;

    public RegisterDeviceRequest(String userId, String username) {
        super(String.class);
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection();
        String deviceId = msc.getPushIOHash(username);
        msc.buildBaseURI("Physician", "InsertPhysicianMobileDevice", "GET");
        msc.addParameter("physicianID", userId);
        msc.addParameter("deviceID", deviceId);
        HttpURLConnection connection = msc.initConnection();
        String result = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        return deviceId;
    }
}
