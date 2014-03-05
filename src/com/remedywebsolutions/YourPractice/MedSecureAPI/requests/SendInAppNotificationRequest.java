package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.PushIOHelper;

import java.net.HttpURLConnection;

public class SendInAppNotificationRequest extends SpiceRequest<String> {
    private Context context;

    public SendInAppNotificationRequest(Context context) {
        super(String.class);
        this.context = context;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Physician", "SendInAppNotification", "POST");

        // Build a testing POJO:
        InAppNotificationRequestContent message = new InAppNotificationRequestContent();
        message.fromPhysicianID = 405;
        message.fromPhysicianName = "Tester";
        message.toPhysicianID = 405;
        message.subject = "Oh, hi there!";
        message.message = "This is a completely different test message. :)";
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(message);
        Log.d("YourPractice", "Test JSON content: " + jsonString);
        HttpURLConnection connection = msc.initConnection(true);
        msc.setupPOSTForJSONContent(connection, jsonString);
        String result = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        return result;
    }
}
