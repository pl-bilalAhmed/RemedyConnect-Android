package com.remedywebsolutions.YourPractice;

import android.app.Application;
import com.pushio.manager.PushIOManager;

public class YourPracticeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiate the Push IO Manager:
        PushIOManager pushIOManager = PushIOManager.getInstance(this);

        // Ensure that any registration changes with Google get reflected with Push IO.
        // Also registers for Broadcast Push Notifications (All Users).
        pushIOManager.ensureRegistration();
    }
}
