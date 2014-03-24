package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.FetchInboxRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.FetchSentRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyAccountActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
    Button inboxButton, sentItemsButton, sendMessageButton;
    String defaultInboxText, defaultSentText;
    private ArrayList<InboxItem> inbox;
    private ArrayList<SentItem> sentItems;
    private boolean refreshFirstCallCompleted;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("My account");
        setContentView(R.layout.activity_my_account);
        Skin.applyActivityBackground(this);
        setTitle("My Account");
        final Button testNotificationsButton = (Button) findViewById(R.id.btnSendTestNotification);
        final TextView welcomeHeader = (TextView) findViewById(R.id.greetingsHeader);
        LoggedInDataStorage storage = new LoggedInDataStorage(this);
        HashMap<String, String> userData  = storage.RetrieveData();
        welcomeHeader.setText("Welcome, " + userData.get("name")  + "!");
        sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
        inboxButton = (Button) findViewById(R.id.btnInbox);
        sentItemsButton = (Button) findViewById(R.id.btnSent);

        Skin.applyButtonStyle(this, sendMessageButton);
        Skin.applyButtonStyle(this, inboxButton);
        Skin.applyButtonStyle(this, sentItemsButton);
        Skin.applyButtonStyle(this, testNotificationsButton);

        defaultInboxText = inboxButton.getText().toString();
        defaultSentText = sentItemsButton.getText().toString();

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);

        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        testNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendInAppNotificationRequest req = new SendInAppNotificationRequest(MyAccountActivity.this);
                spiceManager.execute(req, new TestMessageListener());
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, SendCustomMessageActivity.class);
                startActivity(intent);
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

        sentItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, MessageListActivity.class);
                intent.putExtra("sentContents", sentItems);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
        refreshMessages();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    private void refreshMessages() {
        refreshFirstCallCompleted = false;
        progress.setMessage("Refreshing messages...");
        progress.show();
        FetchInboxRequest inboxReq = new FetchInboxRequest(MyAccountActivity.this);
        spiceManager.execute(inboxReq, new InboxRequestListener());
        FetchSentRequest sentReq = new FetchSentRequest(MyAccountActivity.this);
        spiceManager.execute(sentReq, new SentItemsRequestListener());
    }

    private void updateNumberForButton(String defaultText, int number, Button button) {
        button.setText(defaultText + " (" + Integer.toString(number) + ")");
    }

    private void updateNumberOfInboxItems(int inboxItems) {
        updateNumberForButton(defaultInboxText, inboxItems, inboxButton);
    }

    private void updateNumberOfSentItems(int sentItems) {
        updateNumberForButton(defaultSentText, sentItems, sentItemsButton);
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
            refreshMessages();
        }
    }

    private void dismissProgressCheckOnSuccess() {
        if (refreshFirstCallCompleted) {
            progress.dismiss();
        }
        else {
            refreshFirstCallCompleted = true;
        }
    }

    private void reloginSpiceFailureHandler(SpiceException e) {
        spiceManager.cancelAllRequests();
        if (e.getCause() instanceof IOException) {
            // We should have an authentication failure here, so re-login the user...
            new AlertDialog.Builder(MyAccountActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Your session has expired")
                    .setMessage("You will need to log in again. Please OK to proceed.")
                    .setPositiveButton("OK", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent loginIntent = new Intent(MyAccountActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
                    })
                    .show();
        }
    }

    private final class InboxRequestListener implements  RequestListener<InboxItemsResponse> {
        @Override
        public void onRequestFailure(SpiceException e) {
            progress.dismiss();
            reloginSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(InboxItemsResponse inboxItemsResponse) {
            inbox = inboxItemsResponse.inboxItemsArray;
            updateNumberOfInboxItems(inbox.size());
            dismissProgressCheckOnSuccess();
        }
    }

    private final class SentItemsRequestListener implements  RequestListener<SentItemsResponse> {
        @Override
        public void onRequestFailure(SpiceException e) {
            progress.dismiss();
            reloginSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(SentItemsResponse sentItemsResponse) {
            sentItems = sentItemsResponse.sentItemsArray;
            updateNumberOfSentItems(sentItems.size());
            dismissProgressCheckOnSuccess();
        }
    }
}
