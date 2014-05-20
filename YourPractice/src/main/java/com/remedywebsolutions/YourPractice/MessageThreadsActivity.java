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

import com.remedywebsolutions.YourPractice.MedSecureAPI.DateOperations;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThread;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MessageThreads;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageThreadsActivity extends DefaultActivity {
    protected ArrayAdapter<MessageThread> threadsListAdapter;
    protected ListView threadsListView;
    private ArrayList<InboxItem> inboxItems;
    private ArrayList<SentItem> sentItems;
    private HashMap<String, String> loginData;
    private MessageThreads threads;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);
        Skin.applyActivityBackground(this);
        setTitle("Your messages");

        loginData = new LoggedInDataStorage(this).RetrieveData();
        if (extras.get("inboxContents") instanceof ArrayList) {
            //noinspection unchecked
            inboxItems = (ArrayList<InboxItem>) extras.get("inboxContents");
        }
        if (extras.get("sentContents") instanceof ArrayList) {
            //noinspection unchecked
            sentItems = (ArrayList<SentItem>) extras.get("sentContents");
        }

        String username = loginData.get("name");
        int ownPhysicianID = Integer.parseInt(loginData.get("physicianID"));
        threads = new MessageThreads(inboxItems, sentItems, username, ownPhysicianID);
        final ArrayList<MessageThread> threadList = threads.getSortedThreads();

        threadsListView = (ListView) findViewById(R.id.messageThreads);
        threadsListAdapter = new ArrayAdapter<MessageThread>(this, R.layout.message_thread_row,
                new ArrayList<MessageThread>()) {

            public View getView(int pos, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.message_thread_row, parent, false);
                }
                assert v != null;
                TextView messagePartnersTextView = (TextView) v.findViewById(R.id.messagePartnersTextView);
                TextView subjectTextView = (TextView) v.findViewById(R.id.messageSubjectTextView);
                TextView timeTextView = (TextView) v.findViewById(R.id.timeAgo);
                ImageView readStatusImageView = (ImageView) v.findViewById(R.id.mailIcon);

                MessageThread currentThread = threadList.get(pos);

                messagePartnersTextView.setText(currentThread.getRecipientNameList());
                subjectTextView.setText(currentThread.getSubject());
                timeTextView.setText(
                        DateOperations.getRelativeTimeForTimeString(currentThread.getLastUpdate()));

                if (currentThread.hasBeenRead()) {
                    readStatusImageView.setImageResource(R.drawable.mailopened_black);
                }
                else {
                    readStatusImageView.setImageResource(R.drawable.mail_black);
                }

                messagePartnersTextView.setTypeface(Skin.menuFont(MessageThreadsActivity.this));
                subjectTextView.setTypeface(Skin.menuFont(MessageThreadsActivity.this));
                return v;
            }
        };

        threadsListView.setAdapter(threadsListAdapter);
        if (threads != null) {
            for (MessageThread thread : threadList) {
                threadsListAdapter.add(thread);
            }
        }

        threadsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent threadDisplayActivity = new Intent(MessageThreadsActivity.this, MessageThreadActivity.class);
                threadDisplayActivity.putExtra("thread", threadList.get(position));
                startActivityForResult(threadDisplayActivity, position);
            }
        });
    }

}
