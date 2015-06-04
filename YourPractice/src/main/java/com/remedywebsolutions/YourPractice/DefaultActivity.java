package com.remedywebsolutions.YourPractice;

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
import android.os.Handler;
import android.support.v4.content.IntentCompat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.flurry.android.FlurryAgent;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.downloader.DownloadStatusCodes;
import com.remedywebsolutions.YourPractice.downloader.DownloadTaskStatus;
import com.remedywebsolutions.YourPractice.downloader.DownloadTaskStatusSummary;
import com.remedywebsolutions.YourPractice.parser.MainParser;

import net.lingala.zip4j.exception.ZipException;

import org.wordpress.passcodelock.AbstractAppLock;
import org.wordpress.passcodelock.AppLockManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    private Handler handler = new Handler();
    static int SIZE_OF_THREAD_POOL = 6;
    static int DOWNLOAD_BUFFER_SIZE = 8192;
    static int CONNECTION_TIMEOUT = 5000;
    private static boolean paused = false;
    private static Date lastActivityDate;
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



    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        this.invalidateOptionsMenu();
        int to = com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().EXTENDED_TIMEOUT;
        handler.postDelayed(runnable, to);
    }
    private void checkForIdle()
    {
        if(!paused) {
            Date now = new Date();
            long now_ms = now.getTime();
            int secondsPassed = (int) (now_ms - lastActivityDate.getTime()) / (1000);
            secondsPassed = Math.abs(secondsPassed);
            int to = com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().EXTENDED_TIMEOUT;
            if (secondsPassed > to) {
                com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().mustShowUnlockSceen();
                com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().onActivityResumed(this);
                lastActivityDate = new Date();
            } else {
                handler.postDelayed(runnable, to * 1000 + 5000);
            }
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock() != null) {
                checkForIdle();
            }

        }
    };
    @Override
    protected void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(this, "G8V8NZ5BZ6BBJHF48B5W");
        lastActivityDate =  new Date();
        LoggedInDataStorage store = new LoggedInDataStorage(DefaultActivity.this);
        paused = false;
        handler.postDelayed(runnable, store.GetPinTimeout() * 1000);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        paused = true;

    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        paused = false;

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    public void reportPhase(String phaseName) {
        Map<String, String> phaseParams = new HashMap<String, String>();
        phaseParams.put("name", phaseName);
        FlurryAgent.logEvent("EnteredPhase", phaseParams);
    }

    public void setProgressMessageWaitAndDismissWithRunnable(String message, Runnable finishRunnable) {
        progress.setMessage(message);
        Handler handler = new Handler();
        final Runnable taskToRun = finishRunnable;
        handler.postDelayed(new Runnable() {
            public void run() {
                progress.dismiss();
                taskToRun.run();
            }
        }, 1000);
    }

    public void setProgressMessageWaitAndDismiss(String message, boolean shouldGoBackAfter) {
        final boolean triggerBack = shouldGoBackAfter;
        setProgressMessageWaitAndDismissWithRunnable(message, new Runnable() {
            @Override
            public void run() {
                if (triggerBack) {
                    onBackPressed();
                }
            }
        });
    }

    public void setProgressMessageWaitAndDismiss(String message) {
        setProgressMessageWaitAndDismiss(message, false);
    }


    /**
     * Sets the Home button's visibility.
     *
     * @param visible Whether the Home button should be visible or not.
     */
    public void setHomeVisibility(boolean visible) {
        MenuItem item = abMenu.findItem(R.id.home);
        item.setVisible(visible);
    }

    /**
     * Disables the entire options menu for activities where it might be
     * misleading.
     */
    public void disableOptionsMenu() {
        MenuItem item = abMenu.findItem(R.id.more);
        item.setVisible(false);
    }

    /**
     * Sets up several menu item's visibility based on whether the user is logged in or not.
     *
     * This depends on the abMenu being set, so it should be called in
     * {@link com.remedywebsolutions.YourPractice.DefaultActivity#onCreateOptionsMenu(com.actionbarsherlock.view.Menu)}.
     *
     * @param loggedIn true if the user is logged in at the moment, false otherwise.
     */
    public void setMenuItemsVisibilityBasedOnLogin(boolean loggedIn) {
        MenuItem li_item = abMenu.findItem(R.id.menu_login);
        MenuItem lo_item = abMenu.findItem(R.id.menu_logout);
        if(Data.IsProviderMode(getApplicationContext())) {
            if (loggedIn) {

                li_item.setVisible(false);
            } else {

                lo_item.setVisible(false);
                //     item = abMenu.findItem(R.id.menu_my_account);
                //   item.setVisible(false);
            }
        }
        else
        {
            li_item.setVisible(false);
            lo_item.setVisible(false);
        }
    }

    /**
     * Event for responding to options menu creation.
     *
     * Overrides default event to inflate our menu items into the given menu.
     *
     * @param menu Menu to inflate to.
     * @return Whether inflating was successful or not.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        com.actionbarsherlock.view.MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        abMenu = menu;
        LoggedInDataStorage storage = new LoggedInDataStorage(DefaultActivity.this);
        setMenuItemsVisibilityBasedOnLogin(storage.isLoggedIn());
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
                if(Data.IsProviderMode(getApplicationContext()))
                {
                    intent = new Intent(this, ProviderMenuActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    MainViewController.FireRootActivity(this);
                    finish();
                }
               // MainViewController.FireRootActivity(this);
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
            case R.id.menu_choose_practice:
                Data.ClearAppMode(getApplicationContext());
                LoggedInDataStorage store = new LoggedInDataStorage(DefaultActivity.this);
                store.logOut();

                // Reset passcode lock
                if(com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock() != null) {
                    com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().setPassword(null);
                }
                invalidateOptionsMenu();
                Data.ClearRegistered(getApplicationContext());
                intent = new Intent(this, PracticeSearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_provider_mode:
                LoggedInDataStorage store1 = new LoggedInDataStorage(DefaultActivity.this);
                store1.logOut();

                // Reset passcode lock
              //  if(com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock() != null) {
                //    com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().setPassword(null);
              //  }
                invalidateOptionsMenu();
                Data.ClearRegistered(getApplicationContext());
                intent = new Intent(this, SelectModeActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_logout:

              //  LoggedInDataStorage storage = new LoggedInDataStorage(DefaultActivity.this);
              //  String un = storage.RetrieveData().get("username").toString();

               // storage.logOut();


            //    Toast.makeText(DefaultActivity.this, "You've been logged out.", Toast.LENGTH_LONG).show();
                // Reset passcode lock
              //  com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().setPassword(null);
                invalidateOptionsMenu();
                Data.ClearRegistered(getApplicationContext());
                intent = new Intent(this, LogoutActivity.class);
                startActivity(intent);
                finish();
                return true;
         //   case R.id.menu_my_account:
            //    intent = new Intent(this, MyAccountActivity.class);
            //    startActivity(intent);
              //  return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the title with the informations stored in the Bundle.
     *
     * WARNING! Fire this only after the header has been inflated or you will
     * get a nice NullPointerException.
     */
    public void setTitleFromIntentBundle() {
        if (extras != null && extras.containsKey("title")) {
            String title = extras.getString("title");
            if (title != null) {
                setTitle(title);
            }
        }
    }

    /**
     * Sets up TextViews so that links will respond to clicks.
     *
     * @param textViewResourceIds The resource ids for text views.
     */
    public void makeTextViewLinksClickable(Integer... textViewResourceIds) {
        for (Integer id : textViewResourceIds) {
            TextView text = (TextView) findViewById(id);
            if (text != null) {
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    /**
     * Sets up a title view, if there's any.
     *
     * @param title The title to set.
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        TextView titleview = (TextView) findViewById(R.id.titleTextView);
        if (titleview != null) {
            titleview.setText(title);
        }
    }

    /**
     * Hides the title view, if there's any.
     */
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

    /**
     * Deletes all previously downloaded files to prepare an update, for example.
     *
     * This simple solution was taken from: http://stackoverflow.com/a/5322390/238845 .
     * To be used successfully, using {@link #saveIndex()} and {@link #restoreIndex()} is necessary.
     *
     */
    protected void deleteFiles() {
        assert getApplicationContext() != null;
        assert getApplicationContext().getFilesDir() != null;
        String directoryPath = getApplicationContext().getFilesDir().getAbsolutePath();
        File file = new File(directoryPath);
        if (file.exists()) {
            String deleteCmd = "rm -rf " + directoryPath;
            Runtime runtime = Runtime.getRuntime();
            try {
                Process p = runtime.exec(deleteCmd);
                p.waitFor();
            } catch (IOException ignored) {
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Saves current index file into the cache dir to allow clearing data without any loss.
     *
     * Should be used with {@link #deleteFiles()} and {@link #restoreIndex()}.
     */
    protected void saveIndex() {
        assert getApplicationContext() != null;
        assert getApplicationContext().getFilesDir() != null;
        assert getApplicationContext().getCacheDir() != null;
        String tempDirPath = getApplicationContext().getCacheDir().getAbsolutePath();
        String indexPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/index.xml";
        File file = new File(indexPath);
        if (file.exists()) {
            String cpCmd = "cp " + indexPath + " " + tempDirPath;
            Runtime runtime = Runtime.getRuntime();
            try {
                Process p = runtime.exec(cpCmd);
                p.waitFor();
            } catch (IOException ignored) {
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Restores current index file into the cache dir to allow clearing data without any loss.
     *
     * Should be used with {@link #deleteFiles()} and {@link #saveIndex()}.
     */
    protected void restoreIndex() {
        assert getApplicationContext() != null;
        assert getApplicationContext().getFilesDir() != null;
        assert getApplicationContext().getCacheDir() != null;
        String tempFilePath = getApplicationContext().getCacheDir().getAbsolutePath() + "/index.xml";
        String indexPath = getApplicationContext().getFilesDir().getAbsolutePath();
        File file = new File(tempFilePath);
        if (file.exists()) {
            String cpCmd = "cp " + tempFilePath + " " + indexPath;
            Runtime runtime = Runtime.getRuntime();
            try {
                Process p = runtime.exec(cpCmd);
                p.waitFor();
            } catch (IOException ignored) {
            } catch (InterruptedException ignored) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void startDownload(String feedRoot, String designPack) {
        // Reset data storage.
        saveIndex();
        deleteFiles();
        restoreIndex();

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

    protected void defaultSpiceFailureHandler(SpiceException spiceException) {
        Toast.makeText(DefaultActivity.this,
                "Error: " + spiceException.toString(), Toast.LENGTH_SHORT)
                .show();
        spiceException.printStackTrace();
        HashMap<String, String> errorMap = new HashMap<String, String>();
        errorMap.put("stacktrace", Arrays.toString(spiceException.getStackTrace()));
        errorMap.put("message", spiceException.getMessage());
        FlurryAgent.logEvent("RoboSpice error", errorMap);
        progress.dismiss();
    }

    // This AsyncTask is for downloading a single file.
    private class DownloadTask extends AsyncTask<HashMap<String, String>, DownloadTaskStatus, Void> {
        String dir;
        Integer taskStatusIndex;

        @Override
        protected Void doInBackground(HashMap<String, String>... whatToDownload) {
            taskStatusIndex = downloadSummary.reservePlace();
            if (DefaultActivity.this.isOnline()) {
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
                    int fileSize;
                    int downloadedSize;
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

                    if (!to.equals("skin/DesignPack.zip")) {
                        // We have a feed, have to parse it to decide whether there's
                        // anything left to download...
                        publishProgress(new DownloadTaskStatus(DownloadStatusCodes.PARSING_FOR_MORE));
                        MainParser parser = new MainParser(dir + to);
                        String filename;
                        if (parser.isMenu()) {
                            for (String subFeedURL : parser.getSubfeedURLs()) {
                                filename = MainParser.subFeedURLToLocal(
                                        subFeedURL, Data.GetFeedRoot(getApplicationContext()));
                                HashMap<String, String> additionalDownload = new HashMap<String, String>(2);
                                additionalDownload.put("from", subFeedURL);
                                additionalDownload.put("to", filename);
                                publishProgress(new DownloadTaskStatus(DownloadStatusCodes.NEW_DOWNLOAD, additionalDownload));
                            }
                        }
                        publishProgress(new DownloadTaskStatus(DownloadStatusCodes.DOWNLOAD_FINISHED, downloadedSize, fileSize));
                    }
                    else {
                        // We have a design pack, extract it
                        publishProgress(new DownloadTaskStatus(DownloadStatusCodes.EXTRACTING));
                        try {
                            Skin.extractDesignPack(getApplicationContext());
                            publishProgress(new DownloadTaskStatus(DownloadStatusCodes.EXTRACTING_FINISHED, downloadedSize, fileSize));
                        } catch (ZipException e) {
                            publishProgress(new DownloadTaskStatus(DownloadStatusCodes.EXTRACTING_FAILED));
                        }
                    }
                } catch (IOException e) {
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
                        Log.w("YourPractice downloader", "Thread pool executor rejected the download task", e);
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
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case DownloadStatusCodes.DOWNLOAD_FAILED:
                case DownloadStatusCodes.EXTRACTING_FAILED:
                    threadPoolExecutor.shutdown();
                    progress.dismiss();
                    assert getApplicationContext() != null;
                    Toast.makeText(getApplicationContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_FIRST_USER);
                    break;
            }
        }

        protected String prepareDirectory() {
            assert getApplicationContext() != null;
            assert getApplicationContext().getFilesDir() != null;
            String directoryPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
            File directory = new File(directoryPath);
            if (directory.mkdir() || directory.isDirectory()) {
                String skinPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/skin/";
                File skinDir = new File(skinPath);
                //noinspection ResultOfMethodCallIgnored
                skinDir.mkdir();
            }
            return directoryPath;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Disable the passcode.
     *
     * This method is used by the API testing to avoid asking for passcode during the testing.
     */
    public void disablePasscode() {
        AbstractAppLock lock = AppLockManager.getInstance().getCurrentAppLock();
        lock.setPassword(null);
    }
}

