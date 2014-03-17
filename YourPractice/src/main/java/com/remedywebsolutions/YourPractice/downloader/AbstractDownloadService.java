package com.remedywebsolutions.YourPractice.downloader;

import android.app.IntentService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;

public abstract class AbstractDownloadService extends IntentService {
    public AbstractDownloadService() {
        super("AbstractDownloadService");
    }
    public AbstractDownloadService(String name) {
        super(name);
    }

    protected String prepareDirectory() {
        String directoryPath = this.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        File directory = new File(directoryPath);
        //noinspection ResultOfMethodCallIgnored
        directory.mkdir();
        return directoryPath;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
