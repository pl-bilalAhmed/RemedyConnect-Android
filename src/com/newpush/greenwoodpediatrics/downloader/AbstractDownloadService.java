package com.newpush.greenwoodpediatrics.downloader;

import android.app.IntentService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;

public abstract class AbstractDownloadService extends IntentService {
    public AbstractDownloadService() {
        super("AbstractDownloadService");
        // Of course, you should never see this in a log...
    }
    public AbstractDownloadService(String name) {
        super(name);
    }

    protected String prepareDirectory() {
        String directoryPath = this.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        File directory = new File(directoryPath);
        directory.mkdir();
        return directoryPath;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}
