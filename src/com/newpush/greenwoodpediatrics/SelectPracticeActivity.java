package com.newpush.greenwoodpediatrics;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.newpush.greenwoodpediatrics.parser.MainParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectPracticeActivity extends DefaultActivity {
    SimpleAdapter menuAdapter;
    ListView menu;
    ArrayList<Map<String, String>> menuContents;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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

    protected void setupMenu() {
        // Some sample data
        ArrayList<HashMap<String, String>> practices = (ArrayList<HashMap<String, String>>)extras.getSerializable("practices");
        for (HashMap<String, String> practice : practices) {
            Map<String, String> sampleData = new HashMap<String, String>(2);
            sampleData.put("Practice", practice.get("name"));
            sampleData.put("Location", "Somewhere else...?");
            menuContents.add(sampleData);
        }

        menuAdapter.notifyDataSetChanged();

        /*

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> feeds = extras.getStringArrayList("feeds");
                String localPath = MainParser.subFeedURLToLocal(feeds.get(position));
                MainViewController.FireActivity(SelectPracticeActivity.this,
                        localPath,
                        menuAdapter.getItem(position - 1)); // -1 everywhere because of the header element
            }
        });*/
    }
}