package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;

public class SentItemsResponse {
    @JsonUnwrapped
    public ArrayList<SentItem> sentItemsArray;
}
