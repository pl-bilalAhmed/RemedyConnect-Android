package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class GetInAppNotificationSentItemRequest extends SpiceRequest<SentItem> {
    private Context context;
    private int notificationID;

    public GetInAppNotificationSentItemRequest(Context context, int notificationID) {
        super(SentItem.class);
        this.context = context;
        this.notificationID = notificationID;
    }

    @Override
    public SentItem loadDataFromNetwork() throws Exception {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String physicianID = userData.get("physicianID");
        String practiceID = userData.get("practiceID");

        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Communication", "GetInAppNotificationSentItem", "GET");
        msc.addParameter("NotificationID", Integer.toString(notificationID));
        msc.addParameter("PracticeID", practiceID);
        msc.addParameter("PhysicianID", physicianID);

        HttpURLConnection connection = msc.initConnection(true);
        String response = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        SentItem result = mapper.readValue(response, SentItem.class);

        return result;
    }
}
