package com.newpush.mypractice;

import android.os.Bundle;
import android.webkit.WebView;

public class TermsActivity extends DefaultActivity {
    protected WebView display;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        display = (WebView) findViewById(R.id.pageWebView);
        suppressTitle();
        setTitle(R.string.terms_and_conditions);
        display.loadUrl("file:///android_asset/terms_and_conditions.html");
    }
}