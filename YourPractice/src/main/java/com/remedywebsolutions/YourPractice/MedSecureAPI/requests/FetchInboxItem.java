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

public class FetchInboxItem extends SpiceRequest<InboxItem> {
    private Context context;
    private int notificationID;

    public FetchInboxItem(Context context, int notificationID) {
        super(InboxItem.class);
        this.context = context;
        this.notificationID = notificationID;
    }

    @Override
    public InboxItem loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String physicianID = userData.get("physicianID");

        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Physician", "GetInAppNotificationInBoxItem", "GET");
        msc.addParameter("NotificationID", Integer.toString(notificationID));
        msc.addParameter("PhysicianID", physicianID);

        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        InboxItem result = mapper.readValue(response, InboxItem.class);

        return result;
    }
}
