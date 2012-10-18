package com.newpush.greenwoodpediatrics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PageMyDoctorActivity extends DefaultActivity implements OnClickListener {

	public static final String pageMyDoctorUrl = "http://greenwood.pagemydoctor.net";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_my_doctor);
        makeTextViewLinksClickable(R.id.footerTextView);
        String title = extras.getString("title");
    	setTitle(title);
    	Button open = (Button) this.findViewById(R.id.openButton);
    	open.setOnClickListener(this);
    }

    public void onClick(View v) {
    	this.openSite();
    }

    public void openSite() {
    	String url = "http://www.example.com";
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(pageMyDoctorUrl));
    	startActivity(i);
    }


}
