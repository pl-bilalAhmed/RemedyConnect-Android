package com.remedywebsolutions.YourPractice.passcode;

import android.os.Bundle;
import android.widget.TextView;

import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;

import org.wordpress.passcodelock.*;
import org.wordpress.passcodelock.AppLockManager;

public class PasscodeManagePasswordActivity extends com.remedywebsolutions.YourPractice.passcode.AbstractPasscodeKeyboardActivity {
    private int type = -1;
    private String unverifiedPasscode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt("type", -1);
        }
        
    }
    
    @Override
    protected void onPinLockInserted() {
        String passLock = pinCodeField1.getText().toString() + pinCodeField2.getText().toString() +
                pinCodeField3.getText().toString() + pinCodeField4.getText();
        
        pinCodeField1.setText("");
        pinCodeField2.setText("");
        pinCodeField3.setText("");
        pinCodeField4.setText("");
        pinCodeField1.requestFocus();
        
        switch (type) {
            
            case PasscodePreferencesActivity.DISABLE_PASSLOCK:
                if( com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().verifyPassword(passLock) ) {
                    setResult(RESULT_OK);
                    com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().setPassword(null);
                    finish();
                } else {
                    showPasswordError();
                }
                break;
                
            case PasscodePreferencesActivity.ENABLE_PASSLOCK:
                if( unverifiedPasscode == null ) {
                    ((TextView) findViewById(R.id.top_message)).setText("Re-Enter you PIN");
                    unverifiedPasscode = passLock;
                } else {
                    if( passLock.equals(unverifiedPasscode)) {
                        setResult(RESULT_OK);

                        com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().setPassword(passLock);
                        LoggedInDataStorage dataStorage = new LoggedInDataStorage(PasscodeManagePasswordActivity.this);

                        com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().setOneTimeTimeout(dataStorage.GetPinTimeout());
                        finish();
                    } else {
                        unverifiedPasscode = null;
                        topMessage.setText("Enter you PIN");
                        showPasswordError();
                    }
                }
                break;
                
            case PasscodePreferencesActivity.CHANGE_PASSWORD:
                //verify old password
                if(   com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().verifyPassword(passLock) ) {
                    topMessage.setText("Enter you PIN");
                    type = PasscodePreferencesActivity.ENABLE_PASSLOCK;
                } else {
                    showPasswordError();
                } 
                break;
                
            default:
                break;
        }
    }
}