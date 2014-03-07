package com.remedywebsolutions.YourPractice;

import android.os.Bundle;
import android.widget.TextView;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;

public class MessageDisplayActivity extends DefaultActivity {
    private boolean inboxMode;
    private InboxItem inboxItem;
    private SentItem sentItem;
    private TextView nameView, subjectView, receivedView, messageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_display);
        nameView = (TextView) findViewById(R.id.fromTextView);
        subjectView = (TextView) findViewById(R.id.subjectTextView);
        receivedView = (TextView) findViewById(R.id.receivedTextView);
        messageView = (TextView) findViewById(R.id.messageTextView);

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
        }
        else {
            sentItem = (SentItem) extras.get("messageItem");
            nameView.setText(sentItem.toPhysicianName);
            subjectView.setText(sentItem.subject);
            receivedView.setText(sentItem.dateSent);
            messageView.setText(sentItem.message);
        }
    }
}
