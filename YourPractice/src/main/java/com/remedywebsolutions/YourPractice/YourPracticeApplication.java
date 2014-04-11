package com.remedywebsolutions.YourPractice;

import android.app.Application;

import com.pushio.manager.PushIOManager;

import org.wordpress.passcodelock.AppLockManager;

public class YourPracticeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiate the Push IO Manager:
        PushIOManager pushIOManager = PushIOManager.getInstance(this);
        assert pushIOManager != null;

        // Ensure that any registration changes with Google get reflected with Push IO.
        // Also registers for Broadcast Push Notifications (All Users).
        pushIOManager.ensureRegistration();

        // Switch stacking notifications off.
        pushIOManager.setNotificationsStacked(false);

        AppLockManager.getInstance().enableDefaultAppLockIfAvailable(this);
    }
}
