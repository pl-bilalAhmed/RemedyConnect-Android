package com.newpush.YourPractice;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends DefaultActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void checkDownloadedData() {
        if (Data.isDataAvailable(getApplicationContext())) {
            MainViewController.FireActivity(this, "index.xml");
            finish();
        } else {
            Intent practiceSearchActivity = new Intent(this, PracticeSearchActivity.class);
            startActivity(practiceSearchActivity);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkDownloadedData();
    }
}
