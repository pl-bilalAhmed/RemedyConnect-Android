package com.newpush.greenwoodpediatrics;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;

import com.newpush.greenwoodpediatrics.Parser;

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

	private class ParseNewsItem extends AsyncTask<Integer, Void, String[]> {
		protected String[] doInBackground(Integer... params) {
			String index = params[0].toString();
			Parser parser = new Parser(getApplicationContext().getFilesDir().getAbsolutePath() + "/news.xml");
			String resultTitle = parser.ParseSingle("CMS_News:eq(" + index + ") NewsTitle");
			String resultHTML = parser.ParseSingle("CMS_News:eq(" + index + ") NewsText");
			String[] result = {resultTitle, resultHTML};
			return result;
		}

		protected void onPostExecute(String[] result) {
			setTitle(result[0]);
			display.loadData(result[1], "text/html", "utf-8");
		}

	}

}
