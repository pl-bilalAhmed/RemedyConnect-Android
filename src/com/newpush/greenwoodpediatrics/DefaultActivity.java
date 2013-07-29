package com.newpush.greenwoodpediatrics;

import android.annotation.TargetApi;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.webkit.WebView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

//import android.content.Intent;
//import android.view.Window;

public class DefaultActivity extends SherlockActivity {
    protected Bundle extras;
    protected Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        res = getResources();
        extras = getIntent().getExtras();


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // In this app, we can just simple force the Up button to behave the same way as the Back.
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (titleview != null) {
            titleview.setText(title);
        }
    }

    public void suppressTitle() {
        TextView titleview = (TextView) findViewById(R.id.titleTextView);
        titleview.setVisibility(TextView.INVISIBLE);
        titleview.setHeight(0);
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

    public String getDataDirectory() {
        return this.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null && intentExtras.containsKey("isRoot") && !intentExtras.getBoolean("isRoot")) {
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }
}

