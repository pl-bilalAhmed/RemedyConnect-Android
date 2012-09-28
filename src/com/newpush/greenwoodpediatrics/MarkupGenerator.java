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
}
