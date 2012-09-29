package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;
import java.util.Hashtable;

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


    private class ParseLocations extends AsyncTask<Void, Void, ArrayList<Hashtable<String, String>>> {
    	protected ArrayList<Hashtable<String, String>> doInBackground(Void... params) {
            OfficeLocationParser parser = new OfficeLocationParser(getApplicationContext());
            ArrayList<Hashtable<String, String>> locations = parser.getFullInfo(getApplicationContext());
			return locations;
    	}

    	protected void onPostExecute(ArrayList<Hashtable<String, String>> result) {
    		Integer i = 0;
    		for (Hashtable<String, String> location : result) {
    			String info = MarkupGenerator.officeLocation(
    					location.get("address"), 
    					location.get("contactnumbers"), 
    					location.get("dailyhours"), 
    					location.get("custommessage"));
    			listAdapter.groups.add(location.get("name"));
    			listAdapter.childs.add(new ArrayList<ArrayList<String>>());
    			listAdapter.childs.get(i).add(new ArrayList<String>());
    			listAdapter.childs.get(i).get(0).add(info);
    			i++;
    		}
    		listAdapter.notifyDataSetChanged();
    	}
    }
}
