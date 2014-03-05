package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.FetchInboxRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

import java.util.ArrayList;

public class MyAccountActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
    Button inboxButton, sentItemsButton;
    String defaultInboxText;
    private ArrayList<InboxItem> inbox;

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
        inboxButton = (Button) findViewById(R.id.btnInbox);
        sentItemsButton = (Button) findViewById(R.id.btnSent);
        defaultInboxText = inboxButton.getText().toString();

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);

        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        startFetchingMessages();
        testNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendInAppNotificationRequest req = new SendInAppNotificationRequest(MyAccountActivity.this);
                spiceManager.execute(req, new TestMessageListener());
            }
        });

        inboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, MessageListActivity.class);
                intent.putExtra("inboxContents", inbox);
                startActivity(intent);
            }
        });
    }

    private void startFetchingMessages() {
        progress.setMessage("Updating inbox status...");
        progress.show();
        FetchInboxRequest req = new FetchInboxRequest(MyAccountActivity.this);
        spiceManager.execute(req, new InboxRequestListener());
    }

    private void updateNumberOfInboxItems(int inboxItems) {
        inboxButton.setText(defaultInboxText + " (" + Integer.toString(inboxItems) + ")");
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
            Toast.makeText(MyAccountActivity.this, "Test notification sent, it should arrive by now", Toast.LENGTH_SHORT).show();
            startFetchingMessages();
        }
    }

    private final class InboxRequestListener implements  RequestListener<InboxItemsResponse> {
        @Override
        public void onRequestFailure(SpiceException e) {
            progress.dismiss();
            defaultSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(InboxItemsResponse inboxItemsResponse) {
            progress.dismiss();
            inbox = inboxItemsResponse.inboxItemsArray;
            updateNumberOfInboxItems(inbox.size());
        }
    }
}
