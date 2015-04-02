package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderCallsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderUnreadCallsResponse;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class GetProviderUnreadCallsRequest extends SpiceRequest<GetProviderUnreadCallsResponse> {
    private Context context;

    public GetProviderUnreadCallsRequest(Context context) {
        super(GetProviderUnreadCallsResponse.class);
        this.context = context;
    }

    @Override
    public GetProviderUnreadCallsResponse loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String physicianID = userData.get("physicianID");


        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Communication", "GetUnreadCalls", "GET");

        msc.addParameter("PhysicianID", physicianID);

        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();

        GetProviderUnreadCallsResponse countResp = mapper.readValue(response, GetProviderUnreadCallsResponse.class);

        return countResp;
    }
}
