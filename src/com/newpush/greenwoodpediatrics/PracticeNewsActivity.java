package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.newpush.greenwoodpediatrics.parser.PracticeNewsParser;

public class PracticeNewsActivity extends DefaultActivity {
	protected ArrayAdapter<String> newsAdapter;
	protected ListView newsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_news);

        newsList = (ListView) findViewById(R.id.practiceNewsListView);
        newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.default_header, newsList, false);
        ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.default_footer, newsList, false);
        newsList.addHeaderView(header, null, false);
        newsList.addFooterView(footer, null, false);

        setTitleFromIntentBundle();
        makeTextViewLinksClickable(R.id.footerTextView);

        newsList.setAdapter(newsAdapter);
        new ParseNews().execute();
    }

    protected void attachNewsItemLinks() {
    	newsList.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Intent newsItemIntent = new Intent(getBaseContext(), PracticeNewsItemActivity.class);
    			newsItemIntent.putExtra("which", position-1); // -1 because of the header
    			startActivity(newsItemIntent);
    		}
    	});
    }

    private class ParseNews extends AsyncTask<Void, Void, ArrayList<String>> {
    	@Override
		protected ArrayList<String> doInBackground(Void... params) {
            PracticeNewsParser parser = new PracticeNewsParser(getApplicationContext());
            ArrayList<String> titles = parser.getTitles();
    		return titles;
    	}

    	@Override
		protected void onPostExecute(ArrayList<String> result) {
    		for (String s : result) {
    			newsAdapter.add(s);
    		}
    		attachNewsItemLinks();
    	}
    }
}
