package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateOperations {
    public static Date parseDate(String dateFromAPI) throws ParseException {
        Log.d("Date conversion", "Date string to parse: " + dateFromAPI);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.parse(dateFromAPI);
    }

    public static String formatDate(Date date, Context context) {
        return android.text.format.DateFormat.getLongDateFormat(context).format(date) + " " +
                android.text.format.DateFormat.getTimeFormat(context).format(date);
    }

    public static String reformatToLocal(String dateFromAPI, Context context)
            throws ParseException {
        return formatDate(parseDate(dateFromAPI), context);
    }

 }
