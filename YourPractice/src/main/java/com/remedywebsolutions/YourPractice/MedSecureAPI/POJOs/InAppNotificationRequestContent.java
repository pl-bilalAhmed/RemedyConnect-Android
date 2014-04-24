package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import android.content.Context;

import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.R;

import java.util.HashMap;

public class InAppNotificationRequestContent {
    public Integer toPhysicianID;
    public String conversationID;
    public Integer practiceID;
    public Integer fromPhysicianID;
    public String fromPhysicianName;
    public String subject;
    public String message;

    public void fillWithSelfTestMessage(Context context) {
        LoggedInDataStorage storage = new LoggedInDataStorage(context);
        HashMap<String, String> userData = storage.RetrieveData();
        fromPhysicianID = Integer.parseInt(userData.get("physicianID"));
        practiceID = Integer.parseInt(userData.get("practiceID"));
        fromPhysicianName = userData.get("name");
        toPhysicianID = fromPhysicianID;
        subject = context.getString(R.string.test_inapp_notifications_subject);
        message = context.getString(R.string.test_inapp_notifications_message);
    }

    public void fillWithSelfTestMessageReply(Context context, String conversationID) {
        fillWithSelfTestMessage(context);
        this.conversationID = conversationID;
    }
}
