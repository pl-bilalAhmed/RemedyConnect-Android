package com.newpush.greenwoodpediatrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.newpush.greenwoodpediatrics.DownloadActivity;
import com.newpush.greenwoodpediatrics.DataChecker;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends Activity {
	protected ArrayAdapter<String> menuadapter;
	protected ListView menu;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        menu = (ListView) findViewById(R.id.mainMenu);
        
    	menuadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
    	menu.setAdapter(menuadapter);
        
    	if (DataChecker.isDataAvailable(getApplicationContext())) {
    		setupMenu();
    	}
    	else {
			jumpToDownloadActivity();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_update:
                jumpToDownloadActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    protected void setupMenu() {
    	if (menuadapter.getCount() == 0) {
    		Resources res = getResources();
        	for (String s : res.getStringArray(R.array.MainMenuItems)) {
        		menuadapter.add(s);
        	}
        	
        	menu.setOnItemClickListener(new OnItemClickListener() {
    		  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		    jumpToActivity(position);
    		  }
    		});	
    	}	 
    }
    
    protected void jumpToDownloadActivity() {
		Intent downloadActivity = new Intent(this, DownloadActivity.class);
        startActivityForResult(downloadActivity, 0);
    }
    
    protected void jumpToActivity(int position) {
    	switch (position) {
    		case 0:
				Intent officeInfoIntent = new Intent(this, OfficeInfoActivity.class);
				startActivity(officeInfoIntent);
    			break;
    		case 1:
				Intent practiceNewsIntent = new Intent(this, PracticeNewsActivity.class);
				startActivity(practiceNewsIntent);
    			break;
    	}
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode == Activity.RESULT_OK) {
    		setupMenu();
    	}
    }
}
