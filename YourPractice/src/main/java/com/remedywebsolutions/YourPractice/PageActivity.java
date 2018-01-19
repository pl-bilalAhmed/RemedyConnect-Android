package com.remedywebsolutions.YourPractice;

import android.os.Bundle;
import android.webkit.WebView;

public class PageActivity extends DefaultActivity {
    protected WebView display;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Page");
        showLog("Page Opened With Web");
        setContentView(R.layout.activity_page);
        display = (WebView) findViewById(R.id.pageWebView);
        Skin.applyViewBackground(this, display);
        display.setBackgroundColor(0);
        //display.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        setTitle(extras.getString("title"));
        String html = extras.getString("text");
        html = MarkupGenerator.preImage(this, html);
        html = MarkupGenerator.wrapHTMLWithStyle(html);
        display.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
    }




}
