package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pushio.manager.PushIOManager;
import com.remedywebsolutions.YourPractice.MedSecureAPI.LoggedInDataStorage;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.RegisterDeviceRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.POJOs.LoginResponse;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.SendInAppNotificationRequest;

public class LoginActivity extends DefaultActivity {
    //private static final String KEY_RESULT = "login_result";

    private String username;
    private String token;
    private int physicianId;
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
                MedSecureConnection connection = new MedSecureConnection(LoginActivity.this);
                username = usernameEditor.getText().toString();
                String password = passwordEditor.getText().toString();
                LoginRequest req = new LoginRequest(username, password, LoginActivity.this);
                spiceManager.execute(req, new LoginRequestListener());
                progress.setMessage("Logging in...");
                progress.show();
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
            token = response.getToken();
            progress.setMessage("Logged in, storing data...");
            dataStorage.StoreDataOnLogin(physicianId, token);
            progress.setMessage("Logged in. Registering device...");
            RegisterDeviceRequest req = new RegisterDeviceRequest(physicianId, username, LoginActivity.this);
            spiceManager.execute(req, new RegisterDeviceListener());
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
            progress.setMessage("Registered device - login complete.");
            progress.dismiss();
            onBackPressed();
        }
    }
}
