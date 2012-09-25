package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

import com.newpush.greenwoodpediatrics.Parser;

public class PracticeNewsActivity extends Activity {
	protected ArrayAdapter<String> newsAdapter;
	protected ListView newsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_news);

        newsList = (ListView) findViewById(R.id.practiceNewsListView);
        newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        newsList.setAdapter(newsAdapter);
        new ParseNews().execute();
    }

    protected void attachNewsItemLinks() {
    	newsList.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Intent newsItemIntent = new Intent(getBaseContext(), PracticeNewsItemActivity.class);
    			newsItemIntent.putExtra("which", position);
    			startActivity(newsItemIntent);
    		}
    	});
    }

    private class ParseNews extends AsyncTask<Void, Void, String[]> {
    	protected String[] doInBackground(Void... params) {
            Parser parser = new Parser(getApplicationContext().getFilesDir().getAbsolutePath() + "/news.xml");
            ArrayList<String> titles = parser.Parse("NewsTitle");
    		String[] result = new String[titles.size()];
			titles.toArray(result);
			return result;
    	}

    	protected void onPostExecute(String[] result) {
    		for (String s : result) {
    			newsAdapter.add(s);
    		}
    		attachNewsItemLinks();
    	}
    }
}
