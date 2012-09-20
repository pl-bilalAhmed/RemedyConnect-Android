package com.newpush.greenwood.mobile;

import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        new ParseBasicHTMLTask().execute("http://greenwoodpedstest.pediatricweb.com/feed/1B8EC3EB-3101-4C5D-9746-7E877F4A5DF7/location");
        return true;
    }
    
    private class ParseBasicHTMLTask extends AsyncTask<String, Void, String> {
    	protected String doInBackground(String... urls) {
    		String lead = null;
    		try {
        		Document doc = Jsoup.connect(urls[0])
            			.userAgent("Mozilla")
            			.timeout(3000)
            			.get();
        		lead = doc.select("pw_Office_Location").first().text();
        		
        	}
        	catch (IOException e) {
        		
        	}	
			return lead;
    	}
    	
    	protected void onPostExecute(String result) {
    		TextView testtext = (TextView) findViewById(R.id.TestText);
    		testtext.setText(result);
    	}
    }
}
