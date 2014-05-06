package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import android.util.Log;

import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;

import java.net.HttpURLConnection;

public class DeleteInAppNotificationByConversationRequest extends SpiceRequest<String> {
    private int physicianID, practiceID;
    private String conversationID;
    private boolean fromSentItems;
    private Context context;

    public DeleteInAppNotificationByConversationRequest(String conversationID,
                                                        int practiceID,
                                                        int physicianID,
                                                        boolean fromSentItems,
                                                        Context context) {
        super(String.class);
        this.conversationID = conversationID;
        this.physicianID = physicianID;
        this.fromSentItems = fromSentItems;
        this.context = context;
        this.practiceID = practiceID;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        if (!fromSentItems) {
            msc.buildBaseURI("Communication", "DeleteInAppNotificationInBoxByConversation", "GET");
        }
        else {
            msc.buildBaseURI("Communication", "DeleteInAppNotificationSentByConversation", "GET");
        }

        msc.addParameter("conversationID", conversationID);
        msc.addParameter("practiceID", Integer.toString(practiceID));
        msc.addParameter("physicianID", Integer.toString(physicianID));
        HttpURLConnection connection = msc.initConnection(true);
        String result = MedSecureConnection.getStringResult(connection);
        Log.w("API", "Result of deletion: " + result);
        connection.disconnect();
        return result;
    }
}
