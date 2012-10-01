package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;
import java.util.Hashtable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ExpandableListView;
import android.view.ViewTreeObserver;

import com.newpush.greenwoodpediatrics.parser.OfficeLocationParser;

public class OfficeLocationActivity extends DefaultActivity {
	protected CustomExpandableListAdapter listAdapter;
	protected ExpandableListView officeLocationList;
	protected ArrayList<String> groups;
	protected ArrayList<ArrayList<String>> childs;

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
        childs = new ArrayList<ArrayList<String>>();

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.default_header, officeLocationList, false);
        officeLocationList.addHeaderView(header, null, false);

        listAdapter = new CustomExpandableListAdapter(this, groups, childs);
        officeLocationList.setAdapter(listAdapter);

        setTitleFromIntentBundle();
        new ParseLocations().execute();
    }


    private class ParseLocations extends AsyncTask<Void, Void, ArrayList<Hashtable<String, String>>> {
    	@Override
		protected ArrayList<Hashtable<String, String>> doInBackground(Void... params) {
            OfficeLocationParser parser = new OfficeLocationParser(getApplicationContext());
            ArrayList<Hashtable<String, String>> locations = parser.getFullInfo(getApplicationContext());
			return locations;
    	}

    	@Override
		protected void onPostExecute(ArrayList<Hashtable<String, String>> result) {
    		Integer i = 0;
    		for (Hashtable<String, String> location : result) {
    			String info = MarkupGenerator.officeLocation(
    					location.get("address"),
    					location.get("contactnumbers"),
    					location.get("dailyhours"),
    					location.get("custommessage"));
    			listAdapter.groups.add(location.get("name"));
    			listAdapter.childs.add(new ArrayList<String>());
    			listAdapter.childs.get(i).add(info);
    			i++;
    		}
    		listAdapter.notifyDataSetChanged();
    	}
    }
}
