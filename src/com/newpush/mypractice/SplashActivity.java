package com.newpush.mypractice;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;

public class SplashActivity extends SherlockActivity {
    static final int SPLASH_SLEEP = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        Skin.prepareSkinDirectory(this); // This will resolve first start issues.
        Skin.applyThemeSplash(this);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_SLEEP);
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        splashThread.start();
    }
}
