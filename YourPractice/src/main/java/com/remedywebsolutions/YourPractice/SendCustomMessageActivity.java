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
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationGroupRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Physician;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SendInAppNotificationRequestResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.NewInAppGroupNotification;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.ReplyToInAppGroupNotificationRequest;
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
    private ArrayList<Integer> toPhysicianIDs;
    private String conversationIDForReply;
    private boolean replyMode, groupMode;
    private PhysiciansResponse physicians;
    private ArrayList<Boolean> selectedPhysicians;

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

        if (extras != null) {
            subjectForReply = extras.getString("subject");
            toPhysicianIDs = extras.getIntegerArrayList("toPhysicianIDs");
            groupMode = extras.getBoolean("groupMode", false);
            groupMode = groupMode || (toPhysicianIDs != null && !toPhysicianIDs.isEmpty());
        }
        else {
            subjectForReply = null;
            groupMode = false;
        }
        replyMode = (subjectForReply != null);
        if (groupMode) {
            recipientSpinner.setVisibility(View.GONE);
            if (!replyMode) { chooseGroupRecipientsButton.setVisibility(View.VISIBLE); }
            Skin.applyButtonStyle(this, chooseGroupRecipientsButton);
            chooseGroupRecipientsButton.setOnClickListener(new ShowRecipientChooserButtonListener());
            sendButton.setOnClickListener(new SendButtonListener());
        }
        else {
            recipientSpinner.setVisibility(View.VISIBLE);
            chooseGroupRecipientsButton.setVisibility(View.GONE);
            setupRecipientAdapter();
            sendButton.setOnClickListener(new SendButtonListener());
        }
        if (replyMode) {
            toPhysicianIDForReply  = extras.getInt("toPhysicianID");
            conversationIDForReply = extras.getString("conversationID");
            subjectEditText.setText(subjectForReply);
            subjectEditText.setEnabled(false);
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
        if (replyMode) {
            recipientSpinner.setEnabled(false);
            recipientAdapter.add(extras.getString("toPhysicianName"));
        }
        else {
            for (Physician physician : physicians.physicians) {
                recipientAdapter.add(physician.physicianName);
            }
        }
    }

    private boolean[] toPrimitiveBooleanArray(final List<Boolean> booleanList) {
        final boolean[] primitives = new boolean[booleanList.size()];
        int index = 0;
        for (Boolean object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }

    private int[] toPrimitiveIntegerArray(final List<Integer> booleanList) {
        final int[] primitives = new int[booleanList.size()];
        int index = 0;
        for (Integer object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }

    private class SendButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            LoggedInDataStorage storage = new LoggedInDataStorage(SendCustomMessageActivity.this);
            HashMap<String, String> userData = storage.RetrieveData();

            if (groupMode) {
                InAppNotificationGroupRequestContent message = new InAppNotificationGroupRequestContent();
                message.fromPhysicianID = Integer.parseInt(userData.get("physicianID"));
                message.fromPhysicianName = userData.get("name");
                message.practiceID = Integer.parseInt(userData.get("practiceID"));
                if (replyMode) {
                    message.subject = subjectForReply;
                    message.conversationID = conversationIDForReply;
                    assert messageEditText.getText() != null;
                    message.message = messageEditText.getText().toString();
                    ReplyToInAppGroupNotificationRequest req = new ReplyToInAppGroupNotificationRequest(SendCustomMessageActivity.this, message);
                    spiceManager.execute(req, new SendMessageListener());
                } else {
                    int i = 0;
                    toPhysicianIDs = new ArrayList<Integer>();
                    for (Physician physician: physicians.physicians) {
                        if (selectedPhysicians.get(i)) {
                            toPhysicianIDs.add(physician.physicianID);
                        }
                        ++i;
                    }
                    message.toPhysicianIDs = toPrimitiveIntegerArray(toPhysicianIDs);
                    assert subjectEditText.getText() != null;
                    message.subject = subjectEditText.getText().toString();
                    assert messageEditText.getText() != null;
                    message.message = messageEditText.getText().toString();
                    NewInAppGroupNotification req = new NewInAppGroupNotification(SendCustomMessageActivity.this, message);
                    spiceManager.execute(req, new SendMessageListener());
                }
            }
            else {
                InAppNotificationRequestContent message = new InAppNotificationRequestContent();
                message.fromPhysicianID = Integer.parseInt(userData.get("physicianID"));
                message.fromPhysicianName = userData.get("name");
                message.practiceID = Integer.parseInt(userData.get("practiceID"));
                if (replyMode) {
                    message.toPhysicianID = toPhysicianIDForReply;
                    message.subject = subjectForReply;
                    message.conversationID = conversationIDForReply;
                } else {
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
            }
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

            // Initialize the selection list if it doesn't exist yet
            if (selectedPhysicians == null) {
                selectedPhysicians = new ArrayList<Boolean>();
                for (Physician ignored : physicians.physicians) {
                    selectedPhysicians.add(false);
                }
            }
            final ArrayList<Boolean> newSelection = new ArrayList<Boolean>(selectedPhysicians);
            builder.setTitle("Choose recipients")
                    .setMultiChoiceItems(physicianNames.toArray(new CharSequence[physicianNames.size()]),
                            toPrimitiveBooleanArray(newSelection),
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        newSelection.set(which, true);
                                    } else if (newSelection.get(which)) {
                                        newSelection.set(which, false);
                                    }
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            selectedPhysicians = newSelection;
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // We can leave this empty, just ignore what has happened
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
