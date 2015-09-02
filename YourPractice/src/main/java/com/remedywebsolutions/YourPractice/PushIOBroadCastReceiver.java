package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import com.pushio.manager.PushIOManager;

/**
 * Created by ksciacca on 3/27/2015.
 */
public class PushIOBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String alert = intent.getStringExtra("alert");
        if(alert != null) {
            Toast.makeText(context, alert, Toast.LENGTH_LONG).show();

            Intent notificatIntent = new Intent(context.getApplicationContext(), NotificationActivity.class);
            notificatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificatIntent.putExtra("alert", alert);
            context.getApplicationContext().startActivity(notificatIntent);
        }

        Bundle extras = getResultExtras(true);
        extras.putInt(PushIOManager.PUSH_STATUS, PushIOManager.PUSH_HANDLED_IN_APP);

        setResultExtras(extras);
        this.abortBroadcast();

    }
}
