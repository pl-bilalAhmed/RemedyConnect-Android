package com.remedywebsolutions.YourPractice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Physician;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SendInAppNotificationRequestResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SendCustomMessageActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    private EditText subjectEditText, messageEditText;
    private Spinner recipientSpinner;
    private ArrayAdapter<String> recipientAdapter;
    private String subjectForReply;
    private int toPhysicianIDForReply;
    private String conversationIDForReply;
    private boolean replyMode;
    private PhysiciansResponse physicians;

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
        reportPhase("Send custom message");
        setContentView(R.layout.activity_send_custom_message);
        Button sendButton = (Button) findViewById(R.id.sendMessageButton);
        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        recipientSpinner = (Spinner) findViewById(R.id.recipientSpinner);
        Skin.applyActivityBackground(this);
        Skin.applyButtonStyle(this, sendButton);

        LoggedInDataStorage storage = new LoggedInDataStorage(this);
        HashMap<String, String> userData = storage.RetrieveData();
        ObjectMapper mapper = new ObjectMapper();
        try {
            physicians = mapper.readValue(userData.get("physicians"), PhysiciansResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up adapter for recipient
        recipientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
                }
                TextView text = (TextView)v.findViewById(android.R.id.text1);
                text.setText(this.getItem(position));
                return v;
            }
        };
        recipientSpinner.setAdapter(recipientAdapter);
        if (extras != null) {
            subjectForReply = extras.getString("subject");
        }
        else {
            subjectForReply = null;
        }
        replyMode = (subjectForReply != null);
        if (replyMode) {
            recipientSpinner.setEnabled(false);
            toPhysicianIDForReply  = extras.getInt("toPhysicianID");
            conversationIDForReply = extras.getString("conversationID");
            subjectEditText.setText(subjectForReply);
            subjectEditText.setEnabled(false); // @TODO Should we leave this editable?
            recipientAdapter.add(extras.getString("toPhysicianName"));
        }
        else {
            for (Physician physician : physicians.physicians) {
                recipientAdapter.add(physician.physicianName);
            }
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
                message.practiceID = Integer.parseInt(userData.get("practiceID"));
                if (replyMode) {
                    message.toPhysicianID = toPhysicianIDForReply;
                    message.subject = subjectForReply;
                    message.conversationID = conversationIDForReply;
                }
                else {
                    // Get position in recipient spinner
                    int position = recipientSpinner.getSelectedItemPosition();
                    message.toPhysicianID = physicians.physicians.get(position).physicianID;
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

    private final class SendMessageListener implements RequestListener<SendInAppNotificationRequestResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            setProgressMessageWaitAndDismiss("Couldn't send message.");
            defaultSpiceFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(SendInAppNotificationRequestResponse result) {
            String message;
            if (result.didSendMessageSuccessfully()) {
                message = "Test notification sent. It should arrive in a sec...";
            }
            else {
                message = "Couldn't send notification.";
            }
            setProgressMessageWaitAndDismissWithRunnable(message, new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SendCustomMessageActivity.this, MyAccountActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }
    }
}
