package com.remedywebsolutions.YourPractice.downloader;

import android.app.IntentService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class AbstractDownloadService extends IntentService {
    public AbstractDownloadService() {
        super("AbstractDownloadService");
    }
    public AbstractDownloadService(String name) {
        super(name);
    }

    @Nullable
    protected String prepareDirectory() {
        Context context = this.getApplicationContext();
        if (context != null) {
            File filesDir = context.getFilesDir();
            if (filesDir != null) {
                String directoryPath = filesDir.getAbsolutePath() + "/";
                File directory = new File(directoryPath);
                //noinspection ResultOfMethodCallIgnored
                directory.mkdir();
                return directoryPath;
            }
            throw new IllegalStateException("Files dir shouldn't be null");
        }
        throw new IllegalStateException("Application context shouldn't be null");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
