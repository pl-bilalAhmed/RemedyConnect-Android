package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.BaseResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderUnreadCallsResponse;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class MarkCallAsOpenedRequest extends SpiceRequest<BaseResponse> {
    private Context context;
    private Integer theCallId;
    public MarkCallAsOpenedRequest(Context context,Integer CallId) {
        super(BaseResponse.class);
        this.context = context;
        theCallId = CallId;
    }

    @Override
    public BaseResponse loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String physicianID = userData.get("physicianID");


        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Communication", "MarkSecureCallOpen", "POST");

        msc.addParameter("PhysicianID", physicianID);
        msc.addParameter("CallID", Integer.toString(theCallId));
        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();

        BaseResponse countResp = mapper.readValue(response, BaseResponse.class);

        return countResp;
    }
}
