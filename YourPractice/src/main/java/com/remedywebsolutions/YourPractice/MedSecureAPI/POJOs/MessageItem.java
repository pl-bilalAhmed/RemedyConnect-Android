package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageItem implements Serializable {
    public int notificationID;
    public String conversationID;
    public int practiceID;
    public int fromPhysicianID;
    public String subject;
    public String message;
    public Recipient[] recipients;

    public void filterSelfFromRecipients(int selfPhysicianID) {
        ArrayList<Recipient> recipientList = new ArrayList<Recipient>();
        for (Recipient recipient: recipients) {
            if (recipients.length == 1 ||
                    (recipients.length > 1 && recipient.physicianID != selfPhysicianID)) {
                recipientList.add(recipient);
            }
        }
        recipients = recipientList.toArray(new Recipient[0]);
    }

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
