package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;
import java.util.Hashtable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;

import com.newpush.greenwoodpediatrics.parser.OfficeInfoParser;

public class OfficeInfoItemActivity extends Activity {
	Bundle extras;
	WebView display;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_info_item);
		display = (WebView) findViewById(R.id.contentWebView);

		extras = getIntent().getExtras();
		new ParseOfficeInfoItem().execute(extras.getInt("which"));
	}

	private class ParseOfficeInfoItem extends AsyncTask<Integer, Void, Hashtable<String, String>> {
		protected Hashtable<String, String> doInBackground(Integer... params) {
			OfficeInfoParser parser = new OfficeInfoParser(getApplicationContext());
			Hashtable<String, String> result = parser.getFullInfo(params[0]);
			return result;
		}

		protected void onPostExecute(Hashtable<String, String> result) {
			String title = (String)result.get("title");
			String message = MarkupGenerator.formatTitle(title) + (String)result.get("message");
			setTitle(title);
			display.loadData(message, "text/html", "utf-8");
		}

	}
}
