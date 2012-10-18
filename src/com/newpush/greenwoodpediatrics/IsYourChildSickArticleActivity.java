package com.newpush.greenwoodpediatrics;

import java.util.Hashtable;

import com.newpush.greenwoodpediatrics.parser.IsYourChildSickSubParser;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

public class IsYourChildSickArticleActivity extends DefaultActivity {
	protected WebView display;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_your_child_sick_article);
        display = (WebView) findViewById(R.id.iycsArticleWebView);

        makeTextViewLinksClickable(R.id.footerTextView);
        suppressTitle();

		new ParseArticle().execute(extras.getInt("category_id"), extras.getInt("which"));
    }

    private class ParseArticle extends AsyncTask<Integer, Void, Hashtable<String, String>> {
		@Override
		protected Hashtable<String, String> doInBackground(Integer... params) {
			IsYourChildSickSubParser parser = new IsYourChildSickSubParser(getApplicationContext(), params[0]);
			Hashtable<String, String> result = parser.getFullInfo(params[1]);
			return result;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Hashtable<String, String> article) {
			String title = article.get("title");
			String offsitelink = article.get("offsitelink").trim();
			if (offsitelink != "") {
				display.loadUrl(offsitelink);
			}
			else {
				String contents = MarkupGenerator.formatIYCSArticle(article);
				setTitle(title);

				display.setWebChromeClient(new WebChromeClient());

				display.getSettings().setJavaScriptEnabled(true);
				if (Build.VERSION.SDK_INT >= 7) {
					display.getSettings().setAppCacheEnabled(true);
					display.getSettings().setDomStorageEnabled(true);
				}
				// how plugin is enabled change in API 8
				if (Build.VERSION.SDK_INT < 8) {
				  display.getSettings().setPluginsEnabled(true);
				} else {
				  display.getSettings().setPluginState(PluginState.ON);
				}
				display.getSettings().setUserAgentString("Android Mozilla/5.0 AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");

				display.loadData(contents, "text/html", "utf-8");
			}
		}
    }

}
