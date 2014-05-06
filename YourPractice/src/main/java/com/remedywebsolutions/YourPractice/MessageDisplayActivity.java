package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Recipient;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.RecipientsResponseWrapper;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationInBoxItemRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationSentItemRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MessageDisplayActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    private boolean inboxMode;
    private ArrayList<InboxItem> inboxItems;
    private ArrayList<SentItem> sentItems;
    private ArrayList<Recipient> recipients;
    private int position;
    private InboxItem inboxItem;
    private SentItem sentItem;
    private TextView messageView;
    private ProgressBar progressBar;
    private Button deleteMessageButton, replyButton;
    private boolean loaded;

    @Override
    public void onBackPressed() {
        if (loaded) {
            setResult(RESULT_OK);
        }
        else {
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }

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

    protected String formatDate(String dateFromAPI) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date date = df.parse(dateFromAPI);
        return android.text.format.DateFormat.getLongDateFormat(this).format(date) + " " +
                android.text.format.DateFormat.getTimeFormat(this).format(date);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Message display");
        setContentView(R.layout.activity_message_display);

        Skin.applyActivityBackground(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView nameView = (TextView) findViewById(R.id.fromTextView);
        TextView subjectView = (TextView) findViewById(R.id.subjectTextView);
        TextView receivedView = (TextView) findViewById(R.id.receivedTextView);
        messageView = (TextView) findViewById(R.id.messageTextView);
        deleteMessageButton = (Button) findViewById(R.id.deleteMessageButton);
        replyButton = (Button) findViewById(R.id.replyButton);
        Skin.applyButtonStyle(this, replyButton);
        Skin.applyButtonStyle(this, deleteMessageButton);

        nameView.setText("");
        subjectView.setText("");
        receivedView.setText("");
        messageView.setText("");

        inboxMode = extras.getBoolean("inboxMode");
        replyButton.setVisibility(View.GONE);
        deleteMessageButton.setVisibility(View.GONE);
        position = extras.getInt("position");
        if (inboxMode) {
            if (extras.get("inboxItems") instanceof ArrayList) {
                //noinspection unchecked
                inboxItems = (ArrayList<InboxItem>) extras.get("inboxItems");
                inboxItem = inboxItems.get(position);
                nameView.setText(inboxItem.fromPhysicianName);
                subjectView.setText(inboxItem.subject);
                try {
                    receivedView.setText(formatDate(inboxItem.dateReceived));
                } catch (ParseException e) {
                    receivedView.setText(inboxItem.dateReceived);
                }
                // We only activate the reply button if the message is loaded...
            }
        }
        else {
            if (extras.get("sentItems") instanceof ArrayList) {
                //noinspection unchecked
                sentItems = (ArrayList<SentItem>) extras.get("sentItems");
                sentItem = sentItems.get(position);
                nameView.setText(sentItem.getRecipients());
                subjectView.setText(sentItem.subject);
                try {
                    receivedView.setText(formatDate(sentItem.dateSent));
                } catch (ParseException e) {
                    receivedView.setText(sentItem.dateSent);
                }
            }
        }

        startFetchingMessage();
    }

    private void startFetchingMessage() {
        loaded = false;
        if (inboxMode) {
            GetInAppNotificationInBoxItemRequest req = new GetInAppNotificationInBoxItemRequest(this, inboxItem.notificationID);
            spiceManager.execute(req, new InboxItemListener());
            //GetInAppNotificationRecipientsRequest recipientsRequest = new GetInAppNotificationRecipientsRequest(this, inboxItem.conversationID);
            //spiceManager.execute(recipientsRequest, new RecipientsListener());
        }
        else {
            GetInAppNotificationSentItemRequest req = new GetInAppNotificationSentItemRequest(this, sentItem.notificationID);
            spiceManager.execute(req, new SentItemListener());
            //GetInAppNotificationRecipientsRequest recipientsRequest = new GetInAppNotificationRecipientsRequest(this, inboxItem.conversationID);
            //spiceManager.execute(recipientsRequest, new RecipientsListener());
        }

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
            /*
            DeleteInAppNotificationItemRequest request;
            if (inboxMode) {
                request = new DeleteInAppNotificationItemRequest(inboxItem.notificationID,
                        inboxItem.practiceID, inboxItem.toPhysicianID, false, MessageDisplayActivity.this);
            }
            else {
                request = new DeleteInAppNotificationItemRequest(sentItem.notificationID,
                        sentItem.practiceID, sentItem.toPhysicianID, true, MessageDisplayActivity.this);
            }
            spiceManager.execute(request, new DeleteRequestListener());
            */

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
            String message;
            if (s.equals("true")) {
                message = "Message deleted.";
            }
            else {
                message = "Couldn't delete message.";
            }
            setProgressMessageWaitAndDismissWithRunnable(message, new Runnable() {
                @Override
                public void run() {
                    Intent messageListIntent = new Intent(MessageDisplayActivity.this, MessageListActivity.class);
                    messageListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    messageListIntent.putExtra("shouldReverseList", false);
                    if (inboxMode) {
                        inboxItems.remove(position);
                        messageListIntent.putExtra("inboxContents", inboxItems);
                    }
                    else {
                        sentItems.remove(position);
                        messageListIntent.putExtra("sentContents", sentItems);
                    }
                    startActivity(messageListIntent);
                }
            });

        }
    }

    private ArrayList<Integer> filterSelfFromRecipients(ArrayList<Recipient> recipients) {
        LoggedInDataStorage storage = new LoggedInDataStorage(MessageDisplayActivity.this);
        HashMap<String, String> userData = storage.RetrieveData();
        ArrayList<Integer> result = new ArrayList<Integer>();
        int selfPhysicianID = Integer.parseInt(userData.get("physicianID"));
        for (Recipient recipient : recipients) {
            if (recipient.physicianID != selfPhysicianID) {
                result.add(recipient.physicianID);
            }
        }
        return result;
    }

    private class ReplyButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent replyActivity = new Intent(MessageDisplayActivity.this, SendCustomMessageActivity.class);
            replyActivity.putExtra("subject", "Re: " + inboxItem.subject);
            // @TODO We only handle inbox items right now
            replyActivity.putExtra("toPhysicianID", inboxItem.fromPhysicianID);
            replyActivity.putExtra("toPhysicianIDs", filterSelfFromRecipients(recipients));
            replyActivity.putExtra("conversationID", inboxItem.conversationID);
            replyActivity.putExtra("toPhysicianName", inboxItem.fromPhysicianName);
            startActivity(replyActivity);
        }
    }

    private class InboxItemListener implements RequestListener<InboxItem> {
        @Override
        public void onRequestFailure(SpiceException e) {
            setProgressMessageWaitAndDismiss("Couldn't fetch message, please try again later.");
            defaultSpiceFailureHandler(e);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(InboxItem inboxItem) {
            deleteMessageButton.setOnClickListener(new DeleteButtonListener());
            replyButton.setOnClickListener(new ReplyButtonListener());
            inboxItem.dateReceived = "READ";
            MessageDisplayActivity.this.inboxItem.message = inboxItem.message;
            replyButton.setVisibility(View.VISIBLE);
            deleteMessageButton.setVisibility(View.VISIBLE);
            MessageDisplayActivity.this.messageView.setText(inboxItem.message);
            progressBar.setVisibility(View.GONE);
            loaded = true;
        }
    }

    private class SentItemListener implements RequestListener<SentItem> {
        @Override
        public void onRequestFailure(SpiceException e) {
            setProgressMessageWaitAndDismiss("Couldn't fetch message, please try again later.");
            defaultSpiceFailureHandler(e);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(SentItem sentItem) {
            deleteMessageButton.setOnClickListener(new DeleteButtonListener());
            replyButton.setOnClickListener(new ReplyButtonListener());

            MessageDisplayActivity.this.sentItem.message = sentItem.message;
            MessageDisplayActivity.this.messageView.setText(sentItem.message);
            deleteMessageButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            loaded = true;
        }
    }

    private class RecipientsListener implements RequestListener<RecipientsResponseWrapper> {
        @Override
        public void onRequestFailure(SpiceException e) {
            setProgressMessageWaitAndDismiss("Couldn't fetch message, please try again later.");
            defaultSpiceFailureHandler(e);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onRequestSuccess(RecipientsResponseWrapper recipientsResponseWrapper) {
            recipients = recipientsResponseWrapper.recipients;
        }
    }
}
