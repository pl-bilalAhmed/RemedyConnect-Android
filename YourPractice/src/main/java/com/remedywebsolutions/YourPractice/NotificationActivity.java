package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



public class NotificationActivity extends DefaultActivity implements DialogInterface.OnClickListener {

    private int mcid = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_notification);

        Skin.applyMainMenuBackground(this);

        Skin.applyThemeLogo(this, true);

    }



    @Override
    public void onStart()
    {
        super.onStart();
        int cid = 1;
        String alert = "You received a secure message";

        try {
            alert = getIntent().getStringExtra("alert");
            int indexof = alert.lastIndexOf(":");
            if (indexof > 0) {
                String callId = alert.substring(indexof + 2);
                alert = alert.substring(0, indexof + 1);
                cid = Integer.parseInt(callId);
                mcid = cid;
            }


        }
        catch(Exception ex)
        {

        }
        displayNotification(cid, alert);
        AlertDialog al = new AlertDialog.Builder(NotificationActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(alert)
                .setMessage(alert)
                .setPositiveButton("OK",this)

                .show();


       // Intent intent = new Intent(this, SecureCallListActivity.class);
       // startActivity(intent);
    }

    public void onClick(DialogInterface dialog, int id) {
        // if this button is clicked, close
        // current activity
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mcid);
        Intent intent = new Intent(getApplicationContext(), SecureCallListActivity.class);
        finish();
        startActivity(intent);



    }

    protected void displayNotification(int callid,String alert) {
        Log.i("Start", "notification");

        // Invoking the default notification service //
        NotificationCompat.Builder  mBuilder =
                new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true);

        mBuilder.setContentTitle(alert);
        mBuilder.setContentText(alert);
        mBuilder.setTicker(alert);
        mBuilder.setSmallIcon(R.drawable.rcdiamond);
      //  Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final String packageName = getApplicationContext().getPackageName();

        Uri notification = Uri.parse("android.resource://" + packageName + "/" + R.raw.rocsound );
        mBuilder.setSound(notification);


        // Increase notification number every time a new notification arrives //
        mBuilder.setNumber(callid);

        // Creates an explicit intent for an Activity in your app //

        Intent resultIntent = new Intent(this, SecureCallListActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SecureCallListActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack //
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        callid,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        //  mBuilder.setOngoing(true);
        Notification note = mBuilder.build();
        note.defaults |= Notification.DEFAULT_VIBRATE;
        note.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationID allows you to update the notification later on. //
        mNotificationManager.notify(callid, mBuilder.build());

    }

}
