package com.remedywebsolutions.YourPractice;

import android.content.Intent;
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

public class ArticleSetActivity extends DefaultActivity {
    protected ArrayAdapter<String> adapter;
    protected ListView list;
    protected MainParser parser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportPhase("ArticleSet");
        setContentView(R.layout.activity_articleset);

        list = (ListView) findViewById(R.id.articleSetListView);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.default_header_listitem, list, false);
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.default_footer_listitem, list, false);

        for (int i = 0; i < header.getChildCount(); ++i) {
            View v = header.getChildAt(i);
            if (v.getId() == R.id.titleTextView) {
                ((TextView)v).setTypeface(Skin.menuHeaderFont(this));
            }
        }

        list.addHeaderView(header, null, false);
        list.addFooterView(footer, null, false);

        Skin.applyThemeLogo(this);
        Skin.applyActivityBackground(this);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)  {
            public View getView(int pos, View convertView, android.view.ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(android.R.layout.simple_list_item_1, null);
                }
                TextView tv = (TextView)v.findViewById(android.R.id.text1);
                tv.setText(this.getItem(pos));
                tv.setTypeface(Skin.menuFont(getApplicationContext()));
                return v;
            }
        };
        list.setAdapter(adapter);

        for (String listItem : extras.getStringArrayList("articleTitles")) {
            adapter.add(listItem);
        }

        parser = new MainParser(extras.getString("xml"));

        setTitleFromIntentBundle();
        attachLinks();
        makeTextViewLinksClickable(R.id.footerTextView);
    }

    protected void attachLinks() {
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int MENU_ITEMS_SHIFT = -1;

                Intent intent = new Intent(view.getContext(), PageActivity.class);
                // position-1 because of the header element
                intent.putExtra("text", parser.getArticleFromSet(position + MENU_ITEMS_SHIFT));
                intent.putExtra("title", adapter.getItem(position + MENU_ITEMS_SHIFT));
                intent.putExtra("isRoot", false);
                startActivity(intent);
            }
        });
    }
}
