package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;

import com.newpush.greenwoodpediatrics.parser.DefaultParser;

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

		private class ParseOfficeInfoItem extends AsyncTask<Integer, Void, String[]> {
			protected String[] doInBackground(Integer... params) {
				String index = params[0].toString();
				DefaultParser parser = new DefaultParser(getApplicationContext().getFilesDir().getAbsolutePath() + "/office.xml");
				String resultTitle = parser.ParseSingle("pw_Message:eq(" + index + ") messageTitle");
				String resultHTML = parser.ParseSingle("pw_Message:eq(" + index + ") StandardMessage");
				String[] result = {resultTitle, resultHTML};
				return result;
			}

			protected void onPostExecute(String[] result) {
				setTitle(result[0]);
				display.loadData(result[1], "text/html", "utf-8");
			}

		}
}
