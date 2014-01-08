package com.newpush.YourPractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.pushio.manager.PushIOManager;

public class MainActivity extends DefaultActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PushIOManager mPushIOManager = PushIOManager.getInstance(this);
        mPushIOManager.ensureRegistration();

        //Checking to see if this activity was created by a user engaging with a notification
        String PUSH_KEY_ALERT = "alert";
        if (getIntent().hasExtra(PUSH_KEY_ALERT)) {
            //Since they did engage with a notification we can grab anything from the payload, for this simple case, let's display the text from the last alert.
            Toast.makeText(this, "Last Push: " + getIntent().getStringExtra(PUSH_KEY_ALERT), Toast.LENGTH_LONG).show();
        }
    }

    public void checkDownloadedData() {
        if (Data.isDataAvailable(getApplicationContext())) {
            MainViewController.FireActivity(this, "index.xml");
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
