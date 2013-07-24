package com.newpush.greenwoodpediatrics;

import com.newpush.greenwoodpediatrics.DownloadService;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DownloadActivity extends DefaultActivity implements OnClickListener {
	ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle(R.string.title_activity_download);

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.downloading));
        progress.setIndeterminate(true);
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
            	setResult(Activity.RESULT_FIRST_USER);
            }

            if (resultCode == DownloadService.SWITCH_TO_DETERMINATE) {
            	progress.setIndeterminate(false);
            }
        }
    }
}
