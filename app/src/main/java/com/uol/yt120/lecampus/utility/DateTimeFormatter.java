package com.uol.yt120.lecampus.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {

    private final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final String DATE_NO_TIME_PATTERN = "yyyy-MM-dd";
    private final String TIME_ONLY_PATERN = "HH:mm";
    private final String UNI_DATE_PATTERN = "dd/MMM/yyyy";

    private Locale zoneLocale = Locale.UK;

    public String formatDateToString(Date date, String pattern) {
        DateFormat dateFormat;
        switch (pattern) {
            case "default":
                dateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
                break;
            case "no_time":
                dateFormat = new SimpleDateFormat(DATE_NO_TIME_PATTERN);
                break;
            case "uni_date":
                dateFormat = new SimpleDateFormat(UNI_DATE_PATTERN);
                break;
            default:
                dateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
                break;
        }
        return dateFormat.format(date);

    }

    public Date parseStringToDate(String string, String pattern) throws ParseException {
        DateFormat dateFormat;
        switch (pattern) {
            case "default":
                dateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
                break;
            case "no_time":
                dateFormat = new SimpleDateFormat(DATE_NO_TIME_PATTERN);
                break;
            case "uni_date":
                dateFormat = new SimpleDateFormat(UNI_DATE_PATTERN);
                break;
            default:
                dateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
                break;
        }
        return dateFormat.parse(string);
    }

    public String getDEFAULT_PATTERN() {
        return DEFAULT_PATTERN;
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
