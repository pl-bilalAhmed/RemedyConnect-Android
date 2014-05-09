package com.remedywebsolutions.YourPractice.MedSecureAPI;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateOperations {
    public static Date parseDate(String dateFromAPI) throws ParseException {
        Log.d("Date conversion", "Date string to parse: " + dateFromAPI);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        return df.parse(dateFromAPI);
    }

    /**
     * Offsets the date based on the offset from the API.
     *
     * @param originalDate The original date to offset.
     * @param offsetFromAPIInHours The offset value compared to UTC.
     * @return The date in local time.
     */
    public static Date offsetDate(Date originalDate, int offsetFromAPIInHours) {
        Calendar c = Calendar.getInstance();
        c.setTime(originalDate);
        Log.d("Date conversion", "Original date: " + originalDate.toString());
        c.add(Calendar.HOUR, -1 * offsetFromAPIInHours);
        Log.d("Date conversion", "In UTC: " + c.toString());
        int localOffsetToUTC = TimeZone.getDefault().getOffset(originalDate.getTime());
        Log.d("Date conversion", "Local offset to UTC: " + localOffsetToUTC);
        c.add(Calendar.MILLISECOND, localOffsetToUTC);
        Log.d("Date conversion", "Converted to local time: " + c.toString());
        return c.getTime();
    }

    public static String formatDate(Date date, Context context) {
        return android.text.format.DateFormat.getLongDateFormat(context).format(date) + " " +
                android.text.format.DateFormat.getTimeFormat(context).format(date);
    }

    public static String reformatToLocal(String dateFromAPI, int offsetFromAPIHours, Context context)
            throws ParseException {
        // return formatDate(offsetDate(parseDate(dateFromAPI), offsetFromAPIHours), context);
        return formatDate(parseDate(dateFromAPI), context);
    }

 }
