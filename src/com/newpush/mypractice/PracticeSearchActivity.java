package com.newpush.mypractice;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.newpush.mypractice.downloader.DownloadStatus;
import com.newpush.mypractice.downloader.RootDownloadService;
import com.newpush.mypractice.parser.MainParser;

import java.util.ArrayList;
import java.util.HashMap;

public class PracticeSearchActivity extends DefaultActivity implements OnClickListener {
    ProgressDialog progress;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_search);

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.downloading));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        Button startSearchByName = (Button)this.findViewById(R.id.practiceSearchStartByName);
        Button startSearchByLocation = (Button)this.findViewById(R.id.practiceSearchStartByLocation);

        startSearchByName.setOnClickListener(this);
        startSearchByLocation.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.practiceSearchStartByName:
                startFetchingByName();
                break;
            case R.id.practiceSearchStartByLocation:
                startFetchingByLocation();
                break;
        }
    }

    public void startFetchingByName() {
        EditText practiceNameView = (EditText)this.findViewById(R.id.practiceSearchEdit);
        String practiceName = practiceNameView.getText().toString();
        Intent intent = new Intent(this, RootDownloadService.class);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        intent.putExtra("practiceName", practiceName);
        startService(intent);
    }

    public void startFetchingByLocation() {
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                gotLocation(location);
                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(),
                        "Provider disabled: " + provider,
                        Toast.LENGTH_SHORT).show();
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public void gotLocation(Location location) {
        Toast.makeText(getApplicationContext(),
                "Search by location succeed, location: " + location.toString(),
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RootDownloadService.class);
        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
        intent.putExtra("location", location);
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
            }
            if (resultCode == DownloadStatus.DOWNLOAD_FINISHED) {
                progress.dismiss();
                setResult(Activity.RESULT_OK);
                startParsingPractices();
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

    protected void startParsingPractices() {
        // @TODO Should figure out something better instead of this, huh?
        MainParser parser = new MainParser(this.getFilesDir().getAbsolutePath() + "/root.xml");
        Intent intent = new Intent(PracticeSearchActivity.this, SelectPracticeActivity.class);
        if (parser.isRoot()) {
            ArrayList<HashMap<String, String>> practices = parser.getRootPractices();
            intent.putExtra("practices", practices);
        }
        startActivity(intent);
    }
}