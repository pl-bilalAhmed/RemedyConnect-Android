package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class OfficeActivity extends DefaultActivity {
	protected ArrayAdapter<String> menuadapter;
	protected ListView menu;
	
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

    	setTitle(res.getString(R.string.title_activity_office_info_hours_location));
    	makeTextViewLinksClickable(R.id.footerTextView);

    	setupMenu();
    }

    protected void setupMenu() {
    	if (menuadapter.getCount() < 2) {
        	for (String s : res.getStringArray(R.array.OfficeInfoLocationHoursMenuItems)) {
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
    	ArrayList<String> menuitem_titles = new ArrayList<String>(Arrays.asList(res.getStringArray(R.array.OfficeInfoLocationHoursMenuItems)));
    	switch (position) {
    		case 0:
    			Intent officeInfoIntent = new Intent(this, OfficeInfoActivity.class);
				officeInfoIntent.putExtra("title", menuitem_titles.get(position));
				startActivity(officeInfoIntent);
    			break;
    		case 1:
    			Intent locationIntent = new Intent(this, OfficeLocationActivity.class);
    			locationIntent.putExtra("title", menuitem_titles.get(position));
    			startActivity(locationIntent);
    			break;
    	}
    }
}
