package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;

public class PhysiciansResponse {
    @JsonUnwrapped
    public ArrayList<Physician> physicians;
}
