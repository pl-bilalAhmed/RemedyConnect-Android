package com.newpush.greenwoodpediatrics;

import android.os.Bundle;
import android.webkit.WebView;

public class TermsActivity extends DefaultActivity {
    protected WebView display;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        display = (WebView) findViewById(R.id.pageWebView);
        setWebViewTransparent(display);

        makeTextViewLinksClickable(R.id.footerTextView);
        suppressTitle();

        setTitle(R.string.terms_and_conditions);

        display.loadUrl("file:///android_asset/terms_and_conditions.html");
        setWebViewTransparentAfterLoad(display);
    }
}