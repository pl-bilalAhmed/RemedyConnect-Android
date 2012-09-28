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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

import com.newpush.greenwoodpediatrics.parser.OfficeLocationParser;

public class OfficeLocationActivity extends Activity {
	protected ArrayAdapter<String> officeLocationAdapter;
	protected ListView officeLocationList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_location);

        officeLocationList = (ListView) findViewById(R.id.officeLocationListView);
        officeLocationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        officeLocationList.setAdapter(officeLocationAdapter);
        new ParseLocations().execute();
    }

    protected void attachLocationLinks() {
    	/*officeLocationList.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Intent infoItemIntent = new Intent(getBaseContext(), OfficeInfoItemActivity.class);
    			infoItemIntent.putExtra("which", position);
    			startActivity(infoItemIntent);
    		}
    	});*/
    }

    private class ParseLocations extends AsyncTask<Void, Void, ArrayList<String>> {
    	protected ArrayList<String> doInBackground(Void... params) {
            OfficeLocationParser parser = new OfficeLocationParser(getApplicationContext());
            ArrayList<String> titles = parser.getTitles();
			return titles;
    	}

    	protected void onPostExecute(ArrayList<String> result) {
    		for (String s : result) {
    			officeLocationAdapter.add(s);
    		}
    		attachLocationLinks();
    	}
    }
}
