package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

public class MyAccountActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        final Button testNotificationsButton = (Button) findViewById(R.id.btnSendTestNotification);
        testNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendInAppNotificationRequest req = new SendInAppNotificationRequest(MyAccountActivity.this);
                spiceManager.execute(req, new TestMessageListener());
            }
        });

    }

    private final class TestMessageListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            defaultSpiceFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(String result) {
            progress.dismiss();
            Log.d("YourPractice", "Sending test notification, result: " + result);
            Toast.makeText(MyAccountActivity.this, "Sent test notification...", Toast.LENGTH_SHORT).show();
        }
    }
}
