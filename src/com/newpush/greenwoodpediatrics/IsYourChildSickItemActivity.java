package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import com.newpush.greenwoodpediatrics.parser.IsYourChildSickSubParser;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class IsYourChildSickItemActivity extends Activity {
	protected ListView list;
	protected Bundle extras;
	protected ArrayAdapter<String> adapter;
	protected Integer category_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_your_child_sick_item);
        list = (ListView) findViewById(R.id.iycsItemListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(adapter);
        extras = getIntent().getExtras();
        String id_from_extras = extras.getString("category_id");
        category_id = Integer.valueOf(id_from_extras);
        new ParseIYCSItem().execute(category_id);
    }
    
    protected void attachLinks() {
    	list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
    			Intent intent = new Intent(getBaseContext(), IsYourChildSickArticleActivity.class);
    			intent.putExtra("which", position);
    			intent.putExtra("category_id", category_id);
    			startActivity(intent);
			}
    	});
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
			attachLinks();
		}
    	
    }


}
