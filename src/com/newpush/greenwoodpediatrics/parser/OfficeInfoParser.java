package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import com.newpush.greenwoodpediatrics.StringItemWithIndex;

import android.content.Context;

public class OfficeInfoParser extends DefaultParser {
	public OfficeInfoParser(String XMLpath) {
		super(XMLpath);
	}

	public OfficeInfoParser(Context appContext) {
		super(appContext.getFilesDir().getAbsolutePath() + "/office.xml");
	}

	private Boolean titleShouldBeFilteredOut(String title) {
		ArrayList<String> titlesToFilter = new ArrayList<String>();
		titlesToFilter.add("Welcome Message");
		titlesToFilter.add("Insurance");
		return titlesToFilter.contains(title);
	}
	
	public ArrayList<StringItemWithIndex> getTitles() {
		ArrayList<String> titles = Parse("messagetitle");
		ArrayList<StringItemWithIndex> titlesWithIndex = new ArrayList<StringItemWithIndex>();
		int i = 0;
		for (String title : titles) {
			if (!titleShouldBeFilteredOut(title)) {
				StringItemWithIndex titleWithVisibility = new StringItemWithIndex();
				titleWithVisibility.value = title;
				titleWithVisibility.index = i;
				titlesWithIndex.add(titleWithVisibility);
			}
			++i;
		}
		return titlesWithIndex;
	}

	public Hashtable<String, String> getFullInfo(int index) {
		String title = ParseSingle("pw_Message:eq(" + index + ") messageTitle");
		String message = ParseSingle("pw_Message:eq(" + index + ") StandardMessage");

		Hashtable<String, String> result = new Hashtable<String, String>();
		result.put("title", title);
		result.put("message", message);

		return result;
	}
}
