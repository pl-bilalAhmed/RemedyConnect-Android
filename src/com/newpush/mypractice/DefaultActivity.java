package com.newpush.mypractice;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import java.lang.reflect.Field;


//import android.content.Intent;
//import android.view.Window;

public class DefaultActivity extends SherlockActivity {
    Menu abMenu;
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

    public void setHomeVisibility(boolean visible) {
        MenuItem item = abMenu.findItem(R.id.home);
        item.setVisible(visible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        com.actionbarsherlock.view.MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        abMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                // In this app, we can just simple force the Up button to behave the same way as the Back.
                this.onBackPressed();
                return true;
            case R.id.home:
                MainViewController.FireRootActivity(this);
                return true;
            case R.id.menu_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_terms_and_conditions:
                intent = new Intent(this, TermsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_update:
                String feedRoot = Data.GetFeedRoot(this);
                String designPack = Data.GetDesignPack(this);
                if (!feedRoot.isEmpty() && !designPack.isEmpty()) {
                    intent = new Intent(this, DownloadActivity.class);
                    intent.putExtra("feed", feedRoot);
                    intent.putExtra("designPack", designPack);
                    startActivity(intent);
                }
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
            if (text != null) {
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }
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
        if (titleview != null) {
            titleview.setVisibility(TextView.INVISIBLE);
            titleview.setHeight(0);
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

