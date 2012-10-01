package com.newpush.greenwoodpediatrics;

import java.util.Hashtable;

import com.newpush.greenwoodpediatrics.parser.IsYourChildSickSubParser;

import android.os.AsyncTask;
import android.os.Bundle;
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
				display.loadData(contents, "text/html", "utf-8");
			}
		}
    }

}
