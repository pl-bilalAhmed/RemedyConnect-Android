package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import com.newpush.greenwoodpediatrics.parser.IsYourChildSickSubParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class IsYourChildSickItemActivity extends Activity {
	protected ListView list;
	protected Bundle extras;
	protected ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_your_child_sick_item);
        list = (ListView) findViewById(R.id.iycsItemListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(adapter);
        extras = getIntent().getExtras();
        String id_from_extras = extras.getString("category_id");
        Integer id = Integer.valueOf(id_from_extras);
        new ParseIYCSItem().execute(id);
    }
    
    private class ParseIYCSItem extends AsyncTask<Integer, Void, ArrayList<String>> {
		protected ArrayList<String> doInBackground(Integer... params) {
			IsYourChildSickSubParser parser = new IsYourChildSickSubParser(getApplicationContext(), params[0]);
			return parser.getArticleTitles();
		}
		
		protected void onPostExecute(ArrayList<String> titles) {
			setTitle(extras.getString("category_name"));
			for (String title : titles) {
				adapter.add(title);
			}
		}
    	
    }


}
