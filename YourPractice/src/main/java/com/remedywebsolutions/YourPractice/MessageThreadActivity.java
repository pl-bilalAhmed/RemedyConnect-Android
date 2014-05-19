package com.remedywebsolutions.YourPractice;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.remedywebsolutions.YourPractice.MedSecureAPI.DateOperations;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThread;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThreadMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageThreadActivity extends DefaultActivity {
    protected ArrayAdapter<MessageThreadMessage> threadListAdapter;
    protected ListView threadListView;
    private HashMap<String, String> loginData;
    private MessageThread thread;

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

        partners.setTypeface(Skin.menuFont(MessageThreadActivity.this));
        subject.setTypeface(Skin.menuFont(MessageThreadActivity.this));
        Skin.applyButtonStyle(this, deleteMessage);
        Skin.applyButtonStyle(this, replyMessage);

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

}
