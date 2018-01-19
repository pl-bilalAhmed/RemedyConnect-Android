package com.remedywebsolutions.YourPractice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.remedywebsolutions.YourPractice.parser.MainParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainViewController {


    public static Intent GetIntent(Context context, String parsePoint, String title, int intentFlags) {
        File filesDir = context.getFilesDir();
        assert filesDir != null;
        String startPath = filesDir.getAbsolutePath() + "/";
        showLog("StartPoint = " + startPath + " ----- Parse Point = " + parsePoint);
        showLog("" + startPath + "" + parsePoint);
        MainParser parser = new MainParser(startPath + parsePoint);
        Intent intent = null;
        if (parser.isMenu()) {
            showLog("Menu Opened");
            Boolean isRoot = (parsePoint.equals("index.xml"));
            if (isRoot) {
                intent = new Intent(context, MainMenuActivity.class);
            } else {
                intent = new Intent(context, MenuActivity.class);
            }

            if (intentFlags != 0) {
                intent.setFlags(intentFlags);
            }

            ArrayList<HashMap<String, String>> menuItems = parser.getMenu();
            ArrayList<String> items = new ArrayList<String>();
            ArrayList<String> feeds = new ArrayList<String>();
            ArrayList<String> externalLinks = new ArrayList<String>();
            for (HashMap<String, String> menuItem : menuItems) {
                items.add(menuItem.get("name"));
                feeds.add(menuItem.get("feed"));
                externalLinks.add(menuItem.get("externalLink"));
            }
            //   showLog("isRoot = " + isRoot + " Feed at 0 = " + feeds.get(0) + " Menu items at 0 =" + items.get(0));
            intent.putExtra("menuitems", items);
            intent.putExtra("feeds", feeds);
            intent.putExtra("externalLinks", externalLinks);
            intent.putExtra("title", title);
            intent.putExtra("isRoot", isRoot);
        } else if (parser.isPage()) {
            showLog("Is Page ");
            //   showLog("**********IsPageActivity*********");
            intent = new Intent(context, PageActivity.class);
            if (intentFlags != 0) {
                intent.setFlags(intentFlags);
            }

            HashMap<String, String> page = parser.getPage();
            intent.putExtra("text", page.get("text"));
            intent.putExtra("title", page.get("title"));
            intent.putExtra("isRoot", parsePoint.equals("inex.xml"));

        } else if (parser.isArticleSet()) {
            showLog("Article Set Opened");
            //     showLog("**********isArticleSet*********");
            intent = new Intent(context, ArticleSetActivity.class);
            if (intentFlags != 0) {
                intent.setFlags(intentFlags);
            }
            intent.putExtra("articleTitles", parser.getArticleSetTitles());
            intent.putExtra("title", title);
            intent.putExtra("isRoot", parsePoint.equals("index.xml"));
            intent.putExtra("xml", startPath + parsePoint);
            //    showLog("XML = " + startPath + parsePoint + " is Root= " + parsePoint.equals("index.xml"));
        }

        //  showLog("**********MainViewController ENDS*********");
        return intent;
    }

    public static void FireActivity(Context context, String parsePoint, String title, int intentFlags) {
        Intent intent = GetIntent(context, parsePoint, title, intentFlags);
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    public static void FireActivity(DefaultActivity activity, String parsePoint, int intentFlags) {
        FireActivity(activity, parsePoint, activity.getString(R.string.welcome), intentFlags);
    }


    public static Intent GetRootIntent(Context context) {
        return GetIntent(context, "index.xml", context.getString(R.string.welcome), 0);
    }

    public static void FireRootActivity(DefaultActivity activity) {
        FireActivity(activity, "index.xml", 0);
    }

    public static void FireRootActivity(DefaultActivity activity, int intentFlags) {
        FireActivity(activity, "index.xml", intentFlags);
    }

    public static void FireBrowser(Context context, String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        context.startActivity(browserIntent);
    }


    public static final String TAG = "PentaDebug";
    public static final boolean isBeta = true;

    public static void showLog(String msg) {
        if (isBeta) {
            Log.i(TAG, msg);
        }
    }


}
