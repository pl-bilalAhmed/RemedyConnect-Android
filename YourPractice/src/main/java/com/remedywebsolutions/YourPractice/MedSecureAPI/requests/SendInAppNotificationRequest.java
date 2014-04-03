package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SendInAppNotificationRequestResponse;
import com.remedywebsolutions.YourPractice.R;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class SendInAppNotificationRequest extends SpiceRequest<SendInAppNotificationRequestResponse> {
    private Context context;
    private InAppNotificationRequestContent message;

    public SendInAppNotificationRequest(Context context, InAppNotificationRequestContent message) {
        super(SendInAppNotificationRequestResponse.class);
        this.context = context;
        this.message = message;
    }

    public SendInAppNotificationRequest(Context context) {
        super(SendInAppNotificationRequestResponse.class);
        this.context = context;
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();

        message = new InAppNotificationRequestContent();
        message.fromPhysicianID = Integer.parseInt(userData.get("physicianID"));
        message.practiceID = Integer.parseInt(userData.get("practiceID"));
        message.fromPhysicianName = userData.get("name");
        message.toPhysicianID = message.fromPhysicianID;
        message.subject = context.getString(R.string.test_inapp_notifications_subject);
        message.message = context.getString(R.string.test_inapp_notifications_message);
    }

    @Override
    public SendInAppNotificationRequestResponse loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Communication", "SendInAppNotification", "POST");

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(message);
        HttpURLConnection connection = msc.initConnection(true);
        msc.setupPOSTForJSONContent(connection, jsonString);
        String result = MedSecureConnection.getStringResult(connection);
        connection.disconnect();
        SendInAppNotificationRequestResponse response = mapper.readValue(result,
                SendInAppNotificationRequestResponse.class);

        return response;
    }
}
