package com.newpush.greenwoodpediatrics;

import android.app.Activity;
import android.os.Bundle;


public class AboutActivity extends DefaultActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.title_activity_about);
    }
}