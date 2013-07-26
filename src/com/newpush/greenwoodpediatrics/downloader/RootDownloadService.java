package com.newpush.greenwoodpediatrics.downloader;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.newpush.greenwoodpediatrics.Data;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class RootDownloadService extends AbstractDownloadService {
    public RootDownloadService() {
        super("RootDownloadService");
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
            files.add("root.xml");

            if (intent.getStringExtra("practiceName") != null) {
                feeds.add(Data.SearchRootForPracticeName(intent.getStringExtra("practiceName")));
            }
            else if (intent.getParcelableExtra("location") != null) {
                feeds.add(Data.SearchRootForPracticeLocation((Location) intent.getParcelableExtra("location")));
            }
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

                    files.remove(0);
                    feeds.remove(0);
                }
                resultData.putInt("progress", 100);
                receiver.send(DownloadStatus.DOWNLOAD_FINISHED, resultData);
            } catch (IOException e) {
                resultData.putInt("progress", 0);
                receiver.send(DownloadStatus.DOWNLOAD_FAILED, resultData);
            }
        }
        this.stopSelf();
    }
}
