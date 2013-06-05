package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends DefaultActivity {
	protected ArrayAdapter<String> menuadapter;
	protected ListView menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (ListView) findViewById(R.id.mainMenu);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.default_header, menu, false);
        menu.addHeaderView(header, null, false);

    	menuadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
    	menu.setAdapter(menuadapter);

    	setTitle(res.getString(R.string.welcome));

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    	getSupportActionBar().setHomeButtonEnabled(false);
    		
    	if (DataChecker.isDataAvailable(getApplicationContext())) {
    		MainViewController.FireActivity(this, "index.xml");
    		finish();
    	}
    	else {
    		Intent downloadActivity = new Intent(this, DownloadActivity.class);
            startActivityForResult(downloadActivity, 0);
    	}
    }
}
