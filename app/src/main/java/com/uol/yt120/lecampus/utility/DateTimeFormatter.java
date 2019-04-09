package com.uol.yt120.lecampus.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter {

    private String datePattern = "yyyy/MM/dd HH:mm:ss";
    private Locale zoneLocale = Locale.UK;

    public String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(datePattern, zoneLocale);
        String result = dateFormat.format(date);
        return result;
    }

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
