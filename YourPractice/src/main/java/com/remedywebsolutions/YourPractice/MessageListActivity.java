package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.MessageItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageListActivity extends DefaultActivity {
    protected ArrayAdapter<MessageItem> messageListAdapter;
    protected ListView messageListView;
    private ArrayList<InboxItem> inboxItems;
    private ArrayList<SentItem> sentItems;
    private boolean inboxMode;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Message list");
        setContentView(R.layout.activity_message_list);
        inboxItems = (ArrayList<InboxItem>) extras.get("inboxContents");
        sentItems = (ArrayList<SentItem>) extras.get("sentContents");
        inboxMode =  (inboxItems != null);

        Skin.applyActivityBackground(this);
        if (inboxMode) {
            setTitle("Inbox");
        }
        else {
            setTitle("Sent messages");
        }
        messageListView = (ListView) findViewById(R.id.messageList);
        messageListAdapter = new ArrayAdapter<MessageItem>(this, R.layout.messagelist_row, new ArrayList<MessageItem>()) {
            public View getView(int pos, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.messagelist_row, null);
                }
                TextView messagePartnerTextView = (TextView)v.findViewById(R.id.messagePartnerTextView);
                TextView subjectTextView = (TextView)v.findViewById(R.id.messageSubjectTextView);
                TextView timeTextView = (TextView)v.findViewById(R.id.timeAgo);
                ImageView readStatusImageView = (ImageView)v.findViewById(R.id.mailIcon);
                if (inboxMode) {
                    InboxItem currentItem = inboxItems.get(pos);
                    messagePartnerTextView.setText(currentItem.fromPhysicianName);
                    subjectTextView.setText(currentItem.subject);
                    timeTextView.setText("Received " + getRelativeTimeForTimeString(currentItem.dateReceived));
                    if (currentItem.dateOpened != null) {
                        readStatusImageView.setImageResource(R.drawable.mailopened_black);
                    }
                    else {
                        readStatusImageView.setImageResource(R.drawable.mail_black);
                    }
                }
                else {
                    SentItem currentItem = sentItems.get(pos);
                    messagePartnerTextView.setText(currentItem.toPhysicianName);
                    subjectTextView.setText(currentItem.subject);
                    // @TODO: this should be the read time instead...
                    timeTextView.setText("Sent " + getRelativeTimeForTimeString(currentItem.dateSent));
                    readStatusImageView.setVisibility(View.GONE);
                }

                messagePartnerTextView.setTypeface(Skin.menuFont(MessageListActivity.this));
                subjectTextView.setTypeface(Skin.menuFont(MessageListActivity.this));
                return v;
            }
        };
        messageListView.setAdapter(messageListAdapter);
        if (inboxMode && inboxItems != null) {
            for (InboxItem item : inboxItems) {
                messageListAdapter.add(item);
            }
        }
        else if (!inboxMode && sentItems != null ) {
            for (SentItem item : sentItems) {
                messageListAdapter.add(item);
            }
        }
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent displayActivity = new Intent(MessageListActivity.this, MessageDisplayActivity.class);
            if (inboxMode) {
                displayActivity.putExtra("messageItem", inboxItems.get(position));
            }
            else {
                displayActivity.putExtra("messageItem", sentItems.get(position));
            }
            displayActivity.putExtra("inboxMode", inboxMode);
            startActivity(displayActivity);
            }
        });
    }

    private String getRelativeTimeForTimeString(String timeString) {
        Date date = ParseDateTimeString(timeString);
        return DateUtils.getRelativeTimeSpanString(date.getTime()).toString();
    }

    public Date ParseDateTimeString(String dateTimeString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(dateTimeString);
        } catch (ParseException e) {
            return null;
        }
    }
}
