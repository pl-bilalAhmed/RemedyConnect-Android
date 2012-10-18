package com.newpush.greenwoodpediatrics;

import java.util.Hashtable;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import com.newpush.greenwoodpediatrics.parser.PracticeNewsParser;

public class PracticeNewsItemActivity extends DefaultActivity {
	WebView display;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice_news_item);
		display = (WebView) findViewById(R.id.newsWebView);
		setWebViewTransparent(display);

		suppressTitle();

		new ParseNewsItem().execute(extras.getInt("which"));
	}

	private class ParseNewsItem extends AsyncTask<Integer, Void, Hashtable<String, String>> {
		@Override
		protected Hashtable<String, String> doInBackground(Integer... params) {
			PracticeNewsParser parser = new PracticeNewsParser(getApplicationContext());
			Hashtable<String, String> result = parser.getFullInfo(params[0], getApplicationContext());
			return result;
		}

		@TargetApi(11)
		@Override
		protected void onPostExecute(Hashtable<String, String> result) {
			String title = result.get("title");
			String releasedate = result.get("releasedate");
			String text = MarkupGenerator.formatTitle(title) +
					releasedate +
					result.get("text");
			setTitle(title);
			display.loadData(text, "text/html", "utf-8");
			setWebViewTransparentAfterLoad(display);
		}

	}

}
