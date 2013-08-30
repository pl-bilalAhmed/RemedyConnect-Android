package com.newpush.mypractice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.newpush.mypractice.downloader.DownloadStatus;
import com.newpush.mypractice.parser.MainParser;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadActivity extends DefaultActivity implements OnClickListener {
    ProgressDialog progress;
    ExecutorService threadPoolExecutor;
    DownloadStatusSummary downloadSummary;

    static int SIZE_OF_THREAD_POOL = 6;
    static int DOWNLOAD_BUFFER_SIZE = 8192;
    static int CONNECTION_TIMEOUT = 5000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle(R.string.title_activity_download);

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.downloading));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        Button downloadstart = (Button) this.findViewById(R.id.downloadButton);
        downloadstart.setOnClickListener(this);

        downloadSummary = new DownloadStatusSummary();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.downloadButton) {
            this.startDownload();
        }
    }

    public void startDownload() {
        String feedRoot = extras.getString("feed");
        String designPack = extras.getString("designPack");
        // Store the selected feed endpoint's information.
        Data.SetFeedRoot(this, feedRoot);
        Data.SetDesignPack(this, designPack);

        // Start up the Executor which will handle the multiple threads
        threadPoolExecutor = Executors.newFixedThreadPool(SIZE_OF_THREAD_POOL);

        // Fire up the initial batch
        HashMap<String, String> whatToDownload = new HashMap<String, String>(2);
        whatToDownload.put("from", designPack);
        whatToDownload.put("to", "skin/DesignPack.zip");
        DownloadTask download = new DownloadTask();
        download.executeOnExecutor(threadPoolExecutor, whatToDownload);
        whatToDownload = new HashMap<String, String>(2);
        whatToDownload.put("from", feedRoot);
        whatToDownload.put("to", "index.xml");
        download = new DownloadTask();
        download.executeOnExecutor(threadPoolExecutor, whatToDownload);
    }

    // A class to store and pass the status of a download task around.
    private class DownloadTaskStatus {
        Integer statusCode;
        Integer downloadedSize;
        Integer expectedSize;
        HashMap<String, String> newDownload;

        public DownloadTaskStatus(Integer statusCode, Integer downloadedSize, Integer expectedSize) {
            this.statusCode = statusCode;
            this.downloadedSize = downloadedSize;
            this.expectedSize = expectedSize;
        }

        public DownloadTaskStatus(Integer statusCode) {
            this.statusCode = statusCode;
            this.downloadedSize = 0;
            this.expectedSize = 0;
        }

        public DownloadTaskStatus(Integer statusCode, HashMap<String, String> newDownload) {
            this.statusCode = statusCode;
            this.downloadedSize = 0;
            this.expectedSize = 0;
            this.newDownload = newDownload;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public Integer getDownloadedSize() {
            return downloadedSize;
        }

        public Integer getExpectedSize() {
            return expectedSize;
        }

        public HashMap<String, String> getNewDownload() {
            return newDownload;
        }

    }

    // @TODO check what's happening with finished files, failed files... something is still buggy here
    private class DownloadStatusSummary {
        ArrayList<DownloadTaskStatus> statuses;

        private DownloadStatusSummary() {
            statuses = new ArrayList<DownloadTaskStatus>(20);
        }

        public Integer reservePlace() {
            statuses.add(new DownloadTaskStatus(DownloadStatus.DOWNLOAD_FAILED));
            return statuses.size()-1;
        }

        public void updateStatus(Integer index, DownloadTaskStatus status) {
            statuses.set(index, status);
        }

        public boolean isCompleted() {
            int EXPECTED_NUMBER_OF_EXTRACTED_FILES = 1;
            int finished = 0;
            int extracted = 0;
            for (DownloadTaskStatus status : statuses) {
                switch (status.getStatusCode()) {
                    case DownloadStatus.DOWNLOAD_FINISHED:
                        ++finished;
                        break;
                    case DownloadStatus.EXTRACTING_FINISHED:
                        ++extracted;
                        break;
                }
            }
            return finished == statuses.size()-EXPECTED_NUMBER_OF_EXTRACTED_FILES
                    && extracted == EXPECTED_NUMBER_OF_EXTRACTED_FILES;
        }

        public Integer currentProgressPercentage() {
            Integer sumOfExpectedSize = 0;
            Integer sumOfDownloadedSize = 0;
            boolean hadFail = false;
            for (DownloadTaskStatus status : statuses) {
                if (status.getStatusCode() == DownloadStatus.EXTRACTING_FAILED ||
                        status.getStatusCode() == DownloadStatus.DOWNLOAD_FAILED) {
                    hadFail = true;
                }
                else {
                    sumOfDownloadedSize += status.getDownloadedSize();
                    sumOfExpectedSize += status.getExpectedSize();
                }
            }
            if (!hadFail) {
                Double percentage = sumOfDownloadedSize / (double)sumOfExpectedSize * 100;
                Log.d("MyPractice", percentage.toString());
                return percentage.intValue();
            }
            else {
                return -1;
            }
        }
    }

    // This AsyncTask is for downloading a single file.
    private class DownloadTask extends AsyncTask<HashMap<String, String>, DownloadTaskStatus, Void> {
        String dir;
        Integer taskStatusIndex;

        @Override
        protected Void doInBackground(HashMap<String, String>... whatToDownload) {
            taskStatusIndex = downloadSummary.reservePlace();
            if (isOnline()) {
                publishProgress(new DownloadTaskStatus(DownloadStatus.NETWORK_AVAILABLE));
                String from = whatToDownload[0].get("from");
                String to = whatToDownload[0].get("to");
                try {
                    dir = this.prepareDirectory();
                    publishProgress(new DownloadTaskStatus(DownloadStatus.SWITCH_TO_DETERMINATE));
                    URL url = new URL(from);
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(CONNECTION_TIMEOUT);
                    connection.connect();
                    int fileSize = 0;
                    int downloadedSize = 0;
                    fileSize = connection.getContentLength();
                    InputStream urlStream = new BufferedInputStream(url.openStream());
                    OutputStream fileOutputStream = new FileOutputStream(dir + to);
                    byte downloadBuffer[] = new byte[DOWNLOAD_BUFFER_SIZE];
                    while ((downloadedSize = urlStream.read(downloadBuffer)) != -1) {
                        publishProgress(new DownloadTaskStatus(DownloadStatus.UPDATE_PROGRESS,
                                downloadedSize, fileSize));
                        fileOutputStream.write(downloadBuffer, 0, downloadedSize);
                    }
                    urlStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    if (!to.equals("skin/DesignPack.zip")) {
                        // We have a feed, have to parse it to decide whether there's
                        // anything left to download...
                        MainParser parser = new MainParser(dir + to);
                        String filename;
                        if (parser.isMenu()) {
                            for (String subFeedURL : parser.getSubfeedURLs()) {
                                filename = MainParser.subFeedURLToLocal(
                                        subFeedURL, Data.GetFeedRoot(getApplicationContext()));
                                HashMap<String, String> additionalDownload = new HashMap<String, String>(2);
                                additionalDownload.put("from", subFeedURL);
                                additionalDownload.put("to", filename);
                                publishProgress(new DownloadTaskStatus(DownloadStatus.NEW_DOWNLOAD, additionalDownload));
                            }
                        }
                        publishProgress(new DownloadTaskStatus(DownloadStatus.DOWNLOAD_FINISHED, downloadedSize, fileSize));
                    }
                    else {
                        // We have a design pack, extract it
                        publishProgress(new DownloadTaskStatus(DownloadStatus.EXTRACTING));
                        try {
                            Skin.extractDesignPack(getApplicationContext());
                            publishProgress(new DownloadTaskStatus(DownloadStatus.EXTRACTING_FINISHED, downloadedSize, fileSize));
                        } catch (ZipException e) {
                            e.printStackTrace();
                            publishProgress(new DownloadTaskStatus(DownloadStatus.EXTRACTING_FAILED));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    publishProgress(new DownloadTaskStatus(DownloadStatus.DOWNLOAD_FAILED));
                }
            }
            else {
                publishProgress(new DownloadTaskStatus(DownloadStatus.DOWNLOAD_FAILED));
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            prepareDirectory();
            progress.show();
        }

        @Override
        protected void onProgressUpdate(DownloadTaskStatus... status) {
            super.onProgressUpdate(status);
            // Notify the summary about the update
            downloadSummary.updateStatus(taskStatusIndex, status[0]);
            switch (status[0].getStatusCode()) {
                case DownloadStatus.NETWORK_AVAILABLE:
                    progress.setIndeterminate(false);
                    break;
                case DownloadStatus.NEW_DOWNLOAD:
                    DownloadTask download = new DownloadTask();
                    download.executeOnExecutor(threadPoolExecutor, status[0].getNewDownload());
                    break;
                case DownloadStatus.UPDATE_PROGRESS:
                    Integer currentPercentage = downloadSummary.currentProgressPercentage();
                    progress.setProgress(currentPercentage);
                    progress.setMessage(getString(R.string.downloading));
                    break;
                case DownloadStatus.DOWNLOAD_FINISHED:
                case DownloadStatus.EXTRACTING_FINISHED:
                    if (downloadSummary.isCompleted()) {
                        threadPoolExecutor.shutdown();
                        progress.dismiss();
                        setResult(Activity.RESULT_OK);
                        progress.setMessage("Download finished.");
                        Intent intent = new Intent(DownloadActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case DownloadStatus.DOWNLOAD_FAILED:
                case DownloadStatus.EXTRACTING_FAILED:
                    threadPoolExecutor.shutdown();
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_FIRST_USER);
                    break;
            }
        }

        protected String prepareDirectory() {
            String directoryPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
            File directory = new File(directoryPath);
            directory.mkdir();
            return directoryPath;
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
    }
}
