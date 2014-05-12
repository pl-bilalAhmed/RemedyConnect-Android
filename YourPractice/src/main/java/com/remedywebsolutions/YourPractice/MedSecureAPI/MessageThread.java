package com.remedywebsolutions.YourPractice.MedSecureAPI;

import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Recipient;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class for a message thread.
 */
public class MessageThread {
    private String conversationID;
    private String subject;
    private Recipient[] recipients;
    private Date lastUpdate;
    private ArrayList<MessageThreadMessage> messages;

    public MessageThread(String conversationID, String subject, Recipient[] recipients, Date lastUpdate) {
        this.conversationID = conversationID;
        this.subject = subject;
        this.recipients = recipients;
        messages = new ArrayList<MessageThreadMessage>();
    }

    public void AddToThread(InboxItem inboxItem) {

    }

    public void AddToThread(SentItem sentItem) {

    }

    private void sortThread() {

    }

    public String getConversationID() {
        return conversationID;
    }

    public String getSubject() {
        return subject;
    }

    public Recipient[] getRecipients() {
        return recipients;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public ArrayList<MessageThreadMessage> getMessages() {
        return messages;
    }

    public int numOfMessages() {
        return messages.size();
    }
}
