package com.rootnetapp.rootnetintranet.data.local.db.workflow.converters;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringDateConverter {
    private static final String TAG = "StringDateConverter";

    @TypeConverter
    public static long stringDateToTimestamp(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssZ",
                Locale.getDefault());
        try {
            Date convertedDate = dateFormat.parse(date);
            return convertedDate.getTime();
        } catch (ParseException e) {
            Log.d(TAG, "StringDateToTimestamp: e = " + e.getMessage());
        }
        return 0;
    }

    @TypeConverter
    public static String timestampToStringDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssZ",
                Locale.getDefault());
        try {
            return dateFormat.format(date);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "TimestampToStringDate: e = " + e.getMessage());
        }
        return "";
    }
}
