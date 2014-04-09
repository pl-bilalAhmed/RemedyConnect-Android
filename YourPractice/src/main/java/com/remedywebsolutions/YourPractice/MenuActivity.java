package com.remedywebsolutions.YourPractice;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.remedywebsolutions.YourPractice.parser.MainParser;

import java.util.ArrayList;

public class MenuActivity extends DefaultActivity {
    protected ArrayAdapter<String> menuadapter;
    protected ListView menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("Menu");
        setContentView(R.layout.activity_menu);
        menu = (ListView) findViewById(R.id.menu);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.default_header_listitem, menu, false);
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.default_footer_listitem, menu, false);

        for (int i = 0; i < header.getChildCount(); ++i) {
            View v = header.getChildAt(i);
            if (v.getId() == R.id.titleTextView) {
                ((TextView)v).setTypeface(Skin.menuHeaderFont(this));
            }
        }

        menu.addHeaderView(header, null, false);
        //menu.addFooterView(footer, null, false);

        // Let's use a modified ArrayAdapter so we can use a custom font on the list
        menuadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()) {
            public View getView(int pos, View convertView, android.view.ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
                }
                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                tv.setText(this.getItem(pos));
                tv.setTypeface(Skin.menuFont(getApplicationContext()));
                return v;
            }
        };
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
                                menuadapter.getItem(position + MENU_ITEMS_SHIFT), 0); // -1 everywhere because of the header element
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
