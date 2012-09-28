package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;

public class OfficeInfoParser extends DefaultParser {
	public OfficeInfoParser(String XMLpath) {
		super(XMLpath);
	}

	public OfficeInfoParser(Context appContext) {
		super(appContext.getFilesDir().getAbsolutePath() + "/office.xml");
	}

	public ArrayList<String> getTitles() {
		return Parse("messagetitle");
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
