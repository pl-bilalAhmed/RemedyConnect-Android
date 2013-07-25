package com.newpush.greenwoodpediatrics;

import android.content.Intent;
import com.newpush.greenwoodpediatrics.parser.MainParser;

import java.util.ArrayList;
import java.util.HashMap;

public class MainViewController {
    public static void FireActivity(DefaultActivity activity, String parsePoint, String title) {
        String startPath = activity.getFilesDir().getAbsolutePath() + "/";
        MainParser parser = new MainParser(startPath + parsePoint);
        if (parser.isMenu()) {
            Intent intent = new Intent(activity, MenuActivity.class);

            ArrayList<HashMap<String, String>> menuItems = parser.getMenu();
            ArrayList<String> items = new ArrayList<String>();
            ArrayList<String> feeds = new ArrayList<String>();
            for (HashMap<String, String> menuItem : menuItems) {
                items.add(menuItem.get("name"));
                feeds.add(menuItem.get("feed"));
            }
            intent.putExtra("menuitems", items);
            intent.putExtra("feeds", feeds);
            intent.putExtra("title", title);
            intent.putExtra("isRoot", parsePoint == "index.xml");

            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        if (parser.isPage()) {
            Intent intent = new Intent(activity, PageActivity.class);

            HashMap<String, String> page = parser.getPage();

            intent.putExtra("text", page.get("text"));
            intent.putExtra("title", page.get("title"));
            intent.putExtra("isRoot", parsePoint == "index.xml");

            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        if (parser.isArticleSet()) {
            Intent intent = new Intent(activity, ArticleSetActivity.class);
            intent.putExtra("articleTitles", parser.getArticleSetTitles());
            intent.putExtra("title", title);
            intent.putExtra("isRoot", parsePoint == "index.xml");
            intent.putExtra("xml", startPath + parsePoint);

            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    public static void FireActivity(DefaultActivity activity, String parsePoint) {
        FireActivity(activity, parsePoint, activity.getString(R.string.welcome));
    }

    public static void FireRootActivity(DefaultActivity activity) {
        FireActivity(activity, "index.xml");
    }
}
