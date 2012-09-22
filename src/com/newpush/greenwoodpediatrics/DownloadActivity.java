package com.newpush.greenwoodpediatrics;

import com.newpush.greenwoodpediatrics.DownloadService;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DownloadActivity extends Activity implements OnClickListener {
	ProgressDialog progress;
	
	public static final String LINK_OFFICE_MESSAGE = "http://greenwoodpedstest.pediatricweb.com/feed/1B8EC3EB-3101-4C5D-9746-7E877F4A5DF7/office";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.downloading));
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        
        Button downloadstart = (Button) this.findViewById(R.id.downloadButton);
        downloadstart.setOnClickListener(this);
    }

	public void onClick(View v) {
		this.startDownload();
	}
	
	public void startDownload() {
    	Intent intent = new Intent(this, DownloadService.class);
    	intent.putExtra("url", LINK_OFFICE_MESSAGE);
    	intent.putExtra("filename", "office.xml");
    	intent.putExtra("receiver", new DownloadReceiver(new Handler()));
		startService(intent);	
	}

    private class DownloadReceiver extends ResultReceiver{
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.NETWORK_AVAILABLE) {
            	progress.show();
            }
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int status = resultData.getInt("progress");
                progress.setProgress(status);
                if (status == 100) {
                    progress.dismiss();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
            if (resultCode == DownloadService.DOWNLOAD_FAILED) {
            	progress.dismiss();
            	Toast.makeText(getApplicationContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
            }
        }
    }
}
