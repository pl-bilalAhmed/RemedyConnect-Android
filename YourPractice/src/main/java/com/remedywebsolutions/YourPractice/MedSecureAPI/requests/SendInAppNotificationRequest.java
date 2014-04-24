package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SendInAppNotificationRequestResponse;

import java.net.HttpURLConnection;

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
        message = new InAppNotificationRequestContent();
        message.fillWithSelfTestMessage(context);
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

        return mapper.readValue(result,
                SendInAppNotificationRequestResponse.class);
    }
}
