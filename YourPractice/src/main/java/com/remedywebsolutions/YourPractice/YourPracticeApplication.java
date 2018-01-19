package com.remedywebsolutions.YourPractice;

import android.app.Application;

import com.jenzz.appstate.AppStateListener;
import com.jenzz.appstate.AppStateMonitor;
import com.pushio.manager.PushIOManager;
import com.remedywebsolutions.YourPractice.utility.Util;

public class YourPracticeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //    Log.i(MainActivity.TAG, "On Create of Application Called");
        // Instantiate the Push IO Manager:
        PushIOManager pushIOManager = PushIOManager.getInstance(this);
        assert pushIOManager != null;

        // Ensure that any registration changes with Google get reflected with Push IO.
        // Also registers for Broadcast Push Notifications (All Users).
        pushIOManager.ensureRegistration();

        // Switch stacking notifications off.
        pushIOManager.setNotificationsStacked(false);


        com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().enableDefaultAppLockIfAvailable(this);

        if (com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock() != null) {
            com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().forcePasswordLock();
        }
        //App Fresh start
        Data.setBackrgound(getApplicationContext(), false);
        Data.setBackgroundTime(getApplicationContext(), 0);

        AppStateMonitor appStateMonitor = AppStateMonitor.create(this);
        appStateMonitor.addListener(new AppStateListener() {
            @Override
            public void onAppDidEnterForeground() {
                //Set Time from here
                //   Util.onAppForeground(get);
                // Log.i(MainActivity.TAG, "App Foreground");
            }

            @Override
            public void onAppDidEnterBackground() {
                //   Log.i(MainActivity.TAG, "App going to background");
                Util.onAppBackground(getApplicationContext());
            }
        });
        appStateMonitor.start();

    }


}
