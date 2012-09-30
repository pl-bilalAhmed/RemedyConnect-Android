package com.newpush.greenwoodpediatrics;

import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;

import android.content.Context;

public class MarkupGenerator {
	public static String formatTitle(String title) {
		return "<h1>" + title + "</h1>";
	}
	
	public static String formatDate(Date date, Context context, String label) {
		DateFormat dateformat = android.text.format.DateFormat.getMediumDateFormat(context);
		DateFormat timeformat = android.text.format.DateFormat.getTimeFormat(context);
		return "<p class=\"releasedate\">" + label + " " + dateformat.format(date) + " - " + timeformat.format(date) + "</p>";
	}
	
	public static String formatAddress(String address1, String address2, String city, String state,	String zipcode) {
		return "<p class=\"address\">" + address1 + " " + address2 + "<br/>" + city + ", " + state + " " + zipcode;
	}
	
	public static String officeLocation(String full_address, String contactnumbers, String dailyhours, String custommessage) {
		return "<h2>Address</h2>" +
				full_address + 
				"<h2>Contact numbers</h2>" +
				contactnumbers +
				"<h2>Daily hours</h2>" +
				dailyhours +
				custommessage;
	}
	
	public static String formatIYCSArticle(Hashtable<String, String> article) {
		return formatTitle((String)article.get("title")) +
				(String)article.get("datereviewed") + 
				(String)article.get("dateupdated") +
				(String)article.get("author") +
				(String)article.get("topportion") +
				(String)article.get("call911") +
				(String)article.get("callnow") +
				(String)article.get("call24") +
				(String)article.get("callweekday") +
				(String)article.get("parentcare") +
				(String)article.get("bottomportion") +
				(String)article.get("copyright");
	}
}
