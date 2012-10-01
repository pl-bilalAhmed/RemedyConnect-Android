package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import com.newpush.greenwoodpediatrics.parser.IsYourChildSickSubParser;

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

public class IsYourChildSickItemActivity extends DefaultActivity {
	protected ListView list;
	protected ArrayAdapter<String> adapter;
	protected Integer category_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_your_child_sick_item);
        list = (ListView) findViewById(R.id.iycsItemListView);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.default_header, list, false);
        ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.default_footer, list, false);
        list.addHeaderView(header, null, false);
        list.addFooterView(footer, null, false);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(adapter);
        String id_from_extras = extras.getString("category_id");
        category_id = Integer.valueOf(id_from_extras);

        if (extras.containsKey("title")) {
        	setTitleFromIntentBundle();
        }
        else {
        	setTitle(extras.getString("category_name"));
        }

        makeTextViewLinksClickable(R.id.footerTextView);

        new ParseIYCSItem().execute(category_id);
    }

    protected void attachLinks() {
    	list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
    			Intent intent = new Intent(getBaseContext(), IsYourChildSickArticleActivity.class);
    			intent.putExtra("which", position-1); // -1 because of the header element.
    			intent.putExtra("category_id", category_id);
    			startActivity(intent);
			}
    	});
    }

    private class ParseIYCSItem extends AsyncTask<Integer, Void, ArrayList<String>> {
		@Override
		protected ArrayList<String> doInBackground(Integer... params) {
			IsYourChildSickSubParser parser = new IsYourChildSickSubParser(getApplicationContext(), params[0]);
			return parser.getArticleTitles();
		}

		@Override
		protected void onPostExecute(ArrayList<String> titles) {
			for (String title : titles) {
				adapter.add(title);
			}
			attachLinks();
		}

    }


}
