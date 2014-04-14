package com.remedywebsolutions.YourPractice.tests;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.remedywebsolutions.YourPractice.LoginActivity;
import com.remedywebsolutions.YourPractice.MainViewController;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SendInAppNotificationRequestResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.DeleteInAppNotificationItemRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationInBoxItemRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPhysiciansRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.InitiateInAppGroupNotification;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.InsertPhysicianMobileDeviceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

public class APITest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private LoginActivity loginActivity;
    private LoggedInDataStorage dataStorage;
    private int physicianID, practiceID;

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
        loginActivity.disablePasscode();
    }

    @Override
    public void tearDown() throws Exception {
        loginActivity.enablePasscode();
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

    public void testMessageToSelfAndDelete() throws Exception {
        LoginResponse loginResponse = login();
        registerDevice(loginResponse);
        pullPhysicians();
        int notificationID = sendTestMessageToSelf();
        deleteTestMessages(notificationID);
    }

    public void testSendGroupMessage() throws Exception {
        LoginResponse loginResponse = login();
        registerDevice(loginResponse);
        pullPhysicians();
        int notificationID = sendGroupMessage();
        getGroupMessage(notificationID);
    }

    /**
     * Checks whether a string is a hexadecimal representation of a number.
     * @param str The string to check.
     * @return True if the string is a hexadecimal number, false otherwise.
     */
    private boolean isHex(String str) {
        return str.matches("\\p{XDigit}+");
    }

    private LoginResponse login() throws Exception {
        LoginRequest loginReq = new LoginRequest("zoltan", "zoltan1", loginActivity);
        LoginResponse result = loginReq.loadDataFromNetwork();
        assertEquals("Practice ID does not match", 36, result.getPracticeID());
        assertEquals("Physician ID does not match", 405, result.getPhysicianID());
        assertTrue("Token has zero length", result.getToken().length() > 0);
        assertTrue("Token isn't well-formatted", isHex(result.getToken()));
        return result;
    }

    private String registerDevice(LoginResponse loginResponse) throws Exception {
        practiceID = loginResponse.getPracticeID();
        physicianID = loginResponse.getPhysicianID();
        String token = loginResponse.getToken();
        dataStorage.StoreDataOnLogin(physicianID, practiceID, token);
        InsertPhysicianMobileDeviceRequest registerReq = new InsertPhysicianMobileDeviceRequest(physicianID, practiceID, "zoltan", loginActivity);
        String response = registerReq.loadDataFromNetwork();
        assertNotNull("Doesn't have response", response.length() > 0);
        assertTrue("The device ID is not well-formatted", isHex(response));
        dataStorage.StoreDeviceId(response);
        return response;
    }

    private void pullPhysicians() throws Exception {
        GetPhysiciansRequest pullContactsReq = new GetPhysiciansRequest(loginActivity);
        PhysiciansResponse physicians = pullContactsReq.loadDataFromNetwork();
        assertNotNull("No contacts", physicians.physicians.size() == 0);
        assertTrue("Less than expected number of physicians: got " + physicians.physicians.size() + ", which is less than four",
                physicians.physicians.size() >= 4);
        String name = loginActivity.getAndSetNameFromResponse(physicians, physicianID);
        assertNotNull("Name is not set", name);
        assertEquals("Name does not match", name, "Zoltan Adamek, MD");
    }

    /**
     * Sends a test message to self.
     *
     * @return The notification ID.
     * @throws Exception
     */
    private int sendTestMessageToSelf() throws Exception {
        SendInAppNotificationRequest req = new SendInAppNotificationRequest(loginActivity);
        SendInAppNotificationRequestResponse result = req.loadDataFromNetwork();
        assertTrue("Couldn't send message, failed with status " + result.status,
                result.didSendMessageSuccessfully());
        return result.notificationID;
    }

    private void deleteTestMessages(int notificationID) throws Exception {
        DeleteInAppNotificationItemRequest req = new DeleteInAppNotificationItemRequest(
                notificationID, practiceID, physicianID, false, loginActivity
        );
        String result = req.loadDataFromNetwork();
        assertTrue("Failed to delete inbox message", result.equals("true"));
        req = new DeleteInAppNotificationItemRequest(
                notificationID, practiceID, physicianID, true, loginActivity
        );
        result = req.loadDataFromNetwork();
        assertTrue("Failed to delete sent message", result.equals("true"));
    }

    private int sendGroupMessage() throws Exception {
        InitiateInAppGroupNotification req = new InitiateInAppGroupNotification(loginActivity);
        SendInAppNotificationRequestResponse result = req.loadDataFromNetwork();
        assertTrue("Couldn't send message, failed with status " + result.status,
                result.didSendMessageSuccessfully());
        return result.notificationID;
    }

    private void getGroupMessage(int notificationID) throws Exception {
        GetInAppNotificationInBoxItemRequest req = new GetInAppNotificationInBoxItemRequest(loginActivity, notificationID);
        InboxItem result = req.loadDataFromNetwork();
        assertTrue("Recipient list doesn't contain sender as expected", result.toPhysicianID != physicianID);
    }
}
