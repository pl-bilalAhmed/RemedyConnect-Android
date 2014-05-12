package com.remedywebsolutions.YourPractice.MedSecureAPI;

import java.util.Date;

/**
 * Class for message thread messages.
 *
 * This class is a subset of the MessageItem one intentionally to avoid storing extra information.
 */
public class MessageThreadMessage {
    private int fromPhysicianID;
    private String fromPhysicianName;
    private String message;
    private Date sentTime;
    private Boolean read;

    public MessageThreadMessage(int fromPhysicianID, String fromPhysicianName,
                                String message, Date sentTime, Boolean read) {
        this.fromPhysicianID = fromPhysicianID;
        this.fromPhysicianName = fromPhysicianName;
        this.message = message;
        this.sentTime = sentTime;
        this.read = read;
    }

    public int getFromPhysicianID() {
        return fromPhysicianID;
    }

    public String getFromPhysicianName() {
        return fromPhysicianName;
    }

    public String getMessage() {
        return message;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public Boolean getRead() {
        return read;
    }
}
