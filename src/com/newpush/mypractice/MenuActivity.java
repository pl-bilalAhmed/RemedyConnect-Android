package com.newpush.mypractice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.newpush.mypractice.parser.MainParser;

import java.util.ArrayList;

public class MenuActivity extends DefaultActivity {
    protected ArrayAdapter<String> menuadapter;
    protected ListView menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menu = (ListView) findViewById(R.id.menu);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.default_header_listitem, menu, false);
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.default_footer_listitem, menu, false);
        menu.addHeaderView(header, null, false);
        menu.addFooterView(footer, null, false);
        menuadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        menu.setAdapter(menuadapter);

        Skin.applyThemeLogo(this);
        Skin.applyActivityBackground(this);

        setTitle(extras.getString("title"));
        setupMenu();
        makeTextViewLinksClickable(R.id.footerTextView);
        if (extras.getBoolean("isRoot")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }

    protected void setupMenu() {
        if (menuadapter.getCount() < 2) {
            for (String s : extras.getStringArrayList("menuitems")) {
                menuadapter.add(s);
            }
            menu.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int MENU_ITEMS_SHIFT = -1;

                    ArrayList<String> feeds = extras.getStringArrayList("feeds");
                    ArrayList<String> externalLinks = extras.getStringArrayList("externalLinks");
                    String feed = feeds.get(position + MENU_ITEMS_SHIFT);
                    if (feed != null && !feed.isEmpty()) {
                        String localPath = MainParser.subFeedURLToLocal(
                                feed,
                                Data.GetFeedRoot(view.getContext()));
                        MainViewController.FireActivity(view.getContext(),
                                localPath,
                                menuadapter.getItem(position + MENU_ITEMS_SHIFT)); // -1 everywhere because of the header element
                    }
                    else {
                        if (externalLinks.get(position + MENU_ITEMS_SHIFT) != null) {
                            MainViewController.FireBrowser(view.getContext(),
                                    externalLinks.get(position + MENU_ITEMS_SHIFT));
                        }
                    }
                }
            });
        }
    }
}
