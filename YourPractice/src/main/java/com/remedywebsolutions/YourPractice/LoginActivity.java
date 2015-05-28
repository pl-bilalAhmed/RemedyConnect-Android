package com.remedywebsolutions.YourPractice;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pushio.manager.PushIOManager;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.GetProviderPinTimeoutResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Physician;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPhysiciansRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPracticeUtcTimeZoneOffsetRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetProviderPinTimeoutRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.InsertPhysicianMobileDeviceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;
import com.remedywebsolutions.YourPractice.passcode.AppLockManager;

import org.wordpress.passcodelock.PasscodeManagePasswordActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class LoginActivity extends DefaultActivity implements View.OnClickListener {
    private String username;
    private int physicianId, practiceId;
    private LoggedInDataStorage dataStorage;
    private EditText usernameEditor;
    private EditText passwordEditor;

    static final int ENABLE_PASSLOCK = 0;

    private SpiceManager spiceManager= new SpiceManager(UncachedSpiceService.class);

    @Override
    protected void onStart() {

        spiceManager.start(this);
        super.onStart();

    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.home:

                Intent intent = new Intent(this, SelectModeActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reportPhase("Login");
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login));
        final Button button = (Button) findViewById(R.id.buttonLogin);
      //  Skin.applyButtonStyle(this, button);
        Skin.applyActivityBackground(this);
        usernameEditor = (EditText) findViewById(R.id.userName);
        passwordEditor = (EditText) findViewById(R.id.password);
        TextView forgotBtn = (TextView) findViewById(R.id.forgotPaswsord);
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

      //  getSupportActionBar().setDisplayHomeAsUpEnabled(false);
       // getSupportActionBar().setHomeButtonEnabled(false);


        forgotBtn.setOnClickListener(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert usernameEditor.getText() != null;
                assert passwordEditor.getText() != null;
                username = usernameEditor.getText().toString();
                String password = passwordEditor.getText().toString();
                if(username.length() == 0)
                {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Enter your user name")
                            .setMessage("Username cannot be blank.")
                            .setPositiveButton("OK", null)
                            .show();
                    usernameEditor.requestFocus();

                }
                else if(password.length() == 0) {

                    new AlertDialog.Builder(LoginActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Enter your password")
                            .setMessage("Password cannot be blank.")
                            .setPositiveButton("OK", null)
                            .show();
                    passwordEditor.requestFocus();
                }
                else {
                    LoginRequest req = new LoginRequest(username, password, LoginActivity.this);
                    progress.setMessage("Logging in...");
                    progress.show();
                    spiceManager.execute(req, new LoginRequestListener());
                }
            }
        });
        dataStorage = new LoggedInDataStorage(LoginActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result =  super.onCreateOptionsMenu(menu);
        setHomeVisibility(false);
        MenuItem li_item = abMenu.findItem(R.id.menu_login);
        MenuItem lo_item = abMenu.findItem(R.id.menu_logout);
        li_item.setVisible(false);

        lo_item.setVisible(false);
        return result;
    }

    public void onClick(View v)
    {
        assert usernameEditor.getText() != null;

        username = usernameEditor.getText().toString();
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority(Data.ADMIN_URL);
        uriBuilder.appendPath("Mobile/Providers/Default.aspx");

        try {
            uriBuilder.appendQueryParameter("username",username);
            Uri uri =  uriBuilder.build();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            this.startActivity(browserIntent);
        }

        catch (Exception e) {
            System.out.println("URI Syntax Error: " + e.getMessage());


        }

    }

    private final class LoginRequestListener implements RequestListener<LoginResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            defaultSpiceFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(LoginResponse response) {
            physicianId = response.getPhysicianID();
            practiceId = response.getPracticeID();
            String token = response.getToken();
            if (token != null) {
                Data.SetRegistered(getApplicationContext());
                progress.setMessage("Logged in, storing data...");
                dataStorage.StoreDataOnLogin(physicianId, practiceId, token,username);

                GetProviderPinTimeoutRequest req =
                        new GetProviderPinTimeoutRequest(LoginActivity.this);

                spiceManager.execute(req, new GetPinTimeoutRequestListener());
            }
            else {
                progress.dismiss();
                new AlertDialog.Builder(LoginActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Couldn't log you in")
                        .setMessage("Unknown username or bad password - please try again.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    private final class GetPinTimeoutRequestListener implements RequestListener<GetProviderPinTimeoutResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            defaultSpiceFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(GetProviderPinTimeoutResponse response) {
            int timeOut = response.pinTimeoutSeconds;

            if (timeOut <= 0) {
                timeOut = 60;
            }

            Data.SetRegistered(getApplicationContext());
            progress.setMessage("getting preference data...");
            dataStorage.StorePinTimeout(timeOut);
         //   AppLockManager.getInstance().getCurrentAppLock().DEFAULT_TIMEOUT = timeOut;
            AppLockManager.getInstance().getCurrentAppLock().EXTENDED_TIMEOUT = timeOut;

            InsertPhysicianMobileDeviceRequest req =
                    new InsertPhysicianMobileDeviceRequest(physicianId,
                            practiceId,
                            username,
                            LoginActivity.this);

            spiceManager.execute(req, new RegisterDeviceListener());


        }
    }
    private final class RegisterDeviceListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            defaultSpiceFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(String pushIOHash) {
            dataStorage.StoreDeviceId(pushIOHash);
            PushIOManager.getInstance(LoginActivity.this).registerUserId(pushIOHash);
            Log.d("YourPractice", "Registered with Push.IO with the following user ID: " +
                    PushIOManager.getInstance(LoginActivity.this).getRegisteredUserId());
            progress.setMessage("Registered device.");
            GetPracticeUtcTimeZoneOffsetRequest req =
                    new GetPracticeUtcTimeZoneOffsetRequest(practiceId, LoginActivity.this);
            spiceManager.execute(req, new TimezoneOffsetListener());

         //   GetPhysiciansRequest req = new GetPhysiciansRequest(LoginActivity.this);
         //   spiceManager.execute(req, new PullContactsListener());
        }
    }

    private final class PullContactsListener implements RequestListener<PhysiciansResponse> {
        @Override
        public void onRequestFailure(SpiceException e) {
            defaultSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(PhysiciansResponse physiciansResponse) {
            getAndSetNameFromResponse(physiciansResponse, physicianId);
            progress.setMessage("Fetching practice info...");
            GetPracticeUtcTimeZoneOffsetRequest req =
                    new GetPracticeUtcTimeZoneOffsetRequest(practiceId, LoginActivity.this);
            spiceManager.execute(req, new TimezoneOffsetListener());
        }
    }

    private final class TimezoneOffsetListener implements RequestListener<Integer> {
        @Override
        public void onRequestFailure(SpiceException e) {
            defaultSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(Integer offset) {
            dataStorage.StoreTimezoneOffset(offset);
            progress.setMessage("Login complete.");
            progress.dismiss();

            Toast.makeText(LoginActivity.this, "You've been logged in.", Toast.LENGTH_SHORT).show();

            if(Data.IsPinSet(getApplicationContext()))
            {

                Intent intent = new Intent(LoginActivity.this, ProviderMenuActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                if(com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock() != null) {
                    com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().getCurrentAppLock().forcePasswordLock();
                }
                Intent intent = new Intent(LoginActivity.this, CreatePinActivity.class);
                startActivity(intent);
                finish();
            }


          //  new AlertDialog.Builder(LoginActivity.this)
           //         .setIcon(android.R.drawable.ic_dialog_info)
             //       .setTitle("Set up a passcode")
              //      .setMessage("For safety reasons, setting up a 4-digit passcode is necessary.\n\nPlease press OK to proceed.")
              //      .setPositiveButton("OK", new DialogInterface.OnClickListener() {
             //           @Override
                //        public void onClick(DialogInterface dialogInterface, int i) {
                //            Intent passcodeIntent = new Intent(LoginActivity.this, com.remedywebsolutions.YourPractice.passcode.PasscodeManagePasswordActivity.class);
                //            passcodeIntent.putExtra("type", ENABLE_PASSLOCK);
                 //           startActivityForResult(passcodeIntent, ENABLE_PASSLOCK);
                 //       }
                //    })
                 //   .show();
        }
    }

    public String getAndSetNameFromResponse(PhysiciansResponse physiciansResponse, int physicianId) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String physicians = mapper.writeValueAsString(physiciansResponse);
            dataStorage.StorePhysicians(physicians);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String name = null;
        for (Physician physician : physiciansResponse.physicians) {
            if (physician.physicianID == physicianId) {
                name = physician.physicianName;
            }
        }
        dataStorage.StoreName(name);
        return name;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ENABLE_PASSLOCK:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(LoginActivity.this, "Passcode successfully set.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ProviderMenuActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                break;
        }
        onBackPressed();

    }
}
