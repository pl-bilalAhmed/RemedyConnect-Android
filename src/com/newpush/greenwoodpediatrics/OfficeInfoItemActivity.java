package com.newpush.greenwoodpediatrics;

import java.util.Hashtable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import com.newpush.greenwoodpediatrics.parser.OfficeInfoParser;

public class OfficeInfoItemActivity extends DefaultActivity {
	WebView display;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_info_item);
		display = (WebView) findViewById(R.id.contentWebView);
		setWebViewTransparent(display);

		makeTextViewLinksClickable(R.id.footerTextView);
		suppressTitle();
		new ParseOfficeInfoItem().execute(extras.getInt("which"));
	}

	private class ParseOfficeInfoItem extends AsyncTask<Integer, Void, Hashtable<String, String>> {
		@Override
		protected Hashtable<String, String> doInBackground(Integer... params) {
			OfficeInfoParser parser = new OfficeInfoParser(getApplicationContext());
			Hashtable<String, String> result = parser.getFullInfo(params[0]);
			return result;
		}

		@Override
		protected void onPostExecute(Hashtable<String, String> result) {
			String title = result.get("title");
			String message = MarkupGenerator.formatTitle(title) + result.get("message");
			String text = MarkupGenerator.wrapHTMLWithStyle(message);
			setTitle(title);
			display.loadDataWithBaseURL("file:///android_asset/", text, "text/html", "utf-8", null);
			setWebViewTransparentAfterLoad(display);
		}

	}
}
