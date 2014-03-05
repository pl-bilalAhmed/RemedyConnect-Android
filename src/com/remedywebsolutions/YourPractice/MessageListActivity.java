package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;

import java.util.ArrayList;

public class MessageListActivity extends DefaultActivity {
    protected ArrayAdapter<String> messageListAdapter;
    protected ListView messageListView;
    private ArrayList<InboxItem> inboxItems;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        inboxItems = (ArrayList<InboxItem>) extras.get("inboxContents");
        Skin.applyActivityBackground(this);
        messageListView = (ListView) findViewById(R.id.messageList);
        messageListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, new ArrayList<String>()) {
            public View getView(int pos, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(android.R.layout.simple_list_item_2, null);
                }
                TextView titleView = (TextView)v.findViewById(android.R.id.text1);
                TextView subTitleView = (TextView)v.findViewById(android.R.id.text2);
                InboxItem currentItem = inboxItems.get(pos);
                titleView.setText(currentItem.fromPhysicianName);
                subTitleView.setText(currentItem.subject);
                titleView.setTypeface(Skin.menuFont(MessageListActivity.this));
                subTitleView.setTypeface(Skin.menuFont(MessageListActivity.this));
                return v;
            }
        };
        messageListView.setAdapter(messageListAdapter);
        if (inboxItems != null) {
            for (InboxItem item : inboxItems) {
                messageListAdapter.add(item.fromPhysicianName);
            }
            messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MessageListActivity.this,
                            formatInboxItemForToast(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String formatInboxItemForToast(int index) {
        InboxItem message = inboxItems.get(index);
        String result = "Contents:\n" + message.message;
        return result;
    }
}
