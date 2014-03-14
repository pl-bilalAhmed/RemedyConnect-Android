package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pushio.manager.PushIOManager;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Physician;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPhysiciansRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.RegisterDeviceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;

import java.io.IOException;

public class LoginActivity extends DefaultActivity {
    private String username;
    private String token;
    private int physicianId, practiceId;
    private String pushIOHash;
    private LoggedInDataStorage dataStorage;
    private EditText usernameEditor;
    private EditText passwordEditor;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login));
        final Button button = (Button) findViewById(R.id.buttonLogin);
        usernameEditor = (EditText) findViewById(R.id.userName);
        passwordEditor = (EditText) findViewById(R.id.password);
        // @TODO: remove this, only for debug!
        usernameEditor.setText("zoltan");
        passwordEditor.setText("zoltan1");

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditor.getText().toString();
                String password = passwordEditor.getText().toString();
                LoginRequest req = new LoginRequest(username, password, LoginActivity.this);
                progress.setMessage("Logging in...");
                progress.show();
                spiceManager.execute(req, new LoginRequestListener());
            }
        });
        dataStorage = new LoggedInDataStorage(LoginActivity.this);
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
            token = response.getToken();
            if (token != null) {
                progress.setMessage("Logged in, storing data...");
                dataStorage.StoreDataOnLogin(physicianId, practiceId, token);
                progress.setMessage("Logged in. Registering device...");
                RegisterDeviceRequest req = new RegisterDeviceRequest(physicianId, username, LoginActivity.this);
                spiceManager.execute(req, new RegisterDeviceListener());
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

    private final class RegisterDeviceListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            defaultSpiceFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(String result) {
            pushIOHash = result;
            dataStorage.StoreDeviceId(pushIOHash);
            PushIOManager.getInstance(LoginActivity.this).registerUserId(pushIOHash);
            Log.d("YourPractice", "Registered with Push.IO with the following user ID: " +
                    PushIOManager.getInstance(LoginActivity.this).getRegisteredUserId());
            progress.setMessage("Registered device. Fetching your contacts...");
            GetPhysiciansRequest req = new GetPhysiciansRequest(LoginActivity.this);
            spiceManager.execute(req, new PullContactsListener());
        }
    }

    private final class PullContactsListener implements RequestListener<PhysiciansResponse> {
        @Override
        public void onRequestFailure(SpiceException e) {
            defaultSpiceFailureHandler(e);
        }

        @Override
        public void onRequestSuccess(PhysiciansResponse physiciansResponse) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String physicians = mapper.writeValueAsString(physiciansResponse);
                dataStorage.StorePhysicians(physicians);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            String name = "";
            for (Physician physician : physiciansResponse.physicians) {
                if (physician.physicianID == physicianId) {
                    name = physician.physicianName;
                }
            }
            dataStorage.StoreName(name);
            progress.setMessage("Fetched contacts - login complete.");
            progress.dismiss();
            onBackPressed();
            Toast.makeText(LoginActivity.this, "You've been logged in.", Toast.LENGTH_LONG).show();
        }
    }
}
