package com.newpush.mypractice;

import android.app.Application;
import com.pushio.manager.PushIOManager;
import com.testflightapp.lib.TestFlight;

public class MyPracticeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TestFlight.takeOff(this, "7fb42f49-fa8f-4974-9920-d6e26b6dd8f7");
        TestFlight.passCheckpoint("Application launched");
    }
}