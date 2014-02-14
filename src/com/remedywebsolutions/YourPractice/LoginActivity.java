package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pushio.manager.PushIOManager;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.LoginRequest;
import com.remedywebsolutions.YourPractice.MedSecureAPI.requests.RegisterDeviceRequest;

public class LoginActivity extends Activity {
    //private static final String KEY_RESULT = "login_result";

    private String username;
    private String physicianId;
    private String pushIOHash;

    private SpiceManager spiceManager= new SpiceManager(
            UncachedSpiceService.class
    );

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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedSecureConnection connection = new MedSecureConnection();
                connection.setContext(LoginActivity.this);
                EditText usernameEditor = (EditText) findViewById(R.id.userName);
                EditText passwordEditor = (EditText) findViewById(R.id.password);
                username = usernameEditor.getText().toString();
                String password = passwordEditor.getText().toString();
                //connection.startAsyncLogin(username, password);

                LoginRequest req = new LoginRequest(username, password);
                spiceManager.execute(req, new LoginRequestListener());
            }
        });
    }

    private void usualFailureHandler(SpiceException spiceException) {
        Toast.makeText(LoginActivity.this,
                "Error: " + spiceException.getMessage(), Toast.LENGTH_SHORT)
                .show();
    }

    // Phase 1: request physician id from username/password
    private final class LoginRequestListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            usualFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(String physicianId) {
            LoginActivity.this.physicianId = physicianId;
            RegisterDeviceRequest req = new RegisterDeviceRequest(physicianId, username);
            spiceManager.execute(req, new RegisterDeviceListener());
        }
    }

    private final class RegisterDeviceListener implements RequestListener<String> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            usualFailureHandler(spiceException);
        }

        @Override
        public void onRequestSuccess(String result) {
            LoginActivity.this.pushIOHash = result;
            PushIOManager.getInstance(LoginActivity.this).registerUserId(LoginActivity.this.pushIOHash);
        }
    }



    // Phase 3: pull user data
}
