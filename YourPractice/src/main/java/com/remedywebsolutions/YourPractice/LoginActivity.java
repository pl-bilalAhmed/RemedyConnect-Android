package com.remedywebsolutions.YourPractice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.Physician;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.PhysiciansResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.GetPhysiciansRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.InsertPhysicianMobileDeviceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;

import org.wordpress.passcodelock.PasscodeManagePasswordActivity;

public class LoginActivity extends DefaultActivity {
    private String username;
    private int physicianId;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Login");
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login));
        final Button button = (Button) findViewById(R.id.buttonLogin);
        Skin.applyButtonStyle(this, button);
        Skin.applyActivityBackground(this);
        usernameEditor = (EditText) findViewById(R.id.userName);
        passwordEditor = (EditText) findViewById(R.id.password);

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert usernameEditor.getText() != null;
                assert passwordEditor.getText() != null;
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
            int practiceId = response.getPracticeID();
            String token = response.getToken();
            if (token != null) {
                progress.setMessage("Logged in, storing data...");
                dataStorage.StoreDataOnLogin(physicianId, practiceId, token);
                progress.setMessage("Logged in. Registering device...");
                InsertPhysicianMobileDeviceRequest req =
                        new InsertPhysicianMobileDeviceRequest(physicianId,
                                practiceId,
                                                                username,
                                                                LoginActivity.this);

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
        public void onRequestSuccess(String pushIOHash) {
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
            getAndSetNameFromResponse(physiciansResponse, physicianId);
            progress.setMessage("Fetched contacts - login complete.");
            progress.dismiss();

            Toast.makeText(LoginActivity.this, "You've been logged in.", Toast.LENGTH_SHORT).show();

            new AlertDialog.Builder(LoginActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Set up a passcode")
                    .setMessage("For safety reasons, setting up a 4-digit passcode is necessary.\n\nPlease press OK to proceed.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent passcodeIntent = new Intent(LoginActivity.this, PasscodeManagePasswordActivity.class);
                            passcodeIntent.putExtra("type", ENABLE_PASSLOCK);
                            startActivityForResult(passcodeIntent, ENABLE_PASSLOCK);
                        }
                    })
                    .show();
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
                }
                break;
            default:
                break;
        }
        onBackPressed();

    }
}
