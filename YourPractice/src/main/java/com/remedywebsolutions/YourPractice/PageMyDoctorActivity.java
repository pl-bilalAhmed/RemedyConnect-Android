package com.remedywebsolutions.YourPractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PageMyDoctorActivity extends DefaultActivity implements OnClickListener {

    public static final String pageMyDoctorUrl = "http://greenwood.pagemydoctor.net";
    public static final String PREFS_NAME = "prefs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstTime = settings.getBoolean("firstPageMyDoctor", true);

        setContentView(R.layout.activity_page_my_doctor);
        makeTextViewLinksClickable(R.id.footerTextView);
        String title = extras.getString("title");
        setTitle(title);
        Button open = (Button) this.findViewById(R.id.openButton);
        open.setOnClickListener(this);

        if (firstTime) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstPageMyDoctor", false);
            editor.commit();
        } else {
            openSite();
        }
    }

    public void onClick(View v) {
        this.openSite();
    }

    public void openSite() {
        Intent i = getPageMyDoctorIntent();
        startActivity(i);
    }

    public static Intent getPageMyDoctorIntent() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(pageMyDoctorUrl));
        return i;
    }
}
