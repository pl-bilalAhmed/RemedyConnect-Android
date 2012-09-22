package com.newpush.greenwoodpediatrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

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

    private class ParseNews extends AsyncTask<Void, Void, String[]> {
    	protected String[] doInBackground(Void... params) {
    		ArrayList<String> titles = new ArrayList<String>();
    		String filename = getApplicationContext().getFilesDir().getAbsolutePath() + "/news.xml";
    		File newsXML = new File(filename);
			Document doc;
			try {
				doc = Jsoup.parse(newsXML, "UTF-8", "");
				for (Element titleelement : doc.select("NewsTitle")) {
					titles.add(titleelement.text());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		String[] result = new String[titles.size()];
			titles.toArray(result);
			return result;
    	}
    	
    	protected void onPostExecute(String[] result) {
    		for (String s : result) {
    			newsAdapter.add(s);
    		}
    		//attachInfoItemLinks();
    	} 	
    }
}
