package com.remedywebsolutions.YourPractice;

import android.content.Intent;
import android.os.Bundle;

import com.pushio.manager.PushIOManager;

public class MainActivity extends DefaultActivity {
    private static String PUSH_KEY_ALERT = "alert";
    public static final String EXTRA_SHOULD_UPDATE = "update";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushIOManager mPushIOManager = PushIOManager.getInstance(this);
        mPushIOManager.ensureRegistration();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    
    public void checkDownloadedData() {
        if (Data.isDataAvailable(getApplicationContext())) {
            if (Data.AppModeSelected(getApplicationContext())) {
                if (Data.IsProviderMode(getApplicationContext())) {
                    Intent intent = new Intent(this, ProviderMenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    boolean shouldUpdate = false;
                    if (getIntent().hasExtra(EXTRA_SHOULD_UPDATE)) {
                        shouldUpdate = getIntent().getBooleanExtra(EXTRA_SHOULD_UPDATE, false);
                    } else {
                        shouldUpdate = true;
                    }
                    //showLog("Should update = " + shouldUpdate);
                    Data.setShouldRefreshData(getApplicationContext(), shouldUpdate);
                    MainViewController.FireRootActivity(this); //Open MainMenu Activity
                    finish();
                }

            } else {
                Intent intent = new Intent(this, SelectModeActivity.class);
                startActivity(intent);
            }


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
