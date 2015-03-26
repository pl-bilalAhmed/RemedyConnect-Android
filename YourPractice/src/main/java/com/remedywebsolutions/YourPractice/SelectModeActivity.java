package com.remedywebsolutions.YourPractice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;


public class SelectModeActivity extends DefaultActivity  implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Select application mode");
        setContentView(R.layout.activity_mode_menu);
        Skin.applyMainMenuBackground(this);

        Skin.applyThemeLogo(this, true);
        setTitle(R.string.title_select_practice);

        View b1 = this.findViewById(R.id.provider_mode_button);
        b1.setOnClickListener(this);

        View b2 = this.findViewById(R.id.patient_mode_button);
        b2.setOnClickListener(this);

    }


    public void onClick(View v) {

        Button b = (Button)v;
        if(b.getId() == R.id.provider_mode_button)
        {
            Data.SetProviderAppMode(getApplicationContext());
            Intent intent = new Intent(this, ProviderMenuActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Data.SetPatientAppMode(getApplicationContext());
            MainViewController.FireRootActivity(this);
            finish();
        }

    }


}
