package com.newpush.greenwoodpediatrics;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class DefaultActivity extends Activity {
	protected Bundle extras;
	protected Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        res = getResources();

        extras = getIntent().getExtras();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_update:
                startDownload();
                return true;
            case R.id.menu_item_about:
            	Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // This sets the title with the informations stored in the Bundle.
    // WARNING! Fire this only after the header has been inflated or you will
    // get a nice NullPointerException.
    public void setTitleFromIntentBundle() {
        if (extras != null && extras.containsKey("title")) {
        	String title = extras.getString("title");
        	if (title != null) {
        		setTitle(title);
        	}
        }
    }

    // Call this with the resource IDs of the TextViews to make links respond.
    public void makeTextViewLinksClickable(Integer... textViewResourceIds) {
    	for (Integer id : textViewResourceIds) {
		   TextView text = (TextView) findViewById(id);
		   text.setMovementMethod(LinkMovementMethod.getInstance());
    	}
    }

    @Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		TextView titleview = (TextView) findViewById(R.id.titleTextView);
		titleview.setText(title);
	}

    public void suppressTitle() {
    	TextView titleview = (TextView) findViewById(R.id.titleTextView);
    	titleview.setVisibility(TextView.INVISIBLE);
    	titleview.setHeight(0);
    }

	public void startDownload() {
    	Intent downloadActivity = new Intent(this, DownloadActivity.class);
        startActivityForResult(downloadActivity, 0);
    }
}
