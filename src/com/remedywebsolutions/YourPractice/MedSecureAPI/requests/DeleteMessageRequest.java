package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;

import java.net.HttpURLConnection;

public class DeleteMessageRequest extends SpiceRequest<String> {
    private int notificationID, physicianID;
    private boolean fromSentItems;
    private Context context;

    public DeleteMessageRequest(int notificationID, int physicianID, boolean fromSentItems, Context context) {
        super(String.class);
        this.notificationID = notificationID;
        this.physicianID = physicianID;
        this.fromSentItems = fromSentItems;
        this.context = context;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        if (!fromSentItems) {
            msc.buildBaseURI("Physician", "DeleteInAppNotificationInBoxItem", "GET");
        }
        else {
            msc.buildBaseURI("Physician", "DeleteInAppNotificationSentItem", "GET");
        }

        msc.addParameter("notificationID", Integer.toString(notificationID));
        msc.addParameter("physicianID", Integer.toString(physicianID));
        HttpURLConnection connection = msc.initConnection(true);
        String result = MedSecureConnection.getStringResult(connection);
        connection.disconnect();
        return result;
    }
}
