package com.antplay.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateFormatterHelper {
    public static Locale IND = new Locale("en", "IN");

    public static String DATE_FORMAT_ONE = "yyyy.MM.dd G 'at' HH:mm:ss z";  //2001.07.04 AD at 12:08:56 PDT
    public static String DATE_FORMAT_TWO = "EEE, MMM d, yyyy";          //Wed, Jul 4, '01
    public static String DATE_FORMAT_THREE = "yyyyy.MMMM.dd GGG hh:mm aaa"; //02001.July.04 AD 12:08 PM
    public static String DATE_FORMAT_FOUR = "EEE, d MMM yyyy HH:mm:ss Z"; //Wed, 4 Jul 2001 12:08:56 -0700
    public static String DATE_FORMAT_FIVE = "EEE MMM dd, yyyy"; //
    public static String DATE_FORMAT_SIX = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FORMAT_SEVEN = "dd-MMM-yyyy h:mm a";

    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, IND);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, IND);

        String str = null;

        try {
            Date date = inputFormat.parse(time);
            assert date != null;
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDate(String date,String outputDateFormat) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        //String outputPattern = "dd-MMM-yyyy h:mm a";
        String outputPattern = outputDateFormat;

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, IND);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, IND);

        String strDate = null;

        try {
            Date dateNew = inputFormat.parse(date);
            assert dateNew != null;
            strDate = outputFormat.format(dateNew);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public static long getDifferenceOfTwoDates( String oldDate, String newDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatterHelper.DATE_FORMAT_FIVE, IND);
        try {
            return TimeUnit.DAYS.convert(inputFormat.parse(newDate).getTime() - inputFormat.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
