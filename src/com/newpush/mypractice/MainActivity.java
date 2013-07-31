package com.newpush.mypractice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends DefaultActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void checkDownloadedData() {
        if (DataChecker.isDataAvailable(getApplicationContext())) {
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
