package com.newpush.greenwoodpediatrics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.newpush.greenwoodpediatrics.downloader.ContentDownloadService;
import com.newpush.greenwoodpediatrics.downloader.DownloadStatus;

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
        if (v.getId() == R.id.downloadButton) {
            this.startDownload();
        }
    }

    public void startDownload() {
        Intent intent = new Intent(this, ContentDownloadService.class);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        intent.putExtra("feed", extras.getString("feed"));
        startService(intent);
    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadStatus.NETWORK_AVAILABLE) {
                progress.show();
            }
            if (resultCode == DownloadStatus.UPDATE_PROGRESS) {
                int status = resultData.getInt("progress");
                progress.setProgress(status);
                if (status == 100) {
                    progress.dismiss();
                    setResult(Activity.RESULT_OK);
                    Intent intent = new Intent(DownloadActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            if (resultCode == DownloadStatus.DOWNLOAD_FAILED) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_FIRST_USER);
            }

            if (resultCode == DownloadStatus.SWITCH_TO_DETERMINATE) {
                progress.setIndeterminate(false);
            }
        }
    }
}
