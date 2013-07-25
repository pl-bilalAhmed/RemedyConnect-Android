package com.newpush.greenwoodpediatrics;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectPracticeActivity extends DefaultActivity {
    ArrayAdapter<String> menuAdapter;
    ListView menu;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle(R.string.title_select_practice);
        menu = (ListView) findViewById(R.id.menu);
        List<Map<String, String>> menuContents = new ArrayList<Map<String, String>>();

        // Some sample data
        Map<String, String> sampleData = new HashMap<String, String>(2);
        sampleData.put("Practice", "My Practice");
        sampleData.put("Location", "Somewhere else...");
        menuContents.add(sampleData);

        SimpleAdapter menuAdapter = new SimpleAdapter(this, menuContents,
                android.R.layout.simple_list_item_2,
                new String[] {"Practice", "Location" },
                new int[] {android.R.id.text1, android.R.id.text2 });

        menu.setAdapter(menuAdapter);
    }
}