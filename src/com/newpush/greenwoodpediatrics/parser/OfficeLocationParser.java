package com.newpush.greenwoodpediatrics.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.jsoup.nodes.Element;

import android.content.Context;

import com.newpush.greenwoodpediatrics.MarkupGenerator;
import com.newpush.greenwoodpediatrics.R;


public class OfficeLocationParser extends DefaultParser {

	public OfficeLocationParser(String XMLpath) {
		super(XMLpath);
	}
	
	public OfficeLocationParser(Context appContext) {
		super(appContext.getFilesDir().getAbsolutePath() + "/location.xml");
	}
	
	public ArrayList<String> getTitles() {
		return Parse("locationname");
	}
	
	/*
	public Hashtable<String, String> getFullInfo(int index, Context context) {
		
	 	Element newsitem = doc.select("CMS_News:eq(" + index + ")").first();
	
		String title = newsitem.select("NewsTitle").text();
		String releasedate = newsitem.select("NewsReleaseDate").text();
		String summary = newsitem.select("NewsSummary").text();
		String text = newsitem.select("NewsText").text();
		
		Hashtable<String, String> result = new Hashtable<String, String>();
		
		SimpleDateFormat dateformatter = new SimpleDateFormat();
		dateformatter.applyPattern("MM/dd/yyyy KK:mm:ss aa");
		
		result.put("title", title);
		
		// @TODO: This will use the locale time zone. That should be OK, but I'll keep this notice here.
		Date releasedate_processed;
		try {
			
			releasedate_processed = dateformatter.parse(releasedate);
			result.put("releasedate", MarkupGenerator.formatDate(releasedate_processed, context, context.getString(R.string.news_item_release_date_label)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result.put("summary", summary);
		result.put("text", text);
	
		return result;
		
	}
	*/
}
