package com.newpush.greenwoodpediatrics;

import android.os.Bundle;
import android.view.Menu;

public class AboutActivity extends DefaultActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        makeTextViewLinksClickable(
        		R.id.footerTextView,
        		R.id.about_developers,
        		R.id.about_jsoup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
