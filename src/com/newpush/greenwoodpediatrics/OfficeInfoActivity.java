package com.newpush.greenwoodpediatrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class OfficeInfoActivity extends Activity {
	protected ArrayAdapter<String> officeInfoAdapter;
	protected ListView officeInfoList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_info);
        
        officeInfoList = (ListView) findViewById(R.id.officeInfoListView);
        officeInfoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        officeInfoList.setAdapter(officeInfoAdapter);
        new ParseOffices().execute();
    }
   
    protected void attachInfoItemLinks() {
    	officeInfoList.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Intent infoItemIntent = new Intent(getBaseContext(), OfficeInfoItemActivity.class);
    			infoItemIntent.putExtra("which", position);
    			startActivity(infoItemIntent);
    		}
    	});
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
    			officeInfoAdapter.add(s);
    		}
    		attachInfoItemLinks();
    	}
    }
}
