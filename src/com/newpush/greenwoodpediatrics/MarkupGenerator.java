package com.newpush.greenwoodpediatrics;

import java.text.DateFormat;
import java.util.Date;

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
}
