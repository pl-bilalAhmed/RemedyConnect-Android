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
    int headerH = 0;
    int footerH = 0;
    View headerView;
    View footerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        display = (WebView) findViewById(R.id.pageWebView);
        setWebViewTransparent(display);

        LayoutInflater li=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = li.inflate(R.layout.default_header_listitem, null);
        headerView.setLayoutParams(new AbsoluteLayout.LayoutParams(
                AbsoluteLayout.LayoutParams.MATCH_PARENT,
                AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                0, 0));
        display.addView(headerView);

        footerView = li.inflate(R.layout.default_footer_listitem, null);
        display.addView(footerView);
        footerView.setVisibility(View.INVISIBLE);

        Skin.applyThemeLogo(this);
        makeTextViewLinksClickable(R.id.footerTextView);
        suppressTitle();
        setTitle(extras.getString("title"));
        WebSettings webSettings = display.getSettings();
        webSettings.setJavaScriptEnabled(true);
        display.setWebViewClient(new WebViewClient() {
            public void makeSpaceForAdditionalViews(WebView view) {
                view.loadUrl("javascript:document.getElementsByTagName('body')[0].style.marginTop='" + headerH + "px'");
                view.loadUrl("javascript:document.getElementsByTagName('body')[0].style.marginBottom='" + footerH + "px'");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                makeSpaceForAdditionalViews(view);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                makeSpaceForAdditionalViews(view);
            }
        });
        String html = MarkupGenerator.wrapHTMLWithStyle(extras.getString("text"));
        display.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
        setWebViewTransparentAfterLoad(display);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            headerH = headerView.getMeasuredHeight();
            footerH = footerView.getMeasuredHeight();
        }
    }
}
