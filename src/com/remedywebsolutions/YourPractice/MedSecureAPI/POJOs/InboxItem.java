package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

public class InboxItem extends MessageItem {
    public int notificationID;
    public String conversationID;
    public int toPhysicianID;
    public int fromPhysicianID;
    public String fromPhysicianName;
    public String subject;
    public String message;
    public String dateReceived;
    public String dateOpened;
}
