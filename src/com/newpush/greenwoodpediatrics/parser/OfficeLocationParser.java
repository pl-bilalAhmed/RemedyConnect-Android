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
	
	public ArrayList<Hashtable<String, String>> getFullInfo(Context context) {
		ArrayList<Hashtable<String, String>> results = new ArrayList<Hashtable<String, String>>();
		
		for (Element location: doc.select("pw_Office_location")) {
			String name = location.select("locationname").text();
			String address1 = location.select("address1").text();
			String address2 = location.select("address2").text();
			String city = location.select("city").text();
			String state = location.select("state").text();
			String zipcode = location.select("zipcode").text();
			
			String contactnumbers = location.select("contactnumbers").text();
			String dailyhours = location.select("dailyhours").text();
			String custommessage = location.select("custommessage").text();
			
			Hashtable<String, String> result = new Hashtable<String, String>();
			result.put("name", name);
			result.put("address", MarkupGenerator.formatAddress(address1, address2, city, state, zipcode));
			result.put("contactnumbers", contactnumbers);
			result.put("dailyhours", dailyhours);
			result.put("custommessage", custommessage);
			
			results.add(result);	
		}
	 	
		return results;
	}
}
