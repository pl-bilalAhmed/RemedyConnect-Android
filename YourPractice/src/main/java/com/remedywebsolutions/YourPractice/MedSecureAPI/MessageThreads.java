package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.util.Log;

import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItemsResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public MessageThreads(InboxItemsResponse inboxItemsResponse,
                          SentItemsResponse sentItemsResponse,
                          String ownName) {
        Log.d("Message threads", "Creating threads wrapper from " +
                inboxItemsResponse.inboxItemsArray.size() + " inbox items and " +
                sentItemsResponse.sentItemsArray.size() + " sent items");
        threads = new HashMap<String, MessageThread>();

        Log.d("Message threads", "Processing inbox...");
        for (InboxItem inboxItem : inboxItemsResponse.inboxItemsArray) {
            MessageThread thread = threadByConversationID(inboxItem.conversationID);
            Log.d("Message threads", "Checking whether conversation " + inboxItem.conversationID +
                    " exists");
            if (thread == null) {
                Log.d("Message threads", "No thread yet, creating new one");
                thread = new MessageThread(inboxItem.conversationID,
                        inboxItem.subject,
                        inboxItem.recipients);
                threads.put(inboxItem.conversationID, thread);
            }
            Log.d("Message threads", "Adding inbox message");
            thread.AddToThread(inboxItem);
        }
        Log.d("Message threads", "Sent items...");
        for (SentItem sentItem : sentItemsResponse.sentItemsArray) {
            MessageThread thread = threadByConversationID(sentItem.conversationID);
            Log.d("Message threads", "Checking whether conversation " + sentItem.conversationID +
                    " exists");
            if (thread == null) {
                Log.d("Message threads", "No thread yet, creating new one");
                thread = new MessageThread(sentItem.conversationID,
                        sentItem.subject,
                        sentItem.recipients);
                threads.put(sentItem.conversationID, thread);
            }
            Log.d("Message threads", "Adding sent item message");
            thread.AddToThread(sentItem, ownName);
        }
    }

    /**
     * Compares threads for descending order.
     */
    private class ThreadSortingComparator implements Comparator<MessageThread> {
        @Override
        public int compare(MessageThread lhs, MessageThread rhs) {
            return rhs.getLastUpdate().compareTo(lhs.getLastUpdate());
        }
    }

    /**
     * Sorts the threads by date internally.
     */
    private ArrayList<MessageThread> getSortedThreads() {
        ArrayList<MessageThread> threadsList = new ArrayList<MessageThread>(threads.values());
        Collections.sort(threadsList, new ThreadSortingComparator());
        return threadsList;
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
