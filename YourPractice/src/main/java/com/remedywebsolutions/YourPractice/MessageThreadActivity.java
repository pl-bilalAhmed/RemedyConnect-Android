package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.DateOperations;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThread;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThreadMessage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.DeleteInAppNotificationByConversationRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageThreadActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    protected ArrayAdapter<MessageThreadMessage> threadListAdapter;
    protected ListView threadListView;
    private HashMap<String, String> loginData;
    private MessageThread thread;
    private int selfPhysicianID, practiceID;

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
        setContentView(R.layout.activity_message_thread);
        Skin.applyActivityBackground(this);

        thread = (MessageThread)extras.get("thread");
        assert thread != null;

        TextView partners = (TextView) findViewById(R.id.threadPartners);
        TextView subject = (TextView) findViewById(R.id.threadSubject);
        Button deleteMessage = (Button) findViewById(R.id.messageThreadDeleteMessageButton);
        Button replyMessage = (Button) findViewById(R.id.messageThreadReplyButton);

        partners.setText(thread.getRecipientNameList());
        subject.setText(thread.getSubject());

        loginData = new LoggedInDataStorage(this).RetrieveData();
        selfPhysicianID = Integer.parseInt(loginData.get("physicianID"));
        practiceID = Integer.parseInt(loginData.get("practiceID"));

        partners.setTypeface(Skin.menuFont(MessageThreadActivity.this));
        subject.setTypeface(Skin.menuFont(MessageThreadActivity.this));
        Skin.applyButtonStyle(this, deleteMessage);
        Skin.applyButtonStyle(this, replyMessage);

        deleteMessage.setOnClickListener(new DeleteButtonListener());
        replyMessage.setOnClickListener(new ReplyButtonListener());

        threadListView = (ListView) findViewById(R.id.threadMessages);
        threadListAdapter = new ArrayAdapter<MessageThreadMessage>(this, R.layout.message_thread_message_row,
                new ArrayList<MessageThreadMessage>()) {

            public View getView(int pos, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.message_thread_message_row, parent, false);
                }
                assert v != null;

                TextView messageSentBy = (TextView) v.findViewById(R.id.threadMessageSentBy);
                TextView messageSentAt = (TextView) v.findViewById(R.id.threadMessageSentAt);
                TextView messageBody = (TextView) v.findViewById(R.id.threadMessageBody);
                ImageView readStatusImageView = (ImageView) v.findViewById(R.id.threadMessageReadIcon);

                MessageThreadMessage currentMessage = thread.getMessages().get(pos);

                messageSentBy.setText(currentMessage.getFromPhysicianName());
                messageBody.setText(currentMessage.getMessage());
                messageSentAt.setText("Received " +
                        DateOperations.getRelativeTimeForTimeString(currentMessage.getSentTime()));

                if (currentMessage.getRead()) {
                    readStatusImageView.setImageResource(R.drawable.mailopened_black);
                }
                else {
                    readStatusImageView.setImageResource(R.drawable.mail_black);
                }

                messageSentBy.setTypeface(Skin.menuFont(MessageThreadActivity.this), Typeface.BOLD);
                messageBody.setTypeface(Skin.menuFont(MessageThreadActivity.this));
                messageSentAt.setTypeface(Skin.menuFont(MessageThreadActivity.this), Typeface.ITALIC);
                return v;
            }
        };

        threadListView.setAdapter(threadListAdapter);
        if (thread.getMessages() != null) {
            for (MessageThreadMessage message : thread.getMessages()) {
                threadListAdapter.add(message);
            }
        }
    }

    private class DeleteButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(MessageThreadActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("This thread will be deleted.")
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

            DeleteInAppNotificationByConversationRequest request;
            request = new DeleteInAppNotificationByConversationRequest(thread.getConversationID(),
                        practiceID, selfPhysicianID, false, MessageThreadActivity.this);
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
            String message;
            if (s.equals("true")) {
                message = "Message deleted.";
                DeleteInAppNotificationByConversationRequest request;
                request = new DeleteInAppNotificationByConversationRequest(thread.getConversationID(),
                        practiceID, selfPhysicianID, false, MessageThreadActivity.this);
                spiceManager.execute(request, new DeleteRequest2Listener());
            }
            else {
                message = "Couldn't delete message.";
                setProgressMessageWaitAndDismissWithRunnable(message, new Runnable() {
                    @Override
                    public void run() {
                        Intent myAccountIntent = new Intent(MessageThreadActivity.this, MyAccountActivity.class);
                        myAccountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(myAccountIntent);
                    }
                });
            }
        }
    }

    private class DeleteRequest2Listener implements RequestListener<String> {
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
                    Intent myAccountIntent = new Intent(MessageThreadActivity.this, MyAccountActivity.class);
                    myAccountIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myAccountIntent);
                }
            });

        }
    }

    private class ReplyButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            final EditText input = new EditText(MessageThreadActivity.this);
            new AlertDialog.Builder(MessageThreadActivity.this)
                    .setTitle("Send reply")
                    .setView(input)
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(MessageThreadActivity.this, input.getText(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Do nothing.
                        }
                    }).show();
        }
    }
}
