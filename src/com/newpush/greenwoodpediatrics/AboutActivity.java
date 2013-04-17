package com.newpush.greenwoodpediatrics;

import android.os.Bundle;

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

}
