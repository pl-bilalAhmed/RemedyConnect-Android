package com.remedywebsolutions.YourPractice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import org.wordpress.passcodelock.AppLockManager;


public class SelectModeActivity extends DefaultActivity  implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Select application mode");
        setContentView(R.layout.activity_mode_menu);
        Skin.applyMainMenuBackground(this);

        Skin.applyThemeLogo(this, true);
        setTitle(R.string.title_select_practice);

        Button b1 = (Button)this.findViewById(R.id.provider_mode_button);
        Skin.applyMenuButtonStyle(this,b1);
        b1.setOnClickListener(this);

        Button b2 = (Button)this.findViewById(R.id.patient_mode_button);
        Skin.applyMenuButtonStyle(this,b2);
        b2.setOnClickListener(this);

        View search = this.findViewById(R.id.return_to_Search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Data.ClearAppMode(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), PracticeSearchActivity.class);
                startActivity(intent);
            }
        });
    }


    public void onClick(View v) {

        Button b = (Button)v;
        if(b.getId() == R.id.provider_mode_button)
        {
            com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().enableDefaultAppLockIfAvailable(getApplication());

            Data.SetProviderAppMode(getApplicationContext());
            if(Data.IsRegistered(getApplicationContext())) {
                Intent intent = new Intent(this, ProviderMenuActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("fromMode",true);
                startActivity(intent);
                finish();
            }
        }
        else
        {
            com.remedywebsolutions.YourPractice.passcode.AppLockManager.getInstance().setCurrentAppLock(null);
            Data.SetPatientAppMode(getApplicationContext());
            MainViewController.FireRootActivity(this);
            finish();
        }

    }


}
