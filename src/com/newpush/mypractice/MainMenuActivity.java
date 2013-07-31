package com.newpush.mypractice;

import android.app.Activity;
import android.os.Bundle;
import com.newpush.mypractice.DefaultActivity;

public class MainMenuActivity extends DefaultActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (extras.getBoolean("isRoot")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }
}