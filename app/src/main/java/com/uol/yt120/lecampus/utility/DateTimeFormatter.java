package com.uol.yt120.lecampus.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {

    private String datePattern = "yyyy-MM-dd HH:mm:ss";
    //private String datePatternUni = "yyyy-MM-dd HH:mm:ss";
    private Locale zoneLocale = Locale.UK;

    public String formatDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(datePattern, zoneLocale);
        String result = dateFormat.format(date);
        return result;
    }

//    public String formatDateToStringUni(Date date) {
//        DateFormat dateFormat = new SimpleDateFormat(datePatternUni, zoneLocale);
//        String result = dateFormat.format(date);
//        return result;
//    }

    public Date parseStringToDate(String string) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(datePattern, zoneLocale);
        Date result = dateFormat.parse(string);
        return result;
    }

//    public Date parseStringToDateUni(String string) throws ParseException {
//        DateFormat dateFormat = new SimpleDateFormat(datePatternUni, zoneLocale);
//        Date result = dateFormat.parse(string);
//        return result;
//    }



    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public Locale getZoneLocale() {
        return zoneLocale;
    }

    public String getZoneLocaleInString() {
        String locale = zoneLocale.toString();
        return locale;
    }

    public void setZoneLocale(Locale zoneLocale) {
        this.zoneLocale = zoneLocale;
    }

}
