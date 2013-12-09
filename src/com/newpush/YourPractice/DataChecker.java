package com.newpush.YourPractice;

import android.content.Context;

import java.io.File;

public class DataChecker {
    // TODO Move this into Data
    public static boolean isDataAvailable(Context applicationContext) {
        // TODO Make this faster by checking a special file created on download.
        boolean data_available = true;
        File file = applicationContext.getFileStreamPath("index.xml");
        if (!file.exists()) {
            data_available = false;
        }

        return data_available;
    }
}
