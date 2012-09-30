package com.newpush.greenwoodpediatrics.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.jsoup.nodes.Element;

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
	
	public Hashtable<String, String> getFullInfo(Integer position) {
		Hashtable<String, String> articleinfo = new Hashtable<String, String>();
		Element article = doc.select("PW_Medical_Article:eq(" + position.toString() + ")").first();
		String title = article.select("ArticleTitle").text();
		String datereviewed = article.select("DateReviewed").text();
		String dateupdated = article.select("DateUpdated").text();
		String author = article.select("author").text();
		String topportion = article.select("TopPortion").text();
		String call911 = article.select("Call911").text();
		String callnow = article.select("CallNow").text();
		String call24 = article.select("Call24").text();
		String callweekday = article.select("CallWeekday").text();
		String parentcare = article.select("ParentCare").text();
		String bottomportion = article.select("BottomPortion").text();
		String offsitelink = article.select("OffsiteLink").text();
		String copyright = article.select("Copyright").text();
		
		articleinfo.put("title", title);
		articleinfo.put("datereviewed", datereviewed);
		articleinfo.put("dateupdated", dateupdated);
		articleinfo.put("author", author);
		articleinfo.put("topportion", topportion);
		articleinfo.put("call911", call911);
		articleinfo.put("callnow", callnow);
		articleinfo.put("call24", call24);
		articleinfo.put("callweekday", callweekday);
		articleinfo.put("parentcare", parentcare);
		articleinfo.put("bottomportion", bottomportion);
		articleinfo.put("offsitelink", offsitelink);
		articleinfo.put("copyright", copyright);
		return articleinfo;
	}
}