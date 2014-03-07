package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import java.io.Serializable;

public class SentItem implements Serializable {
    public int notificationID;
    public int toPhysicianID;
    public int fromPhysicianID;
    public String toPhysicianName;
    public String subject;
    public String message;
    public String dateSent;
}
