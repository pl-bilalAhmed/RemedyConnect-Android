package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.DeleteMessageRequest;

import java.util.HashMap;

public class MessageDisplayActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    private boolean inboxMode;
    private InboxItem inboxItem;
    private SentItem sentItem;
    private TextView nameView, subjectView, receivedView, messageView;
    private Button deleteMessageButton, replyButton;

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
        setContentView(R.layout.activity_message_display);
        nameView = (TextView) findViewById(R.id.fromTextView);
        subjectView = (TextView) findViewById(R.id.subjectTextView);
        receivedView = (TextView) findViewById(R.id.receivedTextView);
        messageView = (TextView) findViewById(R.id.messageTextView);
        deleteMessageButton = (Button) findViewById(R.id.deleteMessageButton);
        replyButton = (Button) findViewById(R.id.replyButton);

        nameView.setText("");
        subjectView.setText("");
        receivedView.setText("");
        messageView.setText("");

        inboxMode = extras.getBoolean("inboxMode");
        if (inboxMode) {
            inboxItem = (InboxItem) extras.get("messageItem");
            nameView.setText(inboxItem.fromPhysicianName);
            subjectView.setText(inboxItem.subject);
            receivedView.setText(inboxItem.dateReceived);
            messageView.setText(inboxItem.message);
            replyButton.setVisibility(View.VISIBLE);
        }
        else {
            sentItem = (SentItem) extras.get("messageItem");
            nameView.setText(sentItem.toPhysicianName);
            subjectView.setText(sentItem.subject);
            receivedView.setText(sentItem.dateSent);
            messageView.setText(sentItem.message);
            replyButton.setVisibility(View.GONE);
        }

        deleteMessageButton.setOnClickListener(new DeleteButtonListener());
        replyButton.setOnClickListener(new ReplyButtonListener());
    }

    private class DeleteButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(MessageDisplayActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("This message will be deleted.")
                    .setMessage("This operation cannot be undone. Are you sure?")
                    .setPositiveButton("Yes, delete it", new DeleteConfirmedButtonListener())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private class DeleteConfirmedButtonListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            progress.setMessage("Deleting message...");
            progress.show();
            DeleteMessageRequest request;
            if (inboxMode) {
                request = new DeleteMessageRequest(inboxItem.notificationID,
                        inboxItem.toPhysicianID, false, MessageDisplayActivity.this);
            }
            else {
                request = new DeleteMessageRequest(sentItem.notificationID,
                        sentItem.fromPhysicianID, true, MessageDisplayActivity.this);
            }
            spiceManager.execute(request, new DeleteRequestListener());
        }
    }

    private class DeleteRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException e) {
            setProgressMessageWaitAndDismiss("Couldn't delete message, please try again later.");
            defaultSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(String s) {
            setProgressMessageWaitAndDismissWithRunnable("Message deleted.", new Runnable() {
                @Override
                public void run() {
                    Intent myAccountIntent = new Intent(MessageDisplayActivity.this, MyAccountActivity.class);
                    myAccountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myAccountIntent);
                }
            });
        }
    }

    private class ReplyButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent replyActivity = new Intent(MessageDisplayActivity.this, SendCustomMessageActivity.class);
            replyActivity.putExtra("subject", "Re: " + inboxItem.subject);
            // @TODO We only handle inbox items right now
            replyActivity.putExtra("toPhysicianID", inboxItem.fromPhysicianID);
            replyActivity.putExtra("conversationID", inboxItem.conversationID);
            replyActivity.putExtra("toPhysicianName", inboxItem.fromPhysicianName);
            startActivity(replyActivity);
        }
    }
}
