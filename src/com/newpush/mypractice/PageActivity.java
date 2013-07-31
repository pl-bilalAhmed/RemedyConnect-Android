package com.newpush.mypractice;

import android.os.Bundle;
import android.webkit.WebView;

public class PageActivity extends DefaultActivity {
    protected WebView display;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        display = (WebView) findViewById(R.id.pageWebView);
        setWebViewTransparent(display);

        makeTextViewLinksClickable(R.id.footerTextView);
        suppressTitle();

        setTitle(extras.getString("title"));
        String html = MarkupGenerator.wrapHTMLWithStyle(extras.getString("text"));
        display.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
        setWebViewTransparentAfterLoad(display);
    }
}
