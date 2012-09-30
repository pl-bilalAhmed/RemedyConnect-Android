package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

import com.newpush.greenwoodpediatrics.parser.IsYourChildSickParser;

public class IsYourChildSickActivity extends Activity {
	protected ArrayAdapter<String> adapter;
	protected ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isyourchildsick);
        
        list = (ListView) findViewById(R.id.iycsListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);
        new ParseIYCSCategories().execute();
    }

    private class ParseIYCSCategories extends AsyncTask<Void, Void, ArrayList<String>> {
    	protected ArrayList<String> doInBackground(Void... params) {
    		IsYourChildSickParser parser = new IsYourChildSickParser(getApplicationContext());
    		ArrayList<String> categories = parser.getCategories();
    		return categories;
    	}
    	
    	protected void onPostExecute(ArrayList<String> categories) {
    		for (String category : categories) {
    			adapter.add(category);
    		}
    	}
    	
    }

}
