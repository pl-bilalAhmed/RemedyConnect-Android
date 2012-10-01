package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.newpush.greenwoodpediatrics.parser.IsYourChildSickParser;

public class IsYourChildSickActivity extends DefaultActivity {
	protected ArrayAdapter<String> adapter;
	protected ListView list;
	protected ArrayList<String> category_position_to_ids;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isyourchildsick);

        list = (ListView) findViewById(R.id.iycsListView);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.default_header, list, false);
        list.addHeaderView(header, null, false);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        category_position_to_ids = new ArrayList<String>();
        list.setAdapter(adapter);

        setTitleFromIntentBundle();

        new ParseIYCSCategories().execute();
    }

    protected void attachLinks() {
    	list.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Intent intent = new Intent(getBaseContext(), IsYourChildSickItemActivity.class);
    			// position-1 because of the header element
    			intent.putExtra("category_id", category_position_to_ids.get(position-1));
    			intent.putExtra("category_name", adapter.getItem(position-1));
    			startActivity(intent);
    		}
    	});
    }

    private class ParseIYCSCategories extends AsyncTask<Void, Void, LinkedHashMap<String, String>> {
    	@Override
		protected LinkedHashMap<String, String> doInBackground(Void... params) {
    		IsYourChildSickParser parser = new IsYourChildSickParser(getApplicationContext());
    		LinkedHashMap<String, String> categories = parser.getCategories();
    		return categories;
    	}

    	@Override
		protected void onPostExecute(LinkedHashMap<String, String> categories) {
    		for (Map.Entry<String, String> category : categories.entrySet()) {
    			adapter.add(category.getValue());
    			// Let's store the id too so we have the mapping from positions to id
    			category_position_to_ids.add(category.getKey());
    		}
    		attachLinks();
    	}

    }

}
