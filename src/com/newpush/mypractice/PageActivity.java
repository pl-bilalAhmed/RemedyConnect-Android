package com.newpush.mypractice;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;

public class PageActivity extends DefaultActivity {
    protected WebView display;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        display = (WebView) findViewById(R.id.pageWebView);
        setWebViewTransparent(display);
        setTitle(extras.getString("title"));
        String html = extras.getString("text");
        html = MarkupGenerator.preImage(this, html);
        html = MarkupGenerator.wrapHTMLWithStyle(html);
        display.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
        setWebViewTransparentAfterLoad(display);
    }

}
