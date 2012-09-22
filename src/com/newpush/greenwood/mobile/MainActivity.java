package com.newpush.greenwood.mobile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.newpush.greenwood.mobile.DownloadActivity;

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
        
        Intent downloadActivity = new Intent(this, DownloadActivity.class);
        startActivityForResult(downloadActivity, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    protected void setupMenu() {
    	String[] menuItemLabels = {
    			"Office Info",
    			"-not implemented yet-",
    			"-not implemented yet-"
    	};
    	for (String s : menuItemLabels) {
    		menuadapter.add(s);
    	}
    	
    	menu.setOnItemClickListener(new OnItemClickListener() {
		  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    jumpToActivity(position);
		  }
		}); 
    }
    
    protected void jumpToActivity(int position) {
    	switch (position) {
    		case 0:
    				Intent officeInfoIntent = new Intent(this, OfficeInfoActivity.class);
    				startActivity(officeInfoIntent);
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
