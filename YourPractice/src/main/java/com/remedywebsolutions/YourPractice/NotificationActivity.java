package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class NotificationActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_notification);
        new AlertDialog.Builder(NotificationActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("You have received a new message")
                .setMessage("You have received a new message.")
                .setPositiveButton("OK", null)
                .show();
    }






}
