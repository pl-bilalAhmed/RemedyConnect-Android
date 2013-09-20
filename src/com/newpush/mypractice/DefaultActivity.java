package com.newpush.mypractice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.newpush.mypractice.downloader.DownloadStatusCodes;
import com.newpush.mypractice.downloader.DownloadTaskStatus;
import com.newpush.mypractice.downloader.DownloadTaskStatusSummary;
import com.newpush.mypractice.parser.MainParser;
import com.pushio.manager.PushIOManager;
import com.pushio.manager.tasks.PushIOListener;
import com.testflightapp.lib.TestFlight;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class DefaultActivity extends SherlockActivity {
    Menu abMenu;
    protected Bundle extras;
    protected Resources res;
    ProgressDialog progress;
    ExecutorService threadPoolExecutor;
    DownloadTaskStatusSummary downloadSummary;

    static int SIZE_OF_THREAD_POOL = 6;
    static int DOWNLOAD_BUFFER_SIZE = 8192;
    static int CONNECTION_TIMEOUT = 5000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        res = getResources();
        extras = getIntent().getExtras();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //int colors[] = { 0xff255779 , 0xff3e7492, 0xffa6c0cd };
        //actionBar.setBackgroundDrawable(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors));

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.downloading));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        downloadSummary = new DownloadTaskStatusSummary();
    }

    public void setHomeVisibility(boolean visible) {
        MenuItem item = abMenu.findItem(R.id.home);
        item.setVisible(visible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        com.actionbarsherlock.view.MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        abMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                // In this app, we can just simple force the Up button to behave the same way as the Back.
                this.onBackPressed();
                return true;
            case R.id.home:
                MainViewController.FireRootActivity(this);
                return true;
            case R.id.menu_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_terms_and_conditions:
                intent = new Intent(this, TermsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_update:
                String feedRoot = Data.GetFeedRoot(this);
                String designPack = Data.GetDesignPack(this);
                startDownload(feedRoot, designPack);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // This sets the title with the informations stored in the Bundle.
    // WARNING! Fire this only after the header has been inflated or you will
    // get a nice NullPointerException.
    public void setTitleFromIntentBundle() {
        if (extras != null && extras.containsKey("title")) {
            String title = extras.getString("title");
            if (title != null) {
                setTitle(title);
            }
        }
    }

    // Call this with the resource IDs of the TextViews to make links respond.
    public void makeTextViewLinksClickable(Integer... textViewResourceIds) {
        for (Integer id : textViewResourceIds) {
            TextView text = (TextView) findViewById(id);
            if (text != null) {
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        TextView titleview = (TextView) findViewById(R.id.titleTextView);
        if (titleview != null) {
            titleview.setText(title);
        }
    }

    public void suppressTitle() {
        TextView titleview = (TextView) findViewById(R.id.titleTextView);
        if (titleview != null) {
            titleview.setVisibility(TextView.INVISIBLE);
            titleview.setHeight(0);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null && intentExtras.containsKey("isRoot") && !intentExtras.getBoolean("isRoot")) {
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    @SuppressWarnings("unchecked")
    public void startDownload(String feedRoot, String designPack) {
        // Store the selected feed endpoint's information.
        Data.SetFeedRoot(this, feedRoot);
        Data.SetDesignPack(this, designPack);

        // Start up the Executor which will handle the multiple threads
        threadPoolExecutor = Executors.newFixedThreadPool(SIZE_OF_THREAD_POOL);

        try {
            // Fire up the initial batch
            HashMap<String, String> whatToDownload = new HashMap<String, String>(2);
            whatToDownload.put("from", designPack);
            whatToDownload.put("to", "skin/DesignPack.zip");
            DownloadTask download = new DownloadTask();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                download.executeOnExecutor(threadPoolExecutor, whatToDownload);
            }
            else {
                download.execute(whatToDownload);
            }
            whatToDownload = new HashMap<String, String>(2);
            whatToDownload.put("from", feedRoot);
            whatToDownload.put("to", "index.xml");
            download = new DownloadTask();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                download.executeOnExecutor(threadPoolExecutor, whatToDownload);
            }
            else {
                download.execute(whatToDownload);
            }
        } catch (RejectedExecutionException e) {
            Log.w("MyPractice Downloader", "Thread pool executor rejected the download task", e);
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
                TestFlight.passCheckpoint("Downloading started, device is online");
                publishProgress(new DownloadTaskStatus(DownloadStatusCodes.NETWORK_AVAILABLE));
                String from = whatToDownload[0].get("from");
                String to = whatToDownload[0].get("to");
                try {
                    dir = this.prepareDirectory();
                    publishProgress(new DownloadTaskStatus(DownloadStatusCodes.SWITCH_TO_DETERMINATE));
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
                    int downloadedSizeSum = 0;
                    while ((downloadedSize = urlStream.read(downloadBuffer)) != -1) {
                        downloadedSizeSum += downloadedSize;
                        publishProgress(new DownloadTaskStatus(DownloadStatusCodes.UPDATE_PROGRESS,
                                downloadedSizeSum, fileSize));
                        fileOutputStream.write(downloadBuffer, 0, downloadedSize);
                    }
                    urlStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    TestFlight.log("Downloaded " + to);

                    if (!to.equals("skin/DesignPack.zip")) {
                        TestFlight.log("Parsing " + to + " for additional downloads...");
                        // We have a feed, have to parse it to decide whether there's
                        // anything left to download...
                        publishProgress(new DownloadTaskStatus(DownloadStatusCodes.PARSING_FOR_MORE));
                        MainParser parser = new MainParser(dir + to);
                        String filename;
                        if (parser.isMenu()) {
                            for (String subFeedURL : parser.getSubfeedURLs()) {
                                filename = MainParser.subFeedURLToLocal(
                                        subFeedURL, Data.GetFeedRoot(getApplicationContext()));
                                TestFlight.log("Found new file to download: " + from);
                                HashMap<String, String> additionalDownload = new HashMap<String, String>(2);
                                additionalDownload.put("from", subFeedURL);
                                additionalDownload.put("to", filename);
                                publishProgress(new DownloadTaskStatus(DownloadStatusCodes.NEW_DOWNLOAD, additionalDownload));
                            }
                        }
                        publishProgress(new DownloadTaskStatus(DownloadStatusCodes.DOWNLOAD_FINISHED, downloadedSize, fileSize));
                    }
                    else {
                        TestFlight.log("Extracting design pack...");
                        // We have a design pack, extract it
                        publishProgress(new DownloadTaskStatus(DownloadStatusCodes.EXTRACTING));
                        try {
                            Skin.extractDesignPack(getApplicationContext());
                            publishProgress(new DownloadTaskStatus(DownloadStatusCodes.EXTRACTING_FINISHED, downloadedSize, fileSize));
                        } catch (ZipException e) {
                            e.printStackTrace();
                            publishProgress(new DownloadTaskStatus(DownloadStatusCodes.EXTRACTING_FAILED));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    publishProgress(new DownloadTaskStatus(DownloadStatusCodes.DOWNLOAD_FAILED));
                }
            }
            else {
                publishProgress(new DownloadTaskStatus(DownloadStatusCodes.DOWNLOAD_FAILED));
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            prepareDirectory();
            progress.show();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onProgressUpdate(DownloadTaskStatus... status) {
            super.onProgressUpdate(status);
            // Notify the summary about the update
            downloadSummary.updateStatus(taskStatusIndex, status[0]);
            switch (status[0].getStatusCode()) {
                case DownloadStatusCodes.NETWORK_AVAILABLE:
                    break;
                case DownloadStatusCodes.NEW_DOWNLOAD:
                    progress.setMessage(getString(R.string.starting_additional_download));
                    DownloadTask download = new DownloadTask();
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            download.executeOnExecutor(threadPoolExecutor, status[0].getNewDownload());
                        }
                        else {
                            download.execute(status[0].getNewDownload());
                        }
                    } catch (RejectedExecutionException e) {
                        Log.w("MyPractice Downloader", "Thread pool executor rejected the download task", e);
                    }
                    break;
                case DownloadStatusCodes.UPDATE_PROGRESS:
                    progress.setMessage(getString(R.string.downloading));
                    break;
                case DownloadStatusCodes.PARSING_FOR_MORE:
                    progress.setMessage(getString(R.string.parsing_for_more));
                    break;
                case DownloadStatusCodes.EXTRACTING:
                    progress.setMessage(getString(R.string.extracting_design));
                    break;
                case DownloadStatusCodes.DOWNLOAD_FINISHED:
                case DownloadStatusCodes.EXTRACTING_FINISHED:
                    if (downloadSummary.isCompleted()) {
                        threadPoolExecutor.shutdown();
                        progress.dismiss();
                        setResult(Activity.RESULT_OK);
                        progress.setMessage("Download finished.");
                        Intent intent = new Intent(DefaultActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case DownloadStatusCodes.DOWNLOAD_FAILED:
                case DownloadStatusCodes.EXTRACTING_FAILED:
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

