package com.newpush.mypractice;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.newpush.mypractice.DefaultActivity;
import com.newpush.mypractice.parser.MainParser;

import java.util.ArrayList;

public class MainMenuActivity extends DefaultActivity implements View.OnClickListener {
    ArrayList<Button> menuButtons;
    ArrayList<String> menuButtonTexts;
    ArrayList<String> feeds;
    ArrayList<String> externalLinks;
    Integer NUMBER_OF_BUTTONS = 6;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Skin.applyMainMenuBackground(this);
        Skin.applyMainMenuButtons(this);
        Skin.applyThemeLogo(this, true);
        if (extras.getBoolean("isRoot")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
        menuButtons = new ArrayList<Button>();
        menuButtons.add((Button) this.findViewById(R.id.menuButton1));
        menuButtons.add((Button) this.findViewById(R.id.menuButton2));
        menuButtons.add((Button) this.findViewById(R.id.menuButton3));
        menuButtons.add((Button) this.findViewById(R.id.menuButton4));
        menuButtons.add((Button) this.findViewById(R.id.menuButton5));
        menuButtons.add((Button) this.findViewById(R.id.menuButton6));
        for (Button button : menuButtons) {
            button.setOnClickListener(this);
        }
        menuButtonTexts = extras.getStringArrayList("menuitems");
        feeds = extras.getStringArrayList("feeds");
        externalLinks = extras.getStringArrayList("externalLinks");
        Integer index = 0;
        for (Button button : menuButtons) {
            button.setText(menuButtonTexts.get(index));
            ++index;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // We have to do this first, because the DefaultActivity will inflate the menu in:
        boolean result = super.onCreateOptionsMenu(menu);
        MenuItem more  = menu.findItem(R.id.more);
        if (more != null) {
            SubMenu subMenu = more.getSubMenu();
            if (menuButtonTexts.size() > NUMBER_OF_BUTTONS) {
                for (Integer index = NUMBER_OF_BUTTONS; index < menuButtonTexts.size(); ++index) {
                    subMenu.add(Menu.NONE, index, Menu.FIRST + index, menuButtonTexts.get(index))
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
            }
        }
        setHomeVisibility(false);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer id = item.getItemId();
        if (id >= NUMBER_OF_BUTTONS && id < menuButtonTexts.size()) {
            onClickFireActivity(id);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void onClickFireActivity(Integer index) {
        if (!feeds.get(index).isEmpty()) {
            String localPath = MainParser.subFeedURLToLocal(
                    feeds.get(index),
                    Data.GetFeedRoot(this));
            MainViewController.FireActivity(this,
                    localPath,
                    menuButtonTexts.get(index));
        }
        else {
            if (!externalLinks.get(index).isEmpty()) {
                MainViewController.FireBrowser(this, externalLinks.get(index));
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuButton1:
                onClickFireActivity(0);
                break;
            case R.id.menuButton2:
                onClickFireActivity(1);
                break;
            case R.id.menuButton3:
                onClickFireActivity(2);
                break;
            case R.id.menuButton4:
                onClickFireActivity(3);
                break;
            case R.id.menuButton5:
                onClickFireActivity(4);
                break;
            case R.id.menuButton6:
                onClickFireActivity(5);
                break;
        }
    }
}