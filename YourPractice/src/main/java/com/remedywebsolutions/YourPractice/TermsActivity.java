package com.remedywebsolutions.YourPractice;

import android.os.Bundle;
import android.webkit.WebView;

public class TermsActivity extends DefaultActivity {
    protected WebView display;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Terms");
        setContentView(R.layout.activity_page);
        display = (WebView) findViewById(R.id.pageWebView);
        Skin.applyViewBackground(this, display);
        display.setBackgroundColor(0);
        //display.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        suppressTitle();
        setTitle(R.string.terms_and_conditions);
        display.loadUrl("file:///android_asset/terms_and_conditions.html");
    }
}
