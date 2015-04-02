package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public class NotificationActivity extends DefaultActivity {

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
        new AlertDialog.Builder(NotificationActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("You have received a new message")
                .setMessage("You have received a new message.")
                .setPositiveButton("OK", null)
                .show();

       // Intent intent = new Intent(this, SecureCallListActivity.class);
       // startActivity(intent);
    }


}
