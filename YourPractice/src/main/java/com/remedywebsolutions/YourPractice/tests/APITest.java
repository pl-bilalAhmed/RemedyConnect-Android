package com.remedywebsolutions.YourPractice.tests;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.remedywebsolutions.YourPractice.LoginActivity;
import com.remedywebsolutions.YourPractice.MainViewController;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPhysiciansRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.RegisterDeviceRequest;

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
        assertTrue("App is online", loginActivity.isOnline());
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

    public void testMessageToSelf() throws Exception {
        LoginResponse loginResponse = login();
        registerDevice(loginResponse);

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
        assertEquals("Practice ID check", result.getPracticeID(), 36);
        assertEquals("Physician ID check", result.getPhysicianID(), 405);
        assertTrue("Got token", result.getToken().length() > 0);
        assertTrue("Well-formatted token", isHex(result.getToken()));
        return result;
    }

    private String registerDevice(LoginResponse loginResponse) throws Exception {
        practiceID = loginResponse.getPracticeID();
        physicianID = loginResponse.getPhysicianID();
        String token = loginResponse.getToken();
        dataStorage.StoreDataOnLogin(physicianID, practiceID, token);
        RegisterDeviceRequest registerReq = new RegisterDeviceRequest(physicianID, "zoltan", loginActivity);
        String response = registerReq.loadDataFromNetwork();
        assertNotNull("Got response", response.length() > 0);
        assertTrue("Well-formatted device ID", isHex(response));
        dataStorage.StoreDeviceId(response);
        return response;
    }

    private void pullPhysicians() throws Exception {
        GetPhysiciansRequest pullContactsReq = new GetPhysiciansRequest(loginActivity);
        PhysiciansResponse physicians = pullContactsReq.loadDataFromNetwork();
        assertNotNull("Got contacts", physicians);
        assertTrue("Got at least 4 physicians", physicians.physicians.size() >= 4);
        String name = loginActivity.getAndSetNameFromResponse(physicians, physicianID);
        assertEquals("Name matches", name, "Zoltan Adamek, MD");
    }


}
