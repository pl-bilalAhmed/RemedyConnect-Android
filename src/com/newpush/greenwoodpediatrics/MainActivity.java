package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.newpush.greenwoodpediatrics.DataChecker;

public class MainActivity extends DefaultActivity {
	protected ArrayAdapter<String> menuadapter;
	protected ListView menu;
	public static final String PREFS_NAME = "prefs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (ListView) findViewById(R.id.mainMenu);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.default_header, menu, false);
        ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.default_footer, menu, false);
        menu.addHeaderView(header, null, false);
        menu.addFooterView(footer, null, false);

    	menuadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
    	menu.setAdapter(menuadapter);

    	setTitle(res.getString(R.string.welcome));
    	makeTextViewLinksClickable(R.id.footerTextView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    	getSupportActionBar().setHomeButtonEnabled(false);
    		
    	if (DataChecker.isDataAvailable(getApplicationContext())) {
    		setupMenu();
    	}
    	else {
        	Intent downloadActivity = new Intent(this, DownloadActivity.class);
            startActivityForResult(downloadActivity, 0);
    	}
    }

    protected void setupMenu() {
    	if (menuadapter.getCount() < 2) {
        	for (String s : res.getStringArray(R.array.MainMenuItems)) {
        		menuadapter.add(s);
        	}

        	menu.setOnItemClickListener(new OnItemClickListener() {
    		  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		    jumpToActivity(position-1); // -1 because of the header element
    		  }
    		});
    	}
    }

    protected void jumpToActivity(int position) {
    	ArrayList<String> menuitem_titles = new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.MainMenuItems)));
    	switch (position) {
    		case 0:
    			Intent isYourChildSickIntent = new Intent(this, IsYourChildSickActivity.class);
    			isYourChildSickIntent.putExtra("title", menuitem_titles.get(position));
    			startActivity(isYourChildSickIntent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    			break;
    		case 1:
    			Intent officeIntent = new Intent(this, OfficeActivity.class);
    			officeIntent.putExtra("title", menuitem_titles.get(position));
    			startActivity(officeIntent);
    			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    			break;
    		case 2:
				Intent practiceNewsIntent = new Intent(this, PracticeNewsActivity.class);
				practiceNewsIntent.putExtra("title", menuitem_titles.get(position));
				startActivity(practiceNewsIntent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    			break;
    		case 3:
    	        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	        boolean firstTime = settings.getBoolean("firstPageMyDoctor", true);

    	        if (firstTime) {
    	        	Intent pageMyDoctorIntent = new Intent(this, PageMyDoctorActivity.class);
        			pageMyDoctorIntent.putExtra("title", menuitem_titles.get(position));
        			startActivity(pageMyDoctorIntent);
        			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    	        }
    	        else {
    	        	Intent pageMyDoctorIntent = PageMyDoctorActivity.getPageMyDoctorIntent();
    	        	startActivity(pageMyDoctorIntent);
    	        	overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    	        }

    			break;
    	}
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode == Activity.RESULT_OK) {
    		setupMenu();
    	}
    }
}
