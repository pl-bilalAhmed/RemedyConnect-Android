package com.newpush.greenwoodpediatrics;

import java.io.File;

import android.content.Context;

public class DataChecker {
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
