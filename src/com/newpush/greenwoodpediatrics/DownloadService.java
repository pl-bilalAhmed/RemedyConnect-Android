package com.newpush.greenwoodpediatrics;

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

import com.newpush.greenwoodpediatrics.parser.MainParser;

public class DownloadService extends IntentService {
	public static final int UPDATE_PROGRESS = 1;
	public static final int DOWNLOAD_FAILED = 2;
	public static final int NETWORK_AVAILABLE = 3;
	public static final int SWITCH_TO_DETERMINATE = 4;

	public DownloadService() {
		super("DownloadService");
	}
	
	protected String prepareDirectory() {
		String testdir = this.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
		File testfolder = new File(testdir);
		testfolder.mkdir();
		return testdir;
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

	@Override
	protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        Bundle resultData = new Bundle();
        if (!this.isOnline()) {
            resultData.putInt("progress", 0);
            receiver.send(DOWNLOAD_FAILED, resultData);
        }
        else {
        	receiver.send(NETWORK_AVAILABLE, null);
        	ArrayList<String> files = new ArrayList<String>();
        	ArrayList<String> feeds = new ArrayList<String>();
        	files.add("index.xml");
        	feeds.add(Data.FEED_ROOT);
            try {
            	String dir = this.prepareDirectory();
            	int totalLength = 0;
            	long total = 0;            	
            	
            	resultData.putInt("progress", 0);
            	receiver.send(SWITCH_TO_DETERMINATE, resultData);
            	
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
                        resultData.putInt("progress" ,(int) (total * 100 / totalLength));
                        receiver.send(UPDATE_PROGRESS, resultData);
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();                    
                    
                    MainParser parser = new MainParser(dir + files.get(0));
                    String filename;
                    if (parser.isMenu()) {
	                    for (String subFeedURL : parser.getSubfeedURLs()) {
	                    	filename = subFeedURL.replace(Data.FEED_ROOT, "");
	                    	files.add(filename);
	                    	feeds.add(subFeedURL);
	                    }
                    }
                    Log.i("testing", files.get(0));
                    files.remove(0);
                    feeds.remove(0);
            	}
            } catch (IOException e) {
                resultData.putInt("progress", 0);
                receiver.send(DOWNLOAD_FAILED, resultData);
            }
        }

        resultData.putInt("progress" ,100);
        receiver.send(UPDATE_PROGRESS, resultData);
	}

}
