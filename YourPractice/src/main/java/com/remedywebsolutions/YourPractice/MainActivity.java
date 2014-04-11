package com.remedywebsolutions.YourPractice;

import android.content.Intent;
import android.os.Bundle;
import com.pushio.manager.PushIOManager;

public class MainActivity extends DefaultActivity {
    private static String PUSH_KEY_ALERT = "alert";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PushIOManager mPushIOManager = PushIOManager.getInstance(this);
        mPushIOManager.ensureRegistration();
    }

    public void checkDownloadedData() {
        if (Data.isDataAvailable(getApplicationContext())) {
            MainViewController.FireRootActivity(this);
            finish();
        } else {
            Intent practiceSearchActivity = new Intent(this, PracticeSearchActivity.class);
            startActivity(practiceSearchActivity);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkDownloadedData();
    }
}
