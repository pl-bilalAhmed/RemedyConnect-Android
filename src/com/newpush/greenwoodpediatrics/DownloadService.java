package com.newpush.greenwoodpediatrics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.ResultReceiver;

public class DownloadService extends IntentService {
	public static final int UPDATE_PROGRESS = 1;
	public static final int DOWNLOAD_FAILED = 2;
	public static final int NETWORK_AVAILABLE = 3;
	
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
        if (!this.isOnline()) {
    		Bundle resultData = new Bundle();
            resultData.putInt("progress", 0);
            receiver.send(DOWNLOAD_FAILED, resultData);	
        }
        else {
        	receiver.send(NETWORK_AVAILABLE, null);
            try {
            	String dir = this.prepareDirectory();
            	int totalLength = 0;
            	
            	for (Map.Entry<String, String> fileData : Data.dataFiles.entrySet()) {
            		URL url = new URL(fileData.getValue());
            		URLConnection connection = url.openConnection();
            		connection.connect();
            		totalLength += connection.getContentLength();
            	}
            	
            	long total = 0;
            	for (Map.Entry<String, String> fileData : Data.dataFiles.entrySet()) {
            		URL url = new URL(fileData.getValue());
            		URLConnection connection = url.openConnection();
            		connection.connect();
                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(dir + fileData.getKey());
                    byte data[] = new byte[1024];                    
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        Bundle resultData = new Bundle();
                        resultData.putInt("progress" ,(int) (total * 100 / totalLength));
                        receiver.send(UPDATE_PROGRESS, resultData);
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();            		
            	}


            } catch (IOException e) {
                Bundle resultData = new Bundle();
                resultData.putInt("progress", 0);
                receiver.send(DOWNLOAD_FAILED, resultData);
            }        	
        }
        
        Bundle resultData = new Bundle();
        resultData.putInt("progress" ,100);
        receiver.send(UPDATE_PROGRESS, resultData);
	}

}
