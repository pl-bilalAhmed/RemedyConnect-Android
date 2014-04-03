package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

public class SentItem extends MessageItem {
    public int notificationID;
    public String conversationID;
    public int practiceID;
    public int toPhysicianID;
    public int fromPhysicianID;
    public String toPhysicianName;
    public String subject;
    public String message;
    public String dateSent;
    // @TODO No date received property? Hm...
}
