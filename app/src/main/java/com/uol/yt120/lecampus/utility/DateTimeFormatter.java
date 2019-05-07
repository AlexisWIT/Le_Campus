package com.uol.yt120.lecampus.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeFormatter {

    private final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final String YEAR_MONTH_DAY = "yyyy-MM-dd";
    private final String YEAR_MONTH = "yyyy-MM";
    private final String MONTH_DAY = "MM-dd";
    private final String MONTH_DAY_TIME = "MM-dd HH:mm";

    private final String TIME_ONLY_PATERN = "HH:mm a";
    private final String UNI_DATE_PATTERN = "dd/MMM/yyyy";
    private final String SERVER_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

    private Locale locale = Locale.UK;
    private TimeZone deviceTimeZone = TimeZone.getDefault();
    private TimeZone serverTimeZone = TimeZone.getTimeZone("UTC");

    public String formatDateToString(Date date, String pattern) {
        DateFormat dateFormat;
        switch (pattern) {
            case "default":
                dateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
                break;
            case "no_time":
                dateFormat = new SimpleDateFormat(YEAR_MONTH_DAY);
                break;
            case "uni_date":
                dateFormat = new SimpleDateFormat(UNI_DATE_PATTERN);
                break;
            case "time_only":
                dateFormat = new SimpleDateFormat(TIME_ONLY_PATERN, locale);
                break;
            case "no_year":
                dateFormat = new SimpleDateFormat(MONTH_DAY);
                break;
            case "server":
                dateFormat = new SimpleDateFormat(SERVER_PATTERN);
                break;
            case "year_month":
                dateFormat = new SimpleDateFormat(YEAR_MONTH);
                break;
            case "month_day_time":
                dateFormat = new SimpleDateFormat(MONTH_DAY_TIME);
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
                dateFormat = new SimpleDateFormat(YEAR_MONTH_DAY);
                break;
            case "uni_date":
                dateFormat = new SimpleDateFormat(UNI_DATE_PATTERN);
                break;
            case "time_only":
                dateFormat = new SimpleDateFormat(TIME_ONLY_PATERN, locale);
                break;
            case "no_year":
                dateFormat = new SimpleDateFormat(MONTH_DAY);
                break;
            case "server":
                dateFormat = new SimpleDateFormat(SERVER_PATTERN);
                break;
            case "year_month":
                dateFormat = new SimpleDateFormat(YEAR_MONTH);
                break;
            case "month_day_time":
                dateFormat = new SimpleDateFormat(MONTH_DAY_TIME);
                break;
            default:
                dateFormat = new SimpleDateFormat(DEFAULT_PATTERN);
                break;
        }
        Date result = dateFormat.parse(string);

        return result;
    }

//    public Calendar setGregorianCalendar(Date date) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//
//        Calendar result = Calendar.getInstance();
//        result.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
//        result.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
//
//        result.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
//        result.set(Calendar.MONTH, cal.get(Calendar.MONTH));
//        result.set(Calendar.YEAR, cal.get(Calendar.YEAR));
//
//        return result;
//    }

    public String getDEFAULT_PATTERN() {
        return DEFAULT_PATTERN;
    }
    public Locale getZoneLocale() {
        return locale;
    }
    public String getZoneLocaleInString() {
        String locale = this.locale.toString();
        return locale;
    }

    public void setZoneLocale(Locale zoneLocale) {
        this.locale = zoneLocale;
    }

}
