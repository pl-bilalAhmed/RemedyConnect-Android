package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.R;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.UUID;

public class SendInAppNotificationRequest extends SpiceRequest<String> {
    private Context context;
    private InAppNotificationRequestContent message;

    public SendInAppNotificationRequest(Context context, InAppNotificationRequestContent message) {
        super(String.class);
        this.context = context;
        this.message = message;
    }

    public SendInAppNotificationRequest(Context context) {
        super(String.class);
        this.context = context;
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();

        message = new InAppNotificationRequestContent();
        message.fromPhysicianID = Integer.parseInt(userData.get("physicianID"));
        message.fromPhysicianName = userData.get("name");
        message.toPhysicianID = message.fromPhysicianID;
        message.subject = context.getString(R.string.test_inapp_notifications_subject);
        message.message = context.getString(R.string.test_inapp_notifications_message);
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Physician", "SendInAppNotification", "POST");

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(message);
        HttpURLConnection connection = msc.initConnection(true);
        msc.setupPOSTForJSONContent(connection, jsonString);
        String result = MedSecureConnection.getStringResult(connection);
        Log.d("API", result);
        connection.disconnect();

        return result;
    }
}
