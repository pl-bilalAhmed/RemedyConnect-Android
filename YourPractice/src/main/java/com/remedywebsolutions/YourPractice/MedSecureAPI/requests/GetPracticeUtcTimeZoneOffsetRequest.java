package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;

import java.net.HttpURLConnection;

public class GetPracticeUtcTimeZoneOffsetRequest extends SpiceRequest<Integer> {
    private int practiceID;
    private Context context;

    public GetPracticeUtcTimeZoneOffsetRequest(int practiceID, Context context) {
        super(Integer.class);
        this.context = context;
        this.practiceID = practiceID;
    }

    @Override
    public Integer loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Practice", "GetPracticeUtcTimeZoneOffset", "GET");
        msc.addParameter("PracticeID", Integer.toString(practiceID));
        HttpURLConnection connection = msc.initConnection(true);
        Integer result = Integer.parseInt(MedSecureConnection.getStringResult(connection));
        connection.disconnect();
        return result;
    }
}
