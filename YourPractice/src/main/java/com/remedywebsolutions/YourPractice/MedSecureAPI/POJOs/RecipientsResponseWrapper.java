package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import java.util.ArrayList;

public class RecipientsResponseWrapper {
    public ArrayList<Recipient> recipients;

    public boolean containsPhysicianID(int physicianID) {
        for (Recipient r : recipients) {
            if (r.physicianID == physicianID) {
                return true;
            }
        }
        return false;
    }
}
