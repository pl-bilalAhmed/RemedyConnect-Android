package com.newpush.mypractice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.pushio.manager.PushIOManager;

import java.util.ArrayList;

public class MainActivity extends DefaultActivity {
    private static String PUSH_KEY_ALERT = "alert";
    private PushIOManager mPushIOManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPushIOManager = PushIOManager.getInstance(this);
        mPushIOManager.ensureRegistration();

        //Checking to see if this activity was created by a user engaging with a notification
        if (getIntent().hasExtra(PUSH_KEY_ALERT)) {
            //Since they did engage with a notification we can grab anything from the payload, for this simple case, let's display the text from the last alert.
            Toast.makeText(this, "Last Push: " + getIntent().getStringExtra(PUSH_KEY_ALERT), Toast.LENGTH_LONG);
        }
    }

    public void checkDownloadedData() {
        if (DataChecker.isDataAvailable(getApplicationContext())) {
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
