package com.remedywebsolutions.YourPractice.MedSecureAPI;

import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Recipient;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Class for a message thread.
 */
public class MessageThread implements Serializable {
    private String conversationID;
    private String subject;
    private Recipient[] recipients;
    private Date lastUpdate;
    private ArrayList<MessageThreadMessage> messages;
    private Boolean sorted;

    public MessageThread(String conversationID, String subject, Recipient[] recipients) {
        this.conversationID = conversationID;
        this.subject = subject;
        this.recipients = recipients;
        this.lastUpdate = null;
        this.sorted = false;
        messages = new ArrayList<MessageThreadMessage>();
    }

    public void AddToThread(InboxItem inboxItem, int ownPhysicianID) {
        try {
            // Only add the message to the thread if it's not our own: for those, it makes more
            // sense to display as sent items.
            if (inboxItem.fromPhysicianID != ownPhysicianID) {
                MessageThreadMessage msg = new MessageThreadMessage(inboxItem.fromPhysicianID,
                        inboxItem.fromPhysicianName, inboxItem.message,
                        DateOperations.parseDate(inboxItem.dateReceived), inboxItem.dateOpened != null);
                messages.add(msg);
                if (lastUpdate == null || lastUpdate.compareTo(msg.getSentTime()) < 0) {
                    lastUpdate = msg.getSentTime();
                }
                this.sorted = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void AddToThread(SentItem sentItem, String ownName) {
        try {
            MessageThreadMessage msg = new MessageThreadMessage(sentItem.fromPhysicianID,
                    ownName, sentItem.message,
                    DateOperations.parseDate(sentItem.dateSent), sentItem.dateRead != null);
            messages.add(msg);
            if (lastUpdate == null || lastUpdate.compareTo(msg.getSentTime()) < 0) {
                lastUpdate = msg.getSentTime();
            }
            this.sorted = false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compares messages for ascending order.
     */
    private class MessageSortingComparator implements Comparator<MessageThreadMessage> {
        @Override
        public int compare(MessageThreadMessage lhs, MessageThreadMessage rhs) {
            return lhs.getSentTime().compareTo(rhs.getSentTime());
        }
    }

    private void sortThread() {
        Collections.sort(messages, new MessageSortingComparator());
        this.sorted = true;
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
        if (!sorted) {
            sortThread();
        }
        return messages;
    }

    public boolean hasBeenRead() {
        for (MessageThreadMessage message: messages) {
            if (!message.getRead()) {
                return false;
            }
        }
        return true;
    }

    public String getRecipientNameList() {
        ArrayList<String> names = new ArrayList<String>(recipients.length);
        for (Recipient recipient: recipients) {
            names.add(recipient.physicianName);
        }
        return StringUtils.join(names, ", ");
    }

    public int numOfMessages() {
        return messages.size();
    }
}
