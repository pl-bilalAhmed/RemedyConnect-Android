package com.newpush.greenwoodpediatrics;

import java.util.Hashtable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;

import com.newpush.greenwoodpediatrics.parser.PracticeNewsParser;

public class PracticeNewsItemActivity extends Activity {
	Bundle extras;
	WebView display;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_news_item);
		display = (WebView) findViewById(R.id.newsWebView);

		extras = getIntent().getExtras();
		new ParseNewsItem().execute(extras.getInt("which"));
	}

	private class ParseNewsItem extends AsyncTask<Integer, Void, Hashtable<String, String>> {
		protected Hashtable<String, String> doInBackground(Integer... params) {
			PracticeNewsParser parser = new PracticeNewsParser(getApplicationContext());
			Hashtable<String, String> result = parser.getFullInfo(params[0], getApplicationContext());
			return result;
		}

		protected void onPostExecute(Hashtable<String, String> result) {
			String title = (String)result.get("title");
			String releasedate = (String)result.get("releasedate");
			String text = MarkupGenerator.formatTitle(title) +
					releasedate +
					(String)result.get("text");
			setTitle(title);
			display.loadData(text, "text/html", "utf-8");
		}

	}

}
