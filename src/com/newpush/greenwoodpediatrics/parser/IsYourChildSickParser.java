package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
	
	private String renameItem(String original) {
		if (original.equals("Health Nuts Media Videos")) {
			return "Videos For Kids";
		}
		if (original.equals("Pediatric Illness/Symptoms")) {
			return "Is Your Child Sick?";
		}
		return original;
	}
	
	private Boolean itemShouldBeFilteredOut(String item) {
		ArrayList<String> itemsToFilter = new ArrayList<String>();
		itemsToFilter.add("Special Needs");
		itemsToFilter.add("Family Illness/Symptoms");
		return itemsToFilter.contains(item);
	} 
	
	public LinkedHashMap<String, String> getCategories() {
		LinkedHashMap<String, String> categories_with_ids = new LinkedHashMap<String, String>();
		Elements categories = doc.select("pw_medical_category");
		for (Element category : categories) {
			String id = category.select("categoryid").text();
			String name = category.select("categoryname").text();
			if (!itemShouldBeFilteredOut(name)) {		
				categories_with_ids.put(id, renameItem(name));
			}
		}
		
		return categories_with_ids;
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
