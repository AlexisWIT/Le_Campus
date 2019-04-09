package com.uol.yt120.lecampus.utility;

import java.util.Date;

public class TimeCalculator {

    private final static long minute = 60 * 1000;// 1 min
    private final static long hour = 60 * minute;// 1 hour
    private final static long day = 24 * hour;// 1 day
    private final static long month = 31 * day;// 1 month
    private final static long year = 12 * month;// 1 year

    /**
     * Return time by text format
     *
     * @param date
     * @return
     */
    public static String getTimeFormatByText(Date date) {
        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            if (r == 1)
                return r + " year ago";
            else
                return r + " years ago";
        }
        if (diff > month) {
            r = (diff / month);
            if (r == 1)
                return r + " month ago";
            else
                return r + " months ago";
        }
        if (diff > day) {
            r = (diff / day);
            if (r == 1)
                return r + " day ago";
            else
                return r + " days ago";
        }
        if (diff > hour) {
            r = (diff / hour);
            if (r == 1)
                return r + " hour ago";
            else
                return r + " hours ago";
        }
        if (diff > minute) {
            r = (diff / minute);
            if (r == 1)
                return r + " minute ago";
            else
                return r + " minutes ago";
        }
        return "Just now";
    }

}
