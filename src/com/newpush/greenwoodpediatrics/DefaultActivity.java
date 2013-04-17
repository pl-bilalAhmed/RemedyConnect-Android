package com.newpush.greenwoodpediatrics;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

public class DefaultActivity extends SherlockActivity {
	protected Bundle extras;
	protected Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        res = getResources();

        extras = getIntent().getExtras();
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

	public void setWebViewTransparent(WebView webview) {
		webview.setBackgroundColor(Color.TRANSPARENT);
	}

	@TargetApi(11)
	public void setWebViewTransparentAfterLoad(WebView webview) {
		webview.setBackgroundColor(Color.TRANSPARENT);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			webview.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
	}
}
