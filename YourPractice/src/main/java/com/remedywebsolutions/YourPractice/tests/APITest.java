package com.remedywebsolutions.YourPractice.tests;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.remedywebsolutions.YourPractice.LoginActivity;
import com.remedywebsolutions.YourPractice.MainViewController;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationGroupRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.RecipientsResponseWrapper;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SendInAppNotificationRequestResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.DeleteInAppNotificationByConversationRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationRecipientsRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPhysiciansRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.InsertPhysicianMobileDeviceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.NewInAppGroupNotification;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.ReplyToInAppGroupNotificationRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

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
        SendInAppNotificationRequestResponse result = sendTestMessageToSelf();
        String conversationID = result.conversationID;
        sendTestMessageToSelfReply(conversationID);
        sendTestMessageToSelfReply(conversationID);
        deleteTestMessages(conversationID);
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
        SendInAppNotificationRequestResponse result = sendGroupMessage();
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
        assertTrue("Couldn't send message, failed with status " + result.status,
                result.didSendMessageSuccessfully());
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
     * @return The response from the API.
     * @throws Exception
     */
    private SendInAppNotificationRequestResponse sendGroupMessage() throws Exception {
        NewInAppGroupNotification req = new NewInAppGroupNotification(loginActivity);
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
}
