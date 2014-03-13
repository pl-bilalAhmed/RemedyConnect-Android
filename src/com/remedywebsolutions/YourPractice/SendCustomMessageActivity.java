package com.remedywebsolutions.YourPractice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SendCustomMessageActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    EditText subjectEditText, messageEditText;
    Spinner recipientSpinner;
    private ArrayAdapter<String> recipientAdapter;
    private String subjectForReply;
    private int toPhysicianIDForReply;
    private String conversationIDForReply;
    private boolean replyMode;

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
        setContentView(R.layout.activity_send_custom_message);
        Button sendButton = (Button) findViewById(R.id.sendMessageButton);
        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        recipientSpinner = (Spinner) findViewById(R.id.recipientSpinner);

        // Set up adapter for recipient
        recipientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(android.R.layout.simple_spinner_item, null);
                }
                TextView text = (TextView)v.findViewById(android.R.id.text1);
                text.setText(this.getItem(position));
                return v;
            }
        };
        recipientSpinner.setAdapter(recipientAdapter);

        subjectForReply = extras.getString("subject");
        replyMode = (subjectForReply != null);
        if (replyMode) {
            recipientSpinner.setEnabled(false);
            toPhysicianIDForReply  = extras.getInt("toPhysicianID");
            conversationIDForReply = extras.getString("conversationID");
            subjectEditText.setText(subjectForReply);
            subjectEditText.setEnabled(false); // @TODO Should we leave this editable?
            recipientAdapter.add(extras.getString("toPhysicianName"));
        }

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedInDataStorage storage = new LoggedInDataStorage(SendCustomMessageActivity.this);
                HashMap<String, String> userData = storage.RetrieveData();
                InAppNotificationRequestContent message = new InAppNotificationRequestContent();
                message.fromPhysicianID = Integer.parseInt(userData.get("physicianID"));
                message.fromPhysicianName = userData.get("name");
                if (replyMode) {
                    message.toPhysicianID = toPhysicianIDForReply;
                    message.subject = subjectForReply;
                    message.conversationID = conversationIDForReply;
                }
                else {
                    message.toPhysicianID = 405; // @TODO Implement fetching from contact list
                    message.subject = subjectEditText.getText().toString();
                }
                message.message = messageEditText.getText().toString();
                SendInAppNotificationRequest req = new SendInAppNotificationRequest(SendCustomMessageActivity.this, message);
                spiceManager.execute(req, new SendMessageListener());
                progress.setMessage("Sending message...");
                progress.show();
            }
        });
    }

    private final class SendMessageListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            setProgressMessageWaitAndDismiss("Couldn't send message.");
            defaultSpiceFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(String result) {
            setProgressMessageWaitAndDismiss("Message sent.", true);
        }
    }
}
