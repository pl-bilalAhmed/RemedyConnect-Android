package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;

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

}
