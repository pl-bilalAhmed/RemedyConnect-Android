package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GetInAppNotificationInboxItemsRequest extends SpiceRequest<InboxItemsResponse> {
    private Context context;

    public GetInAppNotificationInboxItemsRequest(Context context) {
        super(InboxItemsResponse.class);
        this.context = context;
    }

    @Override
    public InboxItemsResponse loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String physicianID = userData.get("physicianID");
        String practiceID = userData.get("practiceID");

        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Communication", "GetInAppNotificationInBoxItems", "GET");
        msc.addParameter("PracticeID", practiceID);
        msc.addParameter("PhysicianID", physicianID);

        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        InboxItemsResponse result = new InboxItemsResponse();
        InboxItem[] inboxItems = mapper.readValue(response, InboxItem[].class);
        result.inboxItemsArray = new ArrayList<InboxItem>(Arrays.asList(inboxItems));
        return result;
    }
}
