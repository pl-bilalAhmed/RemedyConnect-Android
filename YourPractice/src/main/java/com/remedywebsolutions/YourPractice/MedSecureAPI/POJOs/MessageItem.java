package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import java.io.Serializable;

public class MessageItem implements Serializable {
    public int notificationID;
    public String conversationID;
    public int practiceID;
    public int fromPhysicianID;
    public String subject;
    public String message;
    public Recipient[] recipients;

    public String getRecipients() {
        String result = "";
        if (recipients != null) {
            for (Recipient recipient : recipients) {
                if (result.isEmpty()) {
                    result = result.concat(recipient.physicianName);
                } else {
                    result = result.concat(", " + recipient.physicianName);
                }
            }
        }
        return result;
    }
}
