package com.newpush.YourPractice;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.view.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectPracticeActivity extends DefaultActivity {
    SimpleAdapter menuAdapter;
    ListView menu;
    ArrayList<Map<String, String>> menuContents;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Skin.applyActivityBackground(this);
        setTitle(R.string.title_select_practice);
        menu = (ListView) findViewById(R.id.menu);

        menuContents = new ArrayList<Map<String, String>>();
        menuAdapter = new SimpleAdapter(this,
                menuContents,
                android.R.layout.simple_list_item_2,
                new String[] {"Practice", "Location" },
                new int[] {android.R.id.text1, android.R.id.text2 });

        menu.setAdapter(menuAdapter);
        setupMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result =  super.onCreateOptionsMenu(menu);
        setHomeVisibility(false);
        return result;
    }

    protected void setupMenu() {
        @SuppressWarnings("unchecked")
        ArrayList<HashMap<String, String>> practices = (ArrayList<HashMap<String, String>>)extras.getSerializable("practices");
        for (HashMap<String, String> practice : practices) {
            Map<String, String> practiceMenuItem = new HashMap<String, String>(3);
            practiceMenuItem.put("Practice", practice.get("name"));
            practiceMenuItem.put("Location", practice.get("location"));
            practiceMenuItem.put("Feed", practice.get("feed"));
            practiceMenuItem.put("designPack", practice.get("designPack"));
            menuContents.add(practiceMenuItem);
        }
        menuAdapter.notifyDataSetChanged();

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String feedRoot = menuContents.get(position).get("Feed");
                String designPack = menuContents.get(position).get("designPack");
                startDownload(feedRoot, designPack);
            }
        });
    }
}