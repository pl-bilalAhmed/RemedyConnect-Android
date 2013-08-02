package com.newpush.mypractice;

import android.os.Bundle;


public class AboutActivity extends DefaultActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.title_activity_about);
        Skin.applyThemeLogo(this);
    }
}