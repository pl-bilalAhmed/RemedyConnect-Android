package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import java.io.Serializable;

/**
 * Created by ksciacca on 3/20/2015.
 */

public class SecureCallMessage implements Serializable{

    public String callerFirstName;
    public String callerLastName;
    public String callerTitle;
    public String phone;
    public String patientFirstName;
    public String patientLastName;
    public String patientDob;
    public String messageDate;
    public Boolean wasOpened;
    public String textMessage;
    public String message;
    public Integer callTypeId;
    public Boolean urgent;
    public Integer callID;
}
