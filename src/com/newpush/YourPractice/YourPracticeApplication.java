package com.newpush.YourPractice;

import android.app.Application;
import com.testflightapp.lib.TestFlight;

public class YourPracticeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TestFlight.takeOff(this, "1e7151d2-5174-4e92-830f-56d6f3b8b9b3");
        TestFlight.passCheckpoint("Application launched");
    }
}