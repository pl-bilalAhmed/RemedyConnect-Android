package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import java.io.Serializable;

public class InboxItem implements Serializable {
    public int notificationID;
    public int toPhysicianID;
    public int fromPhysicianID;
    public String fromPhysicianName;
    public String subject;
    public String message;
    public String dateReceived;
    public String dateOpened;
}
