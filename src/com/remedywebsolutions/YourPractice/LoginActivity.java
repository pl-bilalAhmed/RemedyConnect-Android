package com.remedywebsolutions.YourPractice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.remedywebsolutions.YourPractice.MedSecureAPI.MedSecureConnection;

public class LoginActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button button = (Button) findViewById(R.id.buttonStartTesting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedSecureConnection connection = new MedSecureConnection();
                connection.setContext(LoginActivity.this);
                connection.startAsyncLogin("zoltan", "zoltan1");
            }
        });
    }
}
