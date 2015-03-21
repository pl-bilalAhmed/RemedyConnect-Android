package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderCallsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SecureCallMessage;


import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GetProviderCallsRequest extends SpiceRequest<GetProviderCallsResponse> {
    private Context context;

    public GetProviderCallsRequest(Context context) {
        super(GetProviderCallsResponse.class);
        this.context = context;
    }

    @Override
    public GetProviderCallsResponse loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String physicianID = userData.get("physicianID");
        String practiceID = userData.get("practiceID");

        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Communication", "GetCallsByProvider", "GET");

        msc.addParameter("PhysicianID", physicianID);
        msc.addParameter("PracticeID", practiceID);
        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        GetProviderCallsResponse result = new GetProviderCallsResponse();
        SecureCallMessage[] callItems = mapper.readValue(response, SecureCallMessage[].class);
        result.messages = new ArrayList<SecureCallMessage>(Arrays.asList(callItems));

        return result;
    }
}
