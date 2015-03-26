package com.remedywebsolutions.YourPractice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.readystatesoftware.viewbadger.BadgeView;
import com.remedywebsolutions.YourPractice.parser.MainParser;

import java.util.ArrayList;

public class ProviderMenuActivity extends DefaultActivity implements View.OnClickListener {
    ArrayList<Button> menuButtons;
    ArrayList<String> menuButtonTexts;
    ArrayList<String> feeds;
    ArrayList<String> externalLinks;
    Integer NUMBER_OF_BUTTONS = 6;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Main menu");
        setContentView(R.layout.activity_provider_menu);
        Skin.applyMainMenuBackground(this);
        Skin.applyProviderMenuButtons(this);
        Skin.applyThemeLogo(this, true);
        if (extras != null && extras.getBoolean("isRoot")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
        menuButtons = new ArrayList<Button>();

        menuButtons.add((Button) this.findViewById(R.id.menuButton3));
        menuButtons.add((Button) this.findViewById(R.id.menuButton4));

        for (Button button : menuButtons) {
            button.setOnClickListener(this);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                // In this app, we can just simple force the Up button to behave the same way as the Back.
              //  this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        View target =  findViewById(R.id.imageView2);
        BadgeView badge = new BadgeView(this, target);
        badge.setText("12");
        badge.setTextSize(16);
        badge.setBadgeMargin(20);
        badge.show();
        super.onStart();

    }

    void onClickFireActivity(Integer index) {
      setTitle(Integer.toString(index));
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.menuButton3:

                Intent intent = new Intent(this, SecureCallListActivity.class);
                startActivity(intent);
                break;
            case R.id.menuButton4:
                onClickFireActivity(3);
                MainViewController.FireBrowser(this, "https://admin.remedyoncall.com");

                break;

        }
    }
}
