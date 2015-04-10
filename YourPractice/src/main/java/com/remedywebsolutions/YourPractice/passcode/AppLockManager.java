package com.remedywebsolutions.YourPractice.passcode;

import android.app.Application;
import android.widget.Toast;

import org.wordpress.passcodelock.*;

public class AppLockManager {

    private static AppLockManager instance;
    private com.remedywebsolutions.YourPractice.passcode.AbstractAppLock currentAppLocker;

    
    public static AppLockManager getInstance() {
        if (instance == null) {
            instance = new AppLockManager();
        }
        return instance;
    }



    public void enableDefaultAppLockIfAvailable(Application currentApp) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            currentAppLocker = new DefaultAppLock(currentApp);
            currentAppLocker.enable();
        }
    }
    
    /**
     * Default App lock is available on Android-v14 or higher.
     * @return True if the Passcode Lock feature is available on the device
     */
    public boolean isAppLockFeatureEnabled(){
    	if( currentAppLocker == null )
    		return false;
    	if( currentAppLocker instanceof DefaultAppLock)
    		return (android.os.Build.VERSION.SDK_INT >= 14);
    	else 
    		return true;
    }


    /**
     * Default App lock is available on Android-v14 or higher.
     * @return True if the Passcode Lock feature is available on the device
     */
    public void performUnlockActivity(){

    }

    public void setCurrentAppLock(com.remedywebsolutions.YourPractice.passcode.AbstractAppLock newAppLocker) {
    	if( currentAppLocker != null ) {
    		currentAppLocker.disable(); //disable the old applocker if available
    	}
        currentAppLocker = newAppLocker;
    }
    
    public com.remedywebsolutions.YourPractice.passcode.AbstractAppLock getCurrentAppLock() {
        return currentAppLocker;
    }
    
    /*
     * Convenience method used to extend the default timeout.
     * 
     * There are situations where an activity will start a different application with an intent.  
     * In these situations call this method right before leaving the app.
     */
    public void setExtendedTimeout(){
        if ( currentAppLocker == null )
            return;
        currentAppLocker.setOneTimeTimeout(com.remedywebsolutions.YourPractice.passcode.AbstractAppLock.EXTENDED_TIMEOUT);
    }
}
