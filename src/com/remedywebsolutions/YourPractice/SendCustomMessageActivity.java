package com.remedywebsolutions.YourPractice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InAppNotificationRequestContent;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

import java.util.HashMap;

public class SendCustomMessageActivity extends DefaultActivity {
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    EditText subjectEditText, messageEditText;
    private String subjectForReply;
    private int toPhysicianIDForReply;
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
        final EditText subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        final EditText messageEditText = (EditText) findViewById(R.id.messageEditText);

        subjectForReply = extras.getString("subject");
        replyMode = (subjectForReply != null);
        if (replyMode) {
            toPhysicianIDForReply  = extras.getInt("toPhysicianID");
            subjectEditText.setText(subjectForReply);
            subjectEditText.setEnabled(false); // @TODO Should we leave this editable?
        }

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
                }
                else {
                    message.toPhysicianID = 405; // @TODO This isn't easy to implement
                    message.subject = subjectEditText.getText().toString();
                }
                message.message = messageEditText.getText().toString();
                SendInAppNotificationRequest req = new SendInAppNotificationRequest(SendCustomMessageActivity.this, message);
                spiceManager.execute(req, new SendMessageListener());
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
            setProgressMessageWaitAndDismiss("Message sent.");
        }
    }
}
