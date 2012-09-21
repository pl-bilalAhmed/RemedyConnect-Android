package com.newpush.greenwood.mobile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.newpush.greenwood.mobile.DownloadActivity;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainActivity extends Activity {
	protected ArrayAdapter<String> menuadapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView menu = (ListView) findViewById(R.id.mainMenu);
        
    	menuadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
    	menu.setAdapter(menuadapter);
        
        Intent downloadActivity = new Intent(this, DownloadActivity.class);
        startActivityForResult(downloadActivity, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode == Activity.RESULT_OK) {
    		new ParseOffices().execute();
    	}
    }
    
    private class ParseOffices extends AsyncTask<Void, Void, String[]> {
    	protected String[] doInBackground(Void... params) {
    		ArrayList<String> titles = new ArrayList<String>();
    		String filename = getApplicationContext().getFilesDir().getAbsolutePath() + "/office.xml";
    		File officeXML = new File(filename);
			Document doc;
			try {
				doc = Jsoup.parse(officeXML, "UTF-8", "");
				Log.d("ParseOffices", "Parser opened");
				for (Element titleelement : doc.select("messagetitle")) {
					titles.add(titleelement.text());
				}
				Log.d("ParseOffices", "Parse complete");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		String[] result = new String[titles.size()];
			titles.toArray(result);
			return result;
    	}
    	
    	protected void onPostExecute(String[] result) {
    		Log.d("ParseOffices", "onPostExecute");
    		for (String s : result) {
    			menuadapter.add(s);
    		}
    	}
    }
}
