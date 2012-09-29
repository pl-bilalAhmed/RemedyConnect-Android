package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.newpush.greenwoodpediatrics.Data;

import android.content.Context;

public class IsYourChildSickParser extends DefaultParser {

	public IsYourChildSickParser(String XMLpath) {
		super(XMLpath);
	}
	
	public IsYourChildSickParser(Context appContext) {
		super(appContext.getFilesDir().getAbsolutePath() + "/iycs.xml");
	}

	public HashMap<String, String> getSubFeeds() {
		HashMap<String, String> subfeed_urls = new HashMap<String, String>();
		Elements subfeeds = doc.select("nextfeed");
		for (Element subfeed : subfeeds) {
			String feed_id = subfeed.text();
			String iycs_root = Data.preloadDataFiles.get("iycs.xml");
			subfeed_urls.put("iycs-" + feed_id + ".xml", iycs_root + "/" + feed_id);
		}
		return subfeed_urls;
	}

}
