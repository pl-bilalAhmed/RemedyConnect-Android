package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;

import android.content.Context;

public class IsYourChildSickSubParser extends DefaultParser {

	public IsYourChildSickSubParser(String XMLpath) {
		super(XMLpath);
	}
	
	public IsYourChildSickSubParser(Context appContext, Integer index) {
		super(appContext.getFilesDir().getAbsolutePath() + "/iycs-" + index.toString() + ".xml");
	}
	
	public ArrayList<String> getArticleTitles() {
		return Parse("ArticleTitle");
	}
}
