package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import android.util.Log;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class DeleteInAppNotificationItemRequest extends SpiceRequest<String> {
    private int notificationID, physicianID, practiceID;
    private boolean fromSentItems;
    private Context context;

    public DeleteInAppNotificationItemRequest(int notificationID, int practiceID, int physicianID, boolean fromSentItems, Context context) {
        super(String.class);
        this.notificationID = notificationID;
        this.physicianID = physicianID;
        this.fromSentItems = fromSentItems;
        this.context = context;
        this.practiceID = practiceID;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        if (!fromSentItems) {
            msc.buildBaseURI("Communication", "DeleteInAppNotificationInBoxItem", "GET");
        }
        else {
            msc.buildBaseURI("Communication", "DeleteInAppNotificationSentItem", "GET");
        }

        if (practiceID == 0) {
            // Having issues with older notifications, use practice ID from user data
            LoggedInDataStorage storage = new LoggedInDataStorage(context);
            HashMap<String, String> data = storage.RetrieveData();
            practiceID = Integer.parseInt(data.get("practiceID"));
        }
        msc.addParameter("notificationID", Integer.toString(notificationID));
        msc.addParameter("practiceID", Integer.toString(practiceID));
        msc.addParameter("physicianID", Integer.toString(physicianID));
        HttpURLConnection connection = msc.initConnection(true);
        String result = MedSecureConnection.getStringResult(connection);
        Log.w("API", "Result of deletion: " + result);
        connection.disconnect();
        return result;
    }
}
