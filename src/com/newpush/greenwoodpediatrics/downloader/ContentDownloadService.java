package com.newpush.greenwoodpediatrics.downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.newpush.greenwoodpediatrics.Data;
import com.newpush.greenwoodpediatrics.parser.MainParser;

public class ContentDownloadService extends AbstractDownloadService {

    public ContentDownloadService() {
        super("ContentDownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        Bundle resultData = new Bundle();
        if (!this.isOnline()) {
            resultData.putInt("progress", 0);
            receiver.send(DownloadStatus.DOWNLOAD_FAILED, resultData);
        } else {
            receiver.send(DownloadStatus.NETWORK_AVAILABLE, null);
            ArrayList<String> files = new ArrayList<String>();
            ArrayList<String> feeds = new ArrayList<String>();
            files.add("index.xml");
            feeds.add(Data.GetFeedRoot());
            try {
                String dir = this.prepareDirectory();
                int totalLength = 0;
                long total = 0;

                resultData.putInt("progress", 0);
                receiver.send(DownloadStatus.SWITCH_TO_DETERMINATE, resultData);

                while (!files.isEmpty()) {
                    URL url = new URL(feeds.get(0));
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    connection.setConnectTimeout(250);
                    totalLength += connection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(dir + files.get(0));
                    byte data[] = new byte[1024];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        resultData.putInt("progress", (int) (total * 100 / totalLength));
                        receiver.send(DownloadStatus.UPDATE_PROGRESS, resultData);
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();

                    MainParser parser = new MainParser(dir + files.get(0));
                    String filename;
                    if (parser.isMenu()) {
                        for (String subFeedURL : parser.getSubfeedURLs()) {
                            filename = MainParser.subFeedURLToLocal(subFeedURL);
                            files.add(filename);
                            feeds.add(subFeedURL);
                        }
                    }
                    files.remove(0);
                    feeds.remove(0);
                }
            } catch (IOException e) {
                resultData.putInt("progress", 0);
                receiver.send(DownloadStatus.DOWNLOAD_FAILED, resultData);
            }
        }

        resultData.putInt("progress", 100);
        receiver.send(DownloadStatus.UPDATE_PROGRESS, resultData);
    }

}
