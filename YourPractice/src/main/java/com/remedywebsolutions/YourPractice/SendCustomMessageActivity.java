package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.List;

public class SendCustomMessageActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    private EditText subjectEditText, messageEditText;
    private Spinner recipientSpinner;
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
        Button chooseGroupRecipientsButton = (Button) findViewById(R.id.selectGroupRecipientsButton);
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

        Boolean groupMode;
        if (extras != null) {
            subjectForReply = extras.getString("subject");
            groupMode = extras.getBoolean("groupMode", false);
        }
        else {
            subjectForReply = null;
            groupMode = false;
        }
        if (groupMode) {
            recipientSpinner.setVisibility(View.GONE);
            chooseGroupRecipientsButton.setVisibility(View.VISIBLE);
            Skin.applyButtonStyle(this, chooseGroupRecipientsButton);
            chooseGroupRecipientsButton.setOnClickListener(new ShowRecipientChooserButtonListener());
        }
        else {
            recipientSpinner.setVisibility(View.VISIBLE);
            chooseGroupRecipientsButton.setVisibility(View.GONE);
            setupRecipientAdapter();
            sendButton.setOnClickListener(new SendButtonListener());
        }

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void setupRecipientAdapter() {
        // Set up adapter for recipient
        ArrayAdapter<String> recipientAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                }
                assert v != null;
                TextView text = (TextView) v.findViewById(android.R.id.text1);
                text.setText(this.getItem(position));
                return v;
            }
        };
        recipientSpinner.setAdapter(recipientAdapter);
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
    }

    private class SendButtonListener implements Button.OnClickListener {
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
                assert subjectEditText.getText() != null;
                message.subject = subjectEditText.getText().toString();
            }
            assert messageEditText.getText() != null;
            message.message = messageEditText.getText().toString();
            SendInAppNotificationRequest req = new SendInAppNotificationRequest(SendCustomMessageActivity.this, message);
            spiceManager.execute(req, new SendMessageListener());
            progress.setMessage("Sending message...");
            progress.show();
        }
    }

    private class ShowRecipientChooserButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SendCustomMessageActivity.this);
            List<CharSequence> physicianNames = new ArrayList<CharSequence>();
            for (Physician physician : physicians.physicians) {
                physicianNames.add(physician.physicianName);
            }
            final ArrayList<Integer> selectedItems = new ArrayList<Integer>();
            builder.setTitle("Choose recipients")
                    .setMultiChoiceItems(physicianNames.toArray(new CharSequence[physicianNames.size()]), null,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        selectedItems.add(which);
                                    } else if (selectedItems.contains(which)) {
                                        // Else, if the item is already in the array, remove it
                                        selectedItems.remove(Integer.valueOf(which));
                                    }
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
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
                message = "Message sent.";
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
