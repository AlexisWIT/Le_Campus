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
        long difference = new Date().getTime() - date.getTime();
        long diffInAbs = Math.abs(difference);
        long r = 0;
        if (diffInAbs > year) {
            r = (diffInAbs / year);
            if (r == 1) {
                if (difference>0) return r + " year ago";
                else return "in " +r + " year";
            } else {
                if (difference>0) return r + " years ago";
                else return "in " +r + " years";
            }
        }
        if (diffInAbs > month) {
            r = (diffInAbs / month);
            if (r == 1) {
                if (difference>0) return r + " month ago";
                else return "in " +r + " month";
            } else {
                if (difference>0) return r + " months ago";
                else return "in " +r + " months";
            }
        }
        if (diffInAbs > day) {
            r = (diffInAbs / day);
            if (r == 1) {
                if (difference>0) return "Yesterday";
                else return "Tomorrow";
            } else {
                if (difference>0) return r + " days ago";
                else return "in " +r + " days";
            }
        }
        if (diffInAbs > hour) {
            r = (diffInAbs / hour);
            if (r == 1) {
                if (difference>0) return r + " hour ago";
                else return "in " +r + " hour";
            } else {
                if (difference>0) return r + " hours ago";
                else return "in " +r + " hours";
            }
        }
        if (diffInAbs > minute) {
            r = (diffInAbs / minute);
            if (r == 1) {
                if (difference > 0) return r + " minute ago";
                else return "in " + r + " minute";
            } else {
                if (difference > 0) return r + " minutes ago";
                else return "in " + r + " minutes";
            }
        }
        return "Just now";
    }

}
