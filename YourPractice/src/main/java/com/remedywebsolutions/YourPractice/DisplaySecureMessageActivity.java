package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.BaseResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.SecureCallMessage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.MarkCallAsOpenedRequest;

import java.io.IOException;


public class DisplaySecureMessageActivity extends DefaultActivity implements RequestListener<BaseResponse> {

    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
    SecureCallMessage call;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_secure_message);

   //     Skin.applyMainMenuBackground(this);

        Skin.applyThemeLogo(this, false);
        TextView message = (TextView) findViewById(R.id.call);
        call = (SecureCallMessage)getIntent().getSerializableExtra("call");
        message.setText(call.message);

        TextView phone = (TextView) findViewById(R.id.clickablePhone);
        java.text.MessageFormat phoneMsgFmt=new java.text.MessageFormat("({0})-{1}-{2}");
        //suposing a grouping of 3-3-4
        String[] phoneNumArr={call.phone.substring(0, 3),
                call.phone.substring(3,6),
                call.phone.substring(6)};

        phone.setText(phoneMsgFmt.format(phoneNumArr));
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
        if(!call.wasOpened) {
            MarkCallAsOpenedRequest cntReq = new MarkCallAsOpenedRequest(this, call.callID);
            spiceManager.execute(cntReq, this);
        }
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(call.callID);

    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }


    @Override
    public void onRequestFailure(SpiceException e) {
        progress.dismiss();
        reloginSpiceFailureHandler(e);
    }

    @Override
    public void onRequestSuccess(BaseResponse inboxItemsResponse) {

    }


    private void reloginSpiceFailureHandler(SpiceException e) {
        spiceManager.cancelAllRequests();
        if (e.getCause() instanceof IOException) {
            // We should have an authentication failure here, so re-login the user...
            new AlertDialog.Builder(DisplaySecureMessageActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Your session has expired")
                    .setMessage("You will need to log in again. Please press OK to proceed.")
                    .setPositiveButton("OK", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent loginIntent = new Intent(DisplaySecureMessageActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
                    })
                    .show();
        }
    }
}
