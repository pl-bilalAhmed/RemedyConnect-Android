package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;

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
}
