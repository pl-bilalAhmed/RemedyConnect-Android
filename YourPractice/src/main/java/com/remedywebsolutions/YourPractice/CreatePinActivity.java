package com.remedywebsolutions.YourPractice;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class CreatePinActivity extends DefaultActivity implements View.OnClickListener{
    static final int ENABLE_PASSLOCK = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
        Button button = (android.widget.Button) findViewById(R.id.buttonCreatePin);
        button.setOnClickListener(this);
        ActionBar actionBar = getActionBar();

        try {
            actionBar.setDisplayHomeAsUpEnabled(false);

        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        boolean result =  super.onCreateOptionsMenu(menu);
        setHomeVisibility(false);

        com.actionbarsherlock.view.MenuItem cm_item = abMenu.findItem(R.id.menu_provider_mode);
        com.actionbarsherlock.view.MenuItem refresh_item = abMenu.findItem(R.id.menu_update);
        refresh_item.setVisible(false);

        cm_item.setTitle("Patient/Guardian");


        return result;
    }

    public void onClick(View v) {

        Intent passcodeIntent = new Intent(CreatePinActivity.this, com.remedywebsolutions.YourPractice.passcode.PasscodeManagePasswordActivity.class);
        passcodeIntent.putExtra("type", ENABLE_PASSLOCK);
        startActivityForResult(passcodeIntent, ENABLE_PASSLOCK);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
            case android.R.id.home:
                // In this app, we can just simple force the Up button to behave the same way as the Back.
                //  this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ENABLE_PASSLOCK:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(CreatePinActivity.this, "Passcode successfully set.", Toast.LENGTH_SHORT).show();

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
