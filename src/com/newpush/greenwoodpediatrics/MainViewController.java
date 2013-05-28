package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;

import com.newpush.greenwoodpediatrics.parser.MainParser;

public class MainViewController {
	public static void FireActivity(DefaultActivity activity, String parsePoint, String title) {
		String startPath = activity.getFilesDir().getAbsolutePath() + "/";
		MainParser parser = new MainParser(startPath + parsePoint);
		if (parser.isMenu()) {
			ArrayList<HashMap<String, String>> menuItems = parser.getMenu();
			ArrayList<String> items = new ArrayList<String>();
			ArrayList<String> feeds = new ArrayList<String>();
			for (HashMap<String, String> menuItem : menuItems) {
				items.add(menuItem.get("name"));
				feeds.add(menuItem.get("feed"));
			}
			
			Intent intent = new Intent(activity, MenuActivity.class);
			intent.putExtra("isRoot", parsePoint == "index.xml");
			intent.putExtra("title", title);
			intent.putExtra("menuitems", items);
			intent.putExtra("feeds", feeds);
			
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
	}
	
	public static void FireActivity(DefaultActivity activity, String parsePoint) {
		FireActivity(activity, parsePoint, activity.getString(R.string.welcome));
	}
}
