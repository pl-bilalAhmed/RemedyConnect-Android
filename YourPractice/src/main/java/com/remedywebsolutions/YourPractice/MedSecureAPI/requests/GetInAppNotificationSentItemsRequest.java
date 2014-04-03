package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.*;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GetInAppNotificationSentItemsRequest extends SpiceRequest<SentItemsResponse> {
    private Context context;

    public GetInAppNotificationSentItemsRequest(Context context) {
        super(SentItemsResponse.class);
        this.context = context;
    }

    @Override
    public SentItemsResponse loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String physicianID = userData.get("physicianID");
        String practiceID = userData.get("practiceID");

        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Communication", "GetInAppNotificationSentItems", "GET");
        msc.addParameter("PracticeID", practiceID);
        msc.addParameter("PhysicianID", physicianID);

        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        SentItemsResponse result = new SentItemsResponse();
        SentItem[] sentItems = mapper.readValue(response, SentItem[].class);
        result.sentItemsArray = new ArrayList<SentItem>(Arrays.asList(sentItems));

        return result;
    }
}
