package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderCallsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderPinTimeoutResponse;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class GetProviderPinTimeoutRequest extends SpiceRequest<GetProviderPinTimeoutResponse> {
    private Context context;

    public GetProviderPinTimeoutRequest(Context context) {
        super(GetProviderPinTimeoutResponse.class);
        this.context = context;
    }

    @Override
    public GetProviderPinTimeoutResponse loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String physicianID = userData.get("physicianID");
        String practiceID = userData.get("practiceID");

        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Physician", "GetPhysiciansPinTimeout", "GET");
        msc.addParameter("PracticeID", practiceID);
        msc.addParameter("PhysicianID", physicianID);

        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();

        GetProviderPinTimeoutResponse res = mapper.readValue(response, GetProviderPinTimeoutResponse.class);

        return res;
    }
}
