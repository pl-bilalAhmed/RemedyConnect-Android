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
        setTitle(extras.getString("title"));
        String html = extras.getString("text");
        html = MarkupGenerator.preImage(this, html);
        html = MarkupGenerator.wrapHTMLWithStyle(html);
        display.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
    }

}
