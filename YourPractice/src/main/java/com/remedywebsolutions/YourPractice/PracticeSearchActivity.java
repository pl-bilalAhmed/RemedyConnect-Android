package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.remedywebsolutions.YourPractice.downloader.DownloadStatusCodes;
import com.remedywebsolutions.YourPractice.downloader.RootDownloadService;
import com.remedywebsolutions.YourPractice.parser.MainParser;
import com.remedywebsolutions.YourPractice.passcode.AppLockManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class PracticeSearchActivity extends DefaultActivity implements OnClickListener,View.OnTouchListener {
    ProgressDialog progress;
    private Semaphore buttonSemaphore = new Semaphore(1);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Practice search");
        setContentView(R.layout.activity_practice_search);
        Skin.applyActivityBackground(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        progress = new ProgressDialog(this);

        progress.setMessage(getString(R.string.searching_locations));

        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

      //  Button startSearchByName = (Button)this.findViewById(R.id.practiceSearchStartByName);
     //   Button startSearchByLocation = (Button)this.findViewById(R.id.practiceSearchStartByLocation);
        SearchView searchView = (SearchView)this.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {
                startFetchingByName(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


     //   startSearchByName.setOnClickListener(this);
        View locSearch = this.findViewById(R.id.SearchByLoc);
      //  locSearch.setOnClickListener(this);
        locSearch.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result =  super.onCreateOptionsMenu(menu);
        setHomeVisibility(false);
        disableOptionsMenu();
        return result;
    }

    public void onClick(View v) {

   //     startFetchingByLocation();
      //  startFetchingByName();
      //  switch(v.getId()){
       //     case R.id.practiceSearchStartByName:
        //        startFetchingByName();
         //       break;
        //   case R.id.practiceSearchStartByLocation:
           //     startFetchingByLocation();
            //    break;
      //  }
    }

    public void startFetchingByName(String search) {
        AppLockManager.getInstance().setCurrentAppLock(null);
        String practiceName = search;
        Intent intent = new Intent(this, RootDownloadService.class);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        intent.putExtra("practiceName", practiceName);
        startService(intent);
    }

    public void startFetchingByLocation() {



        progress.show();
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                gotLocation(location);
                locationManager.removeUpdates(this);
                progress.dismiss();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        Location lastLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLoc != null) {
            if (lastLoc.getTime() >= System.currentTimeMillis() - 60 * 1000) {
                gotLocation(lastLoc);
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    public void gotLocation(Location location) {
        Intent intent = new Intent(this, RootDownloadService.class);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        intent.putExtra("location", location);
        startService(intent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(buttonSemaphore.tryAcquire()) {
            startFetchingByLocation();
        }
        return false;
    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            try {
                super.onReceiveResult(resultCode, resultData);
                if (resultCode == DownloadStatusCodes.NETWORK_AVAILABLE) {
                    progress.setMessage(getString(R.string.downloading));
                    progress.show();
                }
                if (resultCode == DownloadStatusCodes.UPDATE_PROGRESS) {
                    int status = resultData.getInt("progress");
                    progress.setProgress(status);
                }
                if (resultCode == DownloadStatusCodes.DOWNLOAD_FINISHED) {
                    progress.dismiss();
                    startParsingPractices();
                    setResult(Activity.RESULT_OK);
                    buttonSemaphore.release();
                }
                if (resultCode == DownloadStatusCodes.DOWNLOAD_FAILED) {
                    progress.dismiss();
                    assert getApplicationContext() != null;
                    Toast.makeText(getApplicationContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_FIRST_USER);
                }
                if (resultCode == DownloadStatusCodes.SSL_PROBLEM) {
                    progress.dismiss();
                    assert getApplicationContext() != null;
                    Toast.makeText(getApplicationContext(), R.string.downloadssl_failed, Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_FIRST_USER);
                }
                if (resultCode == DownloadStatusCodes.SWITCH_TO_DETERMINATE) {
                    progress.setIndeterminate(false);
                }
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void startParsingPractices() {
        assert this.getFilesDir() != null;
        MainParser parser = new MainParser(this.getFilesDir().getAbsolutePath() + "/root.xml");
        if (parser.isRoot()) {
            ArrayList<HashMap<String, String>> practices = parser.getRootPractices();
            if (!practices.isEmpty()) {
                Intent intent = new Intent(PracticeSearchActivity.this, SelectPracticeActivity.class);
                intent.putExtra("practices", practices);
                startActivity(intent);
            }
            else {
                assert getApplicationContext() != null;
                Toast.makeText(getApplicationContext(), getString(R.string.couldnt_find_practice_near_location),
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            assert getApplicationContext() != null;
            Toast.makeText(getApplicationContext(), getString(R.string.couldnt_find_practice_near_location),
                    Toast.LENGTH_LONG).show();
        }
    }
}
