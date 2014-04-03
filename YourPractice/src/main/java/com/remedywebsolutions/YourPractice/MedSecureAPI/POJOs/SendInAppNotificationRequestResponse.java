package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

public class SendInAppNotificationRequestResponse {
    public int notificationID;
    public String conversationID, status;

    public boolean didSendMessageSuccessfully() {
        return !(conversationID.equals("00000000-0000-0000-0000-000000000000"));
    }
}
