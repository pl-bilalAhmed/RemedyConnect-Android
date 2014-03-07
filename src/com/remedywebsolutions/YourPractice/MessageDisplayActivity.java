package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;

public class MessageDisplayActivity extends DefaultActivity {

    private InboxItem inboxItem;
    private TextView nameView, subjectView, receivedView, messageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_display);
        inboxItem = (InboxItem) extras.get("inboxItem");
        nameView = (TextView) findViewById(R.id.fromTextView);
        subjectView = (TextView) findViewById(R.id.subjectTextView);
        receivedView = (TextView) findViewById(R.id.receivedTextView);
        messageView = (TextView) findViewById(R.id.messageTextView);

        nameView.setText("");
        subjectView.setText("");
        receivedView.setText("");
        messageView.setText("");

        nameView.setText(inboxItem.fromPhysicianName);
        subjectView.setText(inboxItem.subject);
        receivedView.setText(inboxItem.dateReceived);
        messageView.setText(inboxItem.message);
    }
}
