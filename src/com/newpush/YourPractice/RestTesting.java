package com.newpush.YourPractice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.newpush.YourPractice.MedSecureAPI.MedSecureConnection;

public class RestTesting extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_testing);
        final Button button = (Button) findViewById(R.id.buttonStartTesting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedSecureConnection connection = new MedSecureConnection();
                connection.setContext(RestTesting.this);
                connection.startAsyncGetPractice(36);
            }
        });
    }
}