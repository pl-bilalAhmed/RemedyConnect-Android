package com.remedywebsolutions.YourPractice.utility;

import android.content.Context;
import android.util.Log;

import com.remedywebsolutions.YourPractice.Data;
import com.remedywebsolutions.YourPractice.MainActivity;

/**
 * Created by ladmin on 12/19/17.
 */

public class Util {

    public static final int PAGE_REFRESH_THRESHOLD = 120000;// 2Minutes;120000

    public static void onAppForeground(Context context) {
        Data.setBackgroundTime(context, 0);
        Data.setBackrgound(context, false);
    }


    public static void onAppBackground(Context context) {
        Data.setBackgroundTime(context, System.currentTimeMillis());
        Data.setBackrgound(context, true);
    }

    /*
    Checks if time exceeded the threshold
     */
    public static boolean shouldUpdateData(Context context) {
        long currentTime = System.currentTimeMillis();
        long backgroundTime = Data.getBackgroundTime(context);
        Log.i(MainActivity.TAG, "Current time = " + currentTime + " Foreground time =" + backgroundTime);
        if (backgroundTime > 0 && (currentTime - backgroundTime) > PAGE_REFRESH_THRESHOLD) {
            return true;
        }
        return false;
    }


}
