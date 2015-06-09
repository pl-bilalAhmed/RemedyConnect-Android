package com.remedywebsolutions.YourPractice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.DateOperations;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.MessageItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MessageListActivity extends DefaultActivity {
    protected ArrayAdapter<MessageItem> messageListAdapter;
    protected ListView messageListView;
    private ArrayList<InboxItem> inboxItems;
    private ArrayList<SentItem> sentItems;
    private boolean inboxMode;
    private HashMap<String, String> loginData;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Message list");
        setContentView(R.layout.activity_message_list);

        loginData = new LoggedInDataStorage(this).RetrieveData();

        Boolean shouldReverse = extras.getBoolean("shouldReverseList", true);
        if (extras.get("inboxContents") instanceof ArrayList) {
            //noinspection unchecked
            inboxItems = (ArrayList<InboxItem>) extras.get("inboxContents");
        }
        if (inboxItems != null && shouldReverse) { Collections.reverse(inboxItems); }
        if (extras.get("sentContents") instanceof ArrayList) {
            //noinspection unchecked
            sentItems = (ArrayList<SentItem>) extras.get("sentContents");
        }
        if (sentItems != null && shouldReverse) { Collections.reverse(sentItems); }
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
                    v = inflater.inflate(R.layout.messagelist_row, parent, false);
                }
                assert v != null;
                TextView messagePartnerTextView = (TextView)v.findViewById(R.id.messagePartnerTextView);
                TextView subjectTextView = (TextView)v.findViewById(R.id.messageSubjectTextView);
                TextView timeTextView = (TextView)v.findViewById(R.id.timeAgo);
                ImageView readStatusImageView = (ImageView)v.findViewById(R.id.mailIcon);
                if (inboxMode) {
                    InboxItem currentItem = inboxItems.get(pos);
                    messagePartnerTextView.setText(currentItem.fromPhysicianName);
                    subjectTextView.setText(currentItem.subject);
                    try {
                        timeTextView.setText("Received " +
                                DateOperations.reformatToLocalRelative(currentItem.dateReceived));
                    } catch (ParseException e) {
                        timeTextView.setVisibility(View.GONE);
                    }

                    if (currentItem.dateOpened != null) {
                        readStatusImageView.setImageResource(R.drawable.mailopened_black);
                    }
                    else {
                        readStatusImageView.setImageResource(R.drawable.mail_black);
                    }
                }
                else {
                    SentItem currentItem = sentItems.get(pos);
                    int selfPhysicianID = Integer.parseInt(loginData.get("physicianID"));
                    messagePartnerTextView.setText(currentItem.getFilteredRecipients(selfPhysicianID));
                    subjectTextView.setText(currentItem.subject);
                    try {
                        timeTextView.setText("Sent " +
                                DateOperations.reformatToLocalRelative(currentItem.dateSent));
                    } catch (ParseException e) {
                        timeTextView.setVisibility(View.GONE);
                    }

                    if (currentItem.dateRead != null) {
                        readStatusImageView.setImageResource(R.drawable.mailopened_black);
                    }
                    else {
                        readStatusImageView.setImageResource(R.drawable.mail_black);
                    }
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
            displayActivity.putExtra("inboxMode", inboxMode);
            displayActivity.putExtra("position", position);
            if (inboxMode) {
                displayActivity.putExtra("inboxItems", inboxItems);
            }
            else {
                displayActivity.putExtra("sentItems", sentItems);
            }
            startActivityForResult(displayActivity, position);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (inboxMode) {
            inboxItems.get(requestCode).dateOpened = "READ";
            messageListAdapter.notifyDataSetChanged();
        }
    }



}
