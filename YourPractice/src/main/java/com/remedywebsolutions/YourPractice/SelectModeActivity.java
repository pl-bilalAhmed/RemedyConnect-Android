package com.remedywebsolutions.YourPractice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class SelectModeActivity extends DefaultActivity  implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Select application mode");
        setContentView(R.layout.activity_mode_menu);
        //Skin.applyActivityBackground(this);
        setTitle(R.string.title_select_practice);

        View b1 = this.findViewById(R.id.button);
        b1.setOnClickListener(this);

        View b2 = this.findViewById(R.id.button10);
        b2.setOnClickListener(this);

    }


    public void onClick(View v) {
        Button b = (Button)v;
        setTitle(b.getText());

    }


}
