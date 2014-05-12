package com.remedywebsolutions.YourPractice.MedSecureAPI;

import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItemsResponse;

import java.util.HashMap;

/**
 * Class for message threads logic.
 *
 * The application gets the inbox and the sent items from the API and assembles those threads to
 * represent the current messaging state.
 */
public class MessageThreads {
    /**
     * A HashMap which associates the conversations with their IDs.
     */
    private HashMap<String, MessageThread> threads;

    /**
     * Assembles the threads from the API responses.
     * @param inboxItemsResponse The response for the inbox items call.
     * @param sentItemsResponse The response for the sent items call.
     */
    public MessageThreads(InboxItemsResponse inboxItemsResponse, SentItemsResponse sentItemsResponse) {
        sortThreads();
    }

    /**
     * Sorts the threads by date internally.
     */
    private void sortThreads() {

    }

    public int numOfThreads() {
        return threads.size();
    }

    public HashMap<String, MessageThread> getThreads() {
        return threads;
    }

    public MessageThread threadByConversationID(String conversationID) {
        return threads.get(conversationID);
    }
}
