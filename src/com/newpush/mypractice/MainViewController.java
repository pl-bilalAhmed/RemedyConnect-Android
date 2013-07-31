package com.newpush.mypractice;

import android.content.Context;
import android.content.Intent;
import com.newpush.mypractice.parser.MainParser;

import java.util.ArrayList;
import java.util.HashMap;

public class MainViewController {
    public static void FireActivity(Context context, String parsePoint, String title) {
        String startPath = context.getFilesDir().getAbsolutePath() + "/";
        MainParser parser = new MainParser(startPath + parsePoint);
        if (parser.isMenu()) {
            Boolean isRoot = (parsePoint == "index.xml");
            Intent intent;
            if (isRoot) {
                intent = new Intent(context, MainMenuActivity.class);
            }
            else {
                intent = new Intent(context, MenuActivity.class);
            }
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
            intent.putExtra("isRoot", isRoot);

            context.startActivity(intent);
        }
        if (parser.isPage()) {
            Intent intent = new Intent(context, PageActivity.class);

            HashMap<String, String> page = parser.getPage();

            intent.putExtra("text", page.get("text"));
            intent.putExtra("title", page.get("title"));
            intent.putExtra("isRoot", parsePoint == "index.xml");

            context.startActivity(intent);
        }
        if (parser.isArticleSet()) {
            Intent intent = new Intent(context, ArticleSetActivity.class);
            intent.putExtra("articleTitles", parser.getArticleSetTitles());
            intent.putExtra("title", title);
            intent.putExtra("isRoot", parsePoint == "index.xml");
            intent.putExtra("xml", startPath + parsePoint);

            context.startActivity(intent);
        }
    }

    public static void FireActivity(DefaultActivity activity, String parsePoint) {
        FireActivity(activity, parsePoint, activity.getString(R.string.welcome));
    }

    public static void FireRootActivity(DefaultActivity activity) {
        FireActivity(activity, "index.xml");
    }
}
