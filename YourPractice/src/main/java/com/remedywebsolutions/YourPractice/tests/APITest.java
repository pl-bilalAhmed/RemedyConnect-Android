package com.remedywebsolutions.YourPractice.tests;

import android.content.Intent;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.remedywebsolutions.YourPractice.LoginActivity;
import com.remedywebsolutions.YourPractice.MainViewController;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThread;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThreadMessage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThreads;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationGroupRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.RecipientsResponseWrapper;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SendInAppNotificationRequestResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.DeleteInAppNotificationByConversationRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationInboxItemsRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationRecipientsRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationSentItemsRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPhysiciansRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPracticeUtcTimeZoneOffsetRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.InsertPhysicianMobileDeviceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.NewInAppGroupNotification;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.ReplyToInAppGroupNotificationRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

import java.util.Date;

public class APITest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private LoginActivity loginActivity;
    private LoggedInDataStorage dataStorage;
    private int physicianID, practiceID;
    private String name;

    public APITest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        Intent intent = MainViewController.GetRootIntent(getInstrumentation().getTargetContext());
        setActivityIntent(intent);
        loginActivity = getActivity();
        dataStorage = new LoggedInDataStorage(loginActivity);
        dataStorage.logOut();
    }

    @Override
    public void tearDown() throws Exception {
        dataStorage.logOut();
        super.tearDown();
    }

    /**
     * Performs a basic check before we start testing: we need a working internet connection.
     * @throws Exception
     */
    public void testPreConditions() throws Exception {
        assertTrue("App is not online...?", loginActivity.isOnline());
    }

    /**
     * Tests the login scenario.
     * @throws Exception
     */
    public void testLogin() throws Exception {
        LoginResponse loginResponse = login();
        registerDevice(loginResponse);
        pullPhysicians();
        getPracticeTimezoneOffset();
    }

    /**
     * Self-messaging test scenario.
     *
     * Sends a message to self with two replies, then deletes the conversation.
     * @throws Exception
     */
    public void testMessageToSelfAndDelete() throws Exception {
        LoginResponse loginResponse = login();
        registerDevice(loginResponse);
        pullPhysicians();
        getPracticeTimezoneOffset();
        SendInAppNotificationRequestResponse result = sendTestMessageToSelf();
        String conversationID = result.conversationID;
        sendTestMessageToSelfReply(conversationID);
        sendTestMessageToSelfReply(conversationID);
        deleteTestMessages(conversationID);
    }

    /**
     * Self-messaging test scenario with threading.
     *
     * Sends a message to self with two replies, then deletes the conversation while checking how
     * the threading works.
     * @throws Exception
     */
    public void testMessageToSelfAndDeleteWithThreading() throws Exception {
        LoginResponse loginResponse = login();
        registerDevice(loginResponse);
        pullPhysicians();
        getPracticeTimezoneOffset();

        MessageThreads threads = assembleMessageThreads();
        int numOfThreads = threads.numOfThreads();

        SendInAppNotificationRequestResponse result = sendTestMessageToSelf();
        String conversationID = result.conversationID;
        threads = assembleMessageThreads();
        assertTrue("Num of threads hasn't increased", threads.numOfThreads() == numOfThreads + 1);
        MessageThread thread = threads.threadByConversationID(conversationID);
        assertNotNull("Thread should exists in the threads", thread);
        assertTrue("Num of messages should be 2 here", thread.numOfMessages() == 2);

        sendTestMessageToSelfReply(conversationID);
        threads = assembleMessageThreads();
        thread = threads.threadByConversationID(conversationID);
        assertTrue("Num of messages should be 4 here", thread.numOfMessages() == 4);

        sendTestMessageToSelfReply(conversationID);
        threads = assembleMessageThreads();
        thread = threads.threadByConversationID(conversationID);
        assertTrue("Num of messages should be 6 here", thread.numOfMessages() == 6);

        checkThreadSorting(thread);

        deleteTestMessages(conversationID);
        threads = assembleMessageThreads();
        thread = threads.threadByConversationID(conversationID);
        assertNull("Thread exists in the threads after deletion", thread);
    }

    /**
     * Self-messaging in a group test scenario with threading.
     *
     * Sends a message to self with two replies, then deletes the conversation while checking how
     * the threading works.
     * @throws Exception
     */
    public void testGroupMessageToSelfAndDeleteWithThreading() throws Exception {
        LoginResponse loginResponse = login();
        registerDevice(loginResponse);
        pullPhysicians();
        getPracticeTimezoneOffset();

        MessageThreads threads = assembleMessageThreads();
        int numOfThreads = threads.numOfThreads();

        SendInAppNotificationRequestResponse result = sendGroupMessage(false);
        String conversationID = result.conversationID;
        threads = assembleMessageThreads();
        assertTrue("Num of threads hasn't increased", threads.numOfThreads() == numOfThreads + 1);
        MessageThread thread = threads.threadByConversationID(conversationID);
        assertNotNull("Thread should exists in the threads", thread);
        assertTrue("Num of messages should be 2 here", thread.numOfMessages() == 2);

        SystemClock.sleep(1500);
        replyToGroupMessage(conversationID, "First reply");
        threads = assembleMessageThreads();
        thread = threads.threadByConversationID(conversationID);
        assertTrue("Num of messages should be 4 here", thread.numOfMessages() == 4);

        SystemClock.sleep(1500);
        replyToGroupMessage(conversationID, "Second reply");
        threads = assembleMessageThreads();
        thread = threads.threadByConversationID(conversationID);
        assertTrue("Num of messages should be 6 here", thread.numOfMessages() == 6);

        checkThreadSorting(thread);

        deleteTestMessages(conversationID);
        threads = assembleMessageThreads();
        thread = threads.threadByConversationID(conversationID);
        assertNull("Thread exists in the threads after deletion", thread);
    }


    /**
     * Checks whether a thread's messages are well-sorted by sent date.
     * @param thread The thread to check.
     */
    private void checkThreadSorting(MessageThread thread) {
        Date startingDate = null;
        for (MessageThreadMessage message: thread.getMessages()) {
            assertNotNull("Sent time shouldn't be null", message.getSentTime());
            Date sentTime = message.getSentTime();
            assertTrue("Message thread should be sorted by date in ascending order",
                    startingDate == null || sentTime.compareTo(startingDate) >= 0);
            startingDate = sentTime;
        }
    }

    /**
     * Group messaging test scenario.
     *
     * Sends a message to a group with three replies, then deletes the conversation.
     * @throws Exception
     */
    public void testSendGroupMessage() throws Exception {
        LoginResponse loginResponse = login();
        registerDevice(loginResponse);
        pullPhysicians();
        getPracticeTimezoneOffset();
        SendInAppNotificationRequestResponse result = sendGroupMessage(false);
        String conversationID = result.conversationID;
        getGroupMessageRecipients(conversationID);
        replyToGroupMessage(conversationID, "First reply");
        replyToGroupMessage(conversationID, "Second reply");
        replyToGroupMessage(conversationID, "Third reply");
        deleteTestMessages(conversationID);
    }

    /**
     * Checks whether a string is a hexadecimal representation of a number.
     * @param str The string to check.
     * @return True if the string is a hexadecimal number, false otherwise.
     */
    private boolean isHex(String str) {
        return str.matches("\\p{XDigit}+");
    }

    /**
     * Performs a login.
     * @return The login response from the API.
     * @throws Exception
     */
    private LoginResponse login() throws Exception {
        LoginRequest loginReq = new LoginRequest("zoltan", "zoltan1", loginActivity);
        LoginResponse result = loginReq.loadDataFromNetwork();
        assertEquals("Practice ID does not match", 36, result.getPracticeID());
        assertEquals("Physician ID does not match", 405, result.getPhysicianID());
        assertTrue("Token has zero length", result.getToken().length() > 0);
        assertTrue("Token isn't well-formatted", isHex(result.getToken()));
        loginActivity.disablePasscode();
        return result;
    }

    /**
     * Registers the device.
     * @param loginResponse The earlier login response to use.
     * @return The returned device ID.
     * @throws Exception
     */
    private String registerDevice(LoginResponse loginResponse) throws Exception {
        practiceID = loginResponse.getPracticeID();
        physicianID = loginResponse.getPhysicianID();
        String token = loginResponse.getToken();
        dataStorage.StoreDataOnLogin(physicianID, practiceID, token);
        InsertPhysicianMobileDeviceRequest registerReq =
                new InsertPhysicianMobileDeviceRequest(physicianID, practiceID,
                                                        "zoltan", loginActivity);
        String response = registerReq.loadDataFromNetwork();
        assertNotNull("Doesn't have response", response.length() > 0);
        assertTrue("The device ID is not well-formatted", isHex(response));
        dataStorage.StoreDeviceId(response);
        return response;
    }

    /**
     * Gets the list of the physicians in the practice.
     * @throws Exception
     */
    private void pullPhysicians() throws Exception {
        GetPhysiciansRequest pullContactsReq = new GetPhysiciansRequest(loginActivity);
        PhysiciansResponse physicians = pullContactsReq.loadDataFromNetwork();
        assertNotNull("No contacts", physicians.physicians.size() == 0);
        assertTrue("Less than expected number of physicians: got " +
                        physicians.physicians.size() + ", which is less than four",
                physicians.physicians.size() >= 4);
        name = loginActivity.getAndSetNameFromResponse(physicians, physicianID);
        assertNotNull("Name is not set", name);
        assertEquals("Name does not match", name, "Zoltan Adamek, MD");
    }

    /**
     * Gets the timezone offset for the actual practice.
     * @throws Exception
     */
    private void getPracticeTimezoneOffset() throws Exception {
        GetPracticeUtcTimeZoneOffsetRequest req =
                new GetPracticeUtcTimeZoneOffsetRequest(practiceID, loginActivity);
        Integer offset = req.loadDataFromNetwork();
        assertTrue("Timezone offset differs from expected: got " + offset.toString() ,
                offset == 6 || offset == 7);
        dataStorage.StoreTimezoneOffset(offset);
    }

    /**
     * Sends a test message to self.
     *
     * @return The notification ID.
     * @throws Exception
     */
    private SendInAppNotificationRequestResponse sendTestMessageToSelf() throws Exception {
        SendInAppNotificationRequest req = new SendInAppNotificationRequest(loginActivity);
        SendInAppNotificationRequestResponse result = req.loadDataFromNetwork();
        assertTrue("Couldn't send message, failed with status " + result.status,
                result.didSendMessageSuccessfully());
        return result;
    }

    /**
     * Sends reply to the earlier self-test message.
     * @param conversationID The conversation ID used.
     * @return The response from the API.
     * @throws Exception
     */
    private SendInAppNotificationRequestResponse sendTestMessageToSelfReply(String conversationID)
                                                                                throws Exception {

        InAppNotificationRequestContent message = new InAppNotificationRequestContent();
        message.fillWithSelfTestMessageReply(loginActivity, conversationID);
        SendInAppNotificationRequest req = new SendInAppNotificationRequest(loginActivity, message);
        SendInAppNotificationRequestResponse result = req.loadDataFromNetwork();
        Log.d("Message threads", "Reply sent for conversation ID " + conversationID +
                ", got reply for " + result.conversationID);
        assertTrue("Couldn't send message, failed with status " + result.status,
                result.didSendMessageSuccessfully());
        assertEquals("Reply should have the same conversation ID", conversationID, result.conversationID);
        return result;
    }

    /**
     * Deletes test messages by their conversation ID, both from the inbox and from the sent items.
     * @param conversationID The conversation ID used.
     * @throws Exception
     */
    private void deleteTestMessages(String conversationID) throws Exception {
        DeleteInAppNotificationByConversationRequest req;
        req = new DeleteInAppNotificationByConversationRequest(
                conversationID, practiceID, physicianID, false, loginActivity
        );
        String result = req.loadDataFromNetwork();
        assertTrue("Failed to delete inbox message", result.equals("true"));

        req = new DeleteInAppNotificationByConversationRequest(
                conversationID, practiceID, physicianID, true, loginActivity
        );
        result = req.loadDataFromNetwork();
        assertTrue("Failed to delete sent message", result.equals("true"));
    }

    /**
     * Sends a new group message.
     * @param selfOnly Only sends message to self (but the message is still a group message)
     * @return The response from the API.
     * @throws Exception
     */
    private SendInAppNotificationRequestResponse sendGroupMessage(boolean selfOnly) throws Exception {
        NewInAppGroupNotification req = new NewInAppGroupNotification(loginActivity, selfOnly);
        SendInAppNotificationRequestResponse result = req.loadDataFromNetwork();
        assertTrue("Couldn't send message, failed with status " + result.status,
                result.didSendMessageSuccessfully());
        return result;
    }

    /**
     * Checks whether the recipients can be fetched correctly for a given conversation.
     * @param conversationID The conversation ID of the conversation to check.
     * @throws Exception
     */
    private void getGroupMessageRecipients(String conversationID) throws Exception {
        GetInAppNotificationRecipientsRequest req =
                new GetInAppNotificationRecipientsRequest(loginActivity, conversationID);
        RecipientsResponseWrapper recipientsWrapper = req.loadDataFromNetwork();
        assertTrue("Recipient list doesn't contain sender as expected",
                recipientsWrapper.containsPhysicianID(physicianID));
        assertTrue("Recipient list doesn't contain everyone",
                recipientsWrapper.containsPhysicianID(17) &&
                        recipientsWrapper.containsPhysicianID(521));
    }

    /**
     * Sends a reply to a group message.
     * @param conversationID The conversation ID used.
     * @param appendText A text to append to the message body (to make the messages different, thus
     *                   easier to check.)
     * @throws Exception
     */
    private void replyToGroupMessage(String conversationID, String appendText) throws Exception {
        InAppNotificationGroupRequestContent message = new InAppNotificationGroupRequestContent();
        message.conversationID = conversationID;
        message.fromPhysicianID = physicianID;
        message.fromPhysicianName = name;
        message.practiceID = practiceID;
        message.subject = "Re: test";
        message.message = "A test reply - " + appendText;
        ReplyToInAppGroupNotificationRequest req =
                new ReplyToInAppGroupNotificationRequest(loginActivity, message);
        SendInAppNotificationRequestResponse result = req.loadDataFromNetwork();
        assertTrue("Couldn't send message, failed with status " + result.status,
                result.didSendMessageSuccessfully());
    }

    /**
     * Creates message threads from the results of the inbox and sent items call.
     * @return The resulting message threads object.
     * @throws Exception
     */
    private MessageThreads assembleMessageThreads() throws Exception{
        GetInAppNotificationInboxItemsRequest inboxReq =
                new GetInAppNotificationInboxItemsRequest(loginActivity);
        InboxItemsResponse inboxResponse = inboxReq.loadDataFromNetwork();
        GetInAppNotificationSentItemsRequest sentItemsReq =
                new GetInAppNotificationSentItemsRequest(loginActivity);
        SentItemsResponse sentItemsResponse = sentItemsReq.loadDataFromNetwork();
        MessageThreads threads = new MessageThreads(inboxResponse, sentItemsResponse, name);
        return threads;
    }
}
