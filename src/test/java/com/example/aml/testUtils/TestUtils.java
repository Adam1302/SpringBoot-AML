package com.example.aml.testUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestUtils {
    public static Date parseDateOrGetDefault(String dateAsString) {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));
            date = sdf.parse(dateAsString);
        } catch (ParseException parseException) {
            date = new Date(1577836800000L);
        }
        Logger.getAnonymousLogger().log(Level.WARNING, "dateAsString: " + dateAsString + " but date: " + date.toString());
        return date;
    }
}
