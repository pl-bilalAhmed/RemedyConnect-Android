package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;

public class PracticeNewsParser extends DefaultParser {

	public PracticeNewsParser(String XMLpath) {
		super(XMLpath);
	}

	public PracticeNewsParser(Context appContext) {
		super(appContext.getFilesDir().getAbsolutePath() + "/news.xml");
	}

	public ArrayList<String> getTitles() {
		return Parse("NewsTitle");
	}

	public Hashtable<String, String> getFullInfo(int index) {
		String title = ParseSingle("CMS_News:eq(" + index + ") NewsTitle");
		String text = ParseSingle("CMS_News:eq(" + index + ") NewsText");

		Hashtable<String, String> result = new Hashtable<String, String>();
		result.put("title", title);
		result.put("text", text);

		return result;
	}

}
