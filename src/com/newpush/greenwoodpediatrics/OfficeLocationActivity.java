package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.support.v4.app.NavUtils;
import android.view.ViewTreeObserver;

import com.newpush.greenwoodpediatrics.parser.OfficeLocationParser;

public class OfficeLocationActivity extends Activity {
	protected CustomExpandableListAdapter listAdapter;
	protected ExpandableListView officeLocationList;
	protected ArrayList<String> groups;
	protected ArrayList<ArrayList<ArrayList<String>>> childs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_location);
        
        officeLocationList = (ExpandableListView) findViewById(R.id.officeLocationExpandableListView);
        ViewTreeObserver observer = officeLocationList.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	public void onGlobalLayout() {
        		officeLocationList.setIndicatorBounds(officeLocationList.getRight() - 40, officeLocationList.getWidth());
        	}
        });
        
        groups = new ArrayList<String>();
        childs = new ArrayList<ArrayList<ArrayList<String>>>();

        listAdapter = new CustomExpandableListAdapter(this, groups, childs);
        officeLocationList.setAdapter(listAdapter);
        new ParseLocations().execute();
    }


    private class ParseLocations extends AsyncTask<Void, Void, ArrayList<String>> {
    	protected ArrayList<String> doInBackground(Void... params) {
            OfficeLocationParser parser = new OfficeLocationParser(getApplicationContext());
            ArrayList<String> titles = parser.getTitles();
			return titles;
    	}

    	protected void onPostExecute(ArrayList<String> result) {
    		Integer i = 0;
    		for (String s : result) {
    			
    			listAdapter.groups.add(s);
    			listAdapter.childs.add(new ArrayList<ArrayList<String>>());
    			listAdapter.childs.get(i).add(new ArrayList<String>());
    			listAdapter.childs.get(i).get(0).add("Teszt" + i.toString());
    			i++;
    		}
    		listAdapter.notifyDataSetChanged();
    	}
    }
}
