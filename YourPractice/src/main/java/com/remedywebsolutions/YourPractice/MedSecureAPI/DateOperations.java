package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Class for the common date operations for handling the dates from the API.
 */
public class DateOperations {
    /**
     * Parses the API datetime into a Date.
     *
     * We assume that the API always returns UTC timestamps.
     * @param dateFromAPI UTC timestamp.
     * @return The right Date with timezone properly set.
     * @throws ParseException
     */
    public static Date parseDate(String dateFromAPI) throws ParseException {
        Log.d("Date conversion", "Date string to parse: " + dateFromAPI);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.parse(dateFromAPI);
    }

    /**
     * Formats a date with the system's specified date and time display settings.
     * @param date The date to work on.
     * @param context Context for getting the system settings.
     * @return The properly formatted string representation of the given date.
     */
    public static String formatDate(Date date, Context context) {
        return android.text.format.DateFormat.getLongDateFormat(context).format(date) + " " +
                android.text.format.DateFormat.getTimeFormat(context).format(date);
    }

    /**
     * Formats a date for relative time displays.
     * @param date The The date to work on.
     * @return The properly formatted string representation of the given date.
     */
    public static String getRelativeTimeForTimeString(Date date) {
        return DateUtils.getRelativeTimeSpanString(date.getTime()).toString();
    }

    /**
     * Formats an API date string into a basic textual representation using the device's timezone.
     * @param dateFromAPI The date string from the API.
     * @param context The context for getting the system settings.
     * @return The properly formatted string representation of the given date.
     * @throws ParseException
     */
    public static String reformatToLocalBasic(String dateFromAPI, Context context) throws ParseException {
        return formatDate(parseDate(dateFromAPI), context);
    }

    /**
     * Formats an API date string into a relative textual representation using the device's timezone.
     * @param dateFromAPI The date string from the API.
     * @return The properly formatted string representation of the given date.
     * @throws ParseException
     */
    public static String reformatToLocalRelative(String dateFromAPI) throws ParseException {
        return getRelativeTimeForTimeString(parseDate(dateFromAPI));
    }

 }
