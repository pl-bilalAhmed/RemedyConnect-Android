package com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs;

import java.util.Map;

public class RecipientsResponseWrapper {
    public Map<String, String> getRecipients() {
        return recipients;
    }

    private Map<String,String> recipients;

    public RecipientsResponseWrapper(Map<String, String> recipients) {
        this.recipients = recipients;
    }
}
