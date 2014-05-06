package com.remedywebsolutions.YourPractice.MedSecureAPI.requests;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.SpiceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Recipient;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.RecipientsResponseWrapper;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class GetInAppNotificationRecipientsRequest extends SpiceRequest<RecipientsResponseWrapper> {
    private Context context;
    private String conversationID;

    public GetInAppNotificationRecipientsRequest(Context context, String conversationID) {
        super(RecipientsResponseWrapper.class);
        this.context = context;
        this.conversationID = conversationID;
    }

    @Override
    public RecipientsResponseWrapper loadDataFromNetwork() throws Exception {
        MedSecureConnection msc = new MedSecureConnection(context);
        msc.buildBaseURI("Communication", "GetInAppNotificationRecipients", "GET");
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        String practiceID = userData.get("practiceID");
        msc.addParameter("PracticeID", practiceID);
        msc.addParameter("ConversationID", conversationID);
        HttpURLConnection connection = msc.initConnection(true);
        String result = MedSecureConnection.getStringResult(connection);
        connection.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Recipient> recipientList = new ObjectMapper().readValue(result,
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, Recipient.class));
        RecipientsResponseWrapper recipientsWrapper = new RecipientsResponseWrapper();
        recipientsWrapper.recipients = recipientList;
        return recipientsWrapper;
    }
}
