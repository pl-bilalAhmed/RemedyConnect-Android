package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.ImageListView.SecureMessageAdapter;
import com.remedywebsolutions.YourPractice.MedSecureAPI.DateOperations;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderCallsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.InboxItemsResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.MessageItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SecureCallMessage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SentItem;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationInboxItemsRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetInAppNotificationSentItemsRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetProviderCallsRequest;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SecureCallListActivity extends DefaultActivity {
    protected SecureMessageAdapter secureMessageAdapter;
    protected ListView messageListView;
    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
   private ArrayList<SecureCallMessage> responses;
    private HashMap<String, String> loginData;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Message list");
        setContentView(R.layout.activity_message_list);

        loginData = new LoggedInDataStorage(this).RetrieveData();

     //   Boolean shouldReverse = extras.getBoolean("shouldReverseList", true);

        messageListView = (ListView) findViewById(R.id.messageList);
        responses = new ArrayList<SecureCallMessage>();
        secureMessageAdapter = new SecureMessageAdapter(this,  responses);
        messageListView.setAdapter(secureMessageAdapter);

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent displayActivity = new Intent(SecureCallListActivity.this, MessageDisplayActivity.class);

            startActivityForResult(displayActivity, position);
            }
        });
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
        refreshMessages();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
    private void refreshMessages() {

        progress.setMessage("Refreshing messages...");
        progress.show();
        GetProviderCallsRequest inboxReq = new GetProviderCallsRequest(this);
        spiceManager.execute(inboxReq, new InboxRequestListener());

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    private final class InboxRequestListener implements RequestListener<GetProviderCallsResponse> {
        @Override
        public void onRequestFailure(SpiceException e) {
            progress.dismiss();
            reloginSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(GetProviderCallsResponse inboxItemsResponse) {
            if(inboxItemsResponse.successfull) {
                responses = inboxItemsResponse.messages;
                secureMessageAdapter.updateData(responses);
                //   updateNumberOfInboxItems(inbox.size());
                 progress.dismiss();
            }
        }
    }

    private void reloginSpiceFailureHandler(SpiceException e) {
        spiceManager.cancelAllRequests();
        if (e.getCause() instanceof IOException) {
            // We should have an authentication failure here, so re-login the user...
            new AlertDialog.Builder(SecureCallListActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Your session has expired")
                    .setMessage("You will need to log in again. Please OK to proceed.")
                    .setPositiveButton("OK", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent loginIntent = new Intent(SecureCallListActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
                    })
                    .show();
        }
    }
}


