package com.newpush.mypractice;

import android.content.Context;

import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;

public class MarkupGenerator {
    public static String formatTitle(String title) {
        return "<h1>" + title + "</h1>";
    }

    public static String formatDate(Date date, Context context, String label) {
        DateFormat dateformat = android.text.format.DateFormat.getMediumDateFormat(context);
        DateFormat timeformat = android.text.format.DateFormat.getTimeFormat(context);
        return "<p class=\"releasedate\">" + label + " " + dateformat.format(date) + " - " + timeformat.format(date) + "</p>";
    }

    public static String formatAddress(String address1, String address2, String city, String state, String zipcode) {
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
        return formatTitle(article.get("title")) +
                article.get("datereviewed") +
                article.get("dateupdated") +
                article.get("author") +
                article.get("topportion") +
                article.get("call911") +
                article.get("callnow") +
                article.get("call24") +
                article.get("callweekday") +
                article.get("parentcare") +
                article.get("bottomportion") +
                article.get("copyright");
    }

    public static String wrapHTMLWithStyle(String html) {
        String style = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />";
        return "<html><head>" + style + "</head><body>" + html + "</body>";
    }
}
