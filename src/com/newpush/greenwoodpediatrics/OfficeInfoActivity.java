package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.newpush.greenwoodpediatrics.parser.OfficeInfoParser;

public class OfficeInfoActivity extends DefaultActivity {
	protected ArrayAdapter<String> officeInfoAdapter;
	protected ListView officeInfoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_info);

        officeInfoList = (ListView) findViewById(R.id.officeInfoListView);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.default_header, officeInfoList, false);
        ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.default_footer, officeInfoList, false);
        officeInfoList.addHeaderView(header, null, false);
        officeInfoList.addFooterView(footer, null, false);

        officeInfoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        officeInfoList.setAdapter(officeInfoAdapter);

        setTitleFromIntentBundle();
        makeTextViewLinksClickable(R.id.footerTextView);

        new ParseOffices().execute();
    }

    protected void attachInfoItemLinks() {
    	officeInfoList.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    			Intent infoItemIntent = new Intent(getBaseContext(), OfficeInfoItemActivity.class);
    			infoItemIntent.putExtra("which", position-1); // -1 because of the header
    			startActivity(infoItemIntent);
    		}
    	});
    }

    private class ParseOffices extends AsyncTask<Void, Void, ArrayList<String>> {
    	@Override
		protected ArrayList<String> doInBackground(Void... params) {
            OfficeInfoParser parser = new OfficeInfoParser(getApplicationContext());
            ArrayList<String> titles = parser.getTitles();
			return titles;
    	}

    	@Override
		protected void onPostExecute(ArrayList<String> result) {
    		for (String s : result) {
    			officeInfoAdapter.add(s);
    		}
    		attachInfoItemLinks();
    	}
    }
}
