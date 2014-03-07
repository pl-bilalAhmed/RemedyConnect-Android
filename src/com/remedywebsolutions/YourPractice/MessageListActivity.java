package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class MessageListActivity extends DefaultActivity {
    protected ArrayAdapter<MessageItem> messageListAdapter;
    protected ListView messageListView;
    private ArrayList<InboxItem> inboxItems;
    private ArrayList<SentItem> sentItems;
    private boolean inboxMode;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        messageListAdapter = new ArrayAdapter<MessageItem>(this, android.R.layout.simple_list_item_2, new ArrayList<MessageItem>()) {
            public View getView(int pos, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(android.R.layout.simple_list_item_2, null);
                }
                TextView titleView = (TextView)v.findViewById(android.R.id.text1);
                TextView subTitleView = (TextView)v.findViewById(android.R.id.text2);
                if (inboxMode) {
                    InboxItem currentItem = inboxItems.get(pos);
                    titleView.setText(currentItem.fromPhysicianName);
                    subTitleView.setText(currentItem.subject);
                }
                else {
                    SentItem currentItem = sentItems.get(pos);
                    titleView.setText(currentItem.toPhysicianName);
                    subTitleView.setText(currentItem.subject);
                }

                titleView.setTypeface(Skin.menuFont(MessageListActivity.this));
                subTitleView.setTypeface(Skin.menuFont(MessageListActivity.this));
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
}
