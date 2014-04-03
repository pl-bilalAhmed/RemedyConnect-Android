package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.PushIOHelper;

import java.net.HttpURLConnection;

public class InsertPhysicianMobileDeviceRequest extends SpiceRequest<String> {
    private int userId, practiceId;
    private String username;
    private Context context;

    public InsertPhysicianMobileDeviceRequest(int userId, int practiceId, String username, Context context) {
        super(String.class);
        this.userId = userId;
        this.username = username;
        this.context = context;
        this.practiceId = practiceId;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        String pushIOHash = PushIOHelper.getDeviceIDHash(username);
        msc.buildBaseURI("Communication", "InsertPhysicianMobileDevice", "GET");
        msc.addParameter("physicianID", Integer.toString(userId));
        msc.addParameter("practiceID", Integer.toString(practiceId));
        msc.addParameter("deviceID", pushIOHash);
        HttpURLConnection connection = msc.initConnection(true);
        MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        return pushIOHash;
    }
}
