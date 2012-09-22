package com.newpush.greenwoodpediatrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.support.v4.app.NavUtils;

public class OfficeInfoItemActivity extends Activity {
	Bundle extras;
	WebView display;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_info_item);
        display = (WebView) findViewById(R.id.contentWebView);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        
        extras = getIntent().getExtras();
        new ParseOfficeInfoItem().execute(extras.getInt("which"));
    }

    private class ParseOfficeInfoItem extends AsyncTask<Integer, Void, String[]> {
		protected String[] doInBackground(Integer... params) {
    		String resultTitle = "";
    		String resultHTML = "";
    		String filename = getApplicationContext().getFilesDir().getAbsolutePath() + "/office.xml";
    		File officeXML = new File(filename);
			Document doc;
			try {
				doc = Jsoup.parse(officeXML, "UTF-8", "");
				Element infoElement = doc.select("pw_Message:eq(" + params[0].toString() + ")").first();
				resultTitle = infoElement.select("messageTitle").first().text();
				Elements resultHTMLElements = infoElement.select("StandardMessage");
				if (!resultHTMLElements.isEmpty()) {
					resultHTML = resultHTMLElements.first().text();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] result = {resultTitle, resultHTML};
			return result;
		}
		
    	protected void onPostExecute(String[] result) {
    		setTitle(result[0]);
    		display.loadData(result[1], "text/html", "utf-8");
    	}
    	
    }
}
