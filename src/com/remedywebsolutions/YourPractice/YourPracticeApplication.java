package com.remedywebsolutions.YourPractice;

import android.app.Application;
import com.pushio.manager.PushIOManager;
import com.testflightapp.lib.TestFlight;

public class YourPracticeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiate the Push IO Manager:
        PushIOManager pushIOManager = PushIOManager.getInstance(this);

        // Ensure that any registration changes with Google get reflected with Push IO.
        // Also registers for Broadcast Push Notifications (All Users).
        pushIOManager.ensureRegistration();

        TestFlight.takeOff(this, "1e7151d2-5174-4e92-830f-56d6f3b8b9b3");
        TestFlight.passCheckpoint("Application launched");
    }
}
