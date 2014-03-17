package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

public class LoginResponse {
    private int physicianID, practiceID;
    private String token;

    public int getPhysicianID() {
        return physicianID;
    }

    public int getPracticeID() {
        return practiceID;
    }

    public String getToken() {
        return token;
    }
}
