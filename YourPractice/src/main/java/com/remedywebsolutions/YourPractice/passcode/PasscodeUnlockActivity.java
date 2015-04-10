package com.remedywebsolutions.YourPractice.passcode;

import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.remedywebsolutions.YourPractice.LoginActivity;
import com.remedywebsolutions.YourPractice.ProviderMenuActivity;
import com.remedywebsolutions.YourPractice.R;


public class PasscodeUnlockActivity extends com.remedywebsolutions.YourPractice.passcode.AbstractPasscodeKeyboardActivity {

    @Override
    public void onStart() {
        super.onStart();
        AppLockManager.getInstance().getCurrentAppLock().NumberOfFailures = 0;
    }

    @Override
    public void onBackPressed() {
        com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().forcePasswordLock();
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);
        finish();
    }


    @Override
    protected void onPinLockInserted() {
        String passLock = pinCodeField1.getText().toString() + pinCodeField2.getText().toString() +
                pinCodeField3.getText().toString() + pinCodeField4.getText();
        if( com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().verifyPassword(passLock) ) {
            setResult(RESULT_OK);
            com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().NumberOfFailures = 0;
            finish();
        } else {
            Thread shake = new Thread() {
                public void run() {
                    Animation shake = AnimationUtils.loadAnimation(PasscodeUnlockActivity.this, R.anim.shake);
                    findViewById(R.id.AppUnlockLinearLayout1).startAnimation(shake);
                    if( com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().NumberOfFailures > 2) {
                        showPasswordError();
                    }
                    pinCodeField1.setText("");
                    pinCodeField2.setText("");
                    pinCodeField3.setText("");
                    pinCodeField4.setText("");
                    pinCodeField1.requestFocus();
                }
            };
            runOnUiThread(shake);
            com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().NumberOfFailures++;
            if( com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().NumberOfFailures > 2)
            {
                showPasswordMaxError();
                Intent loginIntent = new Intent(PasscodeUnlockActivity.this, LoginActivity.class);
                com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().disable();
                startActivity(loginIntent);
            }

        }
    }
}