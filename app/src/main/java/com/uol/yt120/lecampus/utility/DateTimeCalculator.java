package com.uol.yt120.lecampus.utility;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeCalculator {

    private final static long minuteInMillis = 60 * 1000;// 1 min
    private final static long hourInMillis = 60 * minuteInMillis;// 1 hourInMillis
    private final static long dayInMillis = 24 * hourInMillis;// 1 dayInMillis
    private final static long weekInMillis = 7 * dayInMillis;
    private final static long monthInMillis = (long) 30.5 * dayInMillis;// 1 monthInMillis
    private final static long yearInMillis = 12 * monthInMillis;// 1 yearInMillis

    public static final String UNIT_DAY = "day";
    public static final String UNIT_WEEK = "week";
    public static final String UNIT_MONTH = "month";
    public static final String UNIT_YEAR = "year";

    private DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
    private TimeZone deviceTimeZone = TimeZone.getDefault();
    private TimeZone serverTimeZone = TimeZone.getTimeZone("UTC");

    /**
     * Return time by text format
     *
     * @param date
     * @return
     */
    public String getTimeFormatOfText(Date date) {
        if (date == null) {
            return null;
        }
        long difference = System.currentTimeMillis() - date.getTime();
        long diffInAbs = Math.abs(difference);
        long r = 0;
        if (diffInAbs > yearInMillis) {
            r = (diffInAbs / yearInMillis);
            if (r == 1) {
                if (difference>0) return r + " yearInMillis ago";
                else return "in " +r + " yearInMillis";
            } else {
                if (difference>0) return r + " years ago";
                else return "in " +r + " years";
            }
        }
        if (diffInAbs > monthInMillis) {
            r = (diffInAbs / monthInMillis);
            if (r == 1) {
                if (difference>0) return r + " monthInMillis ago";
                else return "in " +r + " monthInMillis";
            } else {
                if (difference>0) return r + " months ago";
                else return "in " +r + " months";
            }
        }
        if (diffInAbs > dayInMillis) {
            r = (diffInAbs / dayInMillis);
            if (r == 1) {
                if (difference>0) return "Yesterday";
                else return "Tomorrow";
            } else {
                if (difference>0) return r + " days ago";
                else return "in " +r + " days";
            }
        }
        if (diffInAbs > hourInMillis) {
            r = (diffInAbs / hourInMillis);
            if (r == 1) {
                if (difference>0) return r + " hourInMillis ago";
                else return "in " +r + " hourInMillis";
            } else {
                if (difference>0) return r + " hours ago";
                else return "in " +r + " hours";
            }
        }
        if (diffInAbs > minuteInMillis) {
            r = (diffInAbs / minuteInMillis);
            if (r == 1) {
                if (difference > 0) return r + " minuteInMillis ago";
                else return "in " + r + " minuteInMillis";
            } else {
                if (difference > 0) return r + " minutes ago";
                else return "in " + r + " minutes";
            }
        }
        return "Just now";
    }

    public String getToday(boolean includeTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(deviceTimeZone);
        Date date=calendar.getTime();

        if (!includeTime) {
            return dateTimeFormatter.formatDateToString(date,"no_time");
        }

        return dateTimeFormatter.formatDateToString(date,"default");
    }

    /**
     * Get Date only
     * @return a date in format "MM-dd" (4-11)
     */
    public String getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeZone(deviceTimeZone);
        Date date=calendar.getTime();

        return dateTimeFormatter.formatDateToString(date,"no_year");
    }

    /**
     * Get new date by given condtions
     *
     * @param amount amount of change
     * @param unit unit of amount change
     *             eg,
     *              -1, day means get date of yesterday
     *              2, week means get date after 2 weeks
     *
     *
     * @param oldDate could be null if only want to
     *                get new date from current date
     *
     */
    public Date getNewDateBy(int amount, String unit, @Nullable Date oldDate) {
        Calendar calendar = Calendar.getInstance();

        if (oldDate != null) {
            calendar.setTime(oldDate);
        }

        switch (unit) {
            case UNIT_DAY:
                calendar.add(Calendar.DAY_OF_MONTH, amount);
                break;

            case UNIT_WEEK:
                calendar.add(Calendar.DAY_OF_MONTH, amount * 7);
                break;

            case UNIT_MONTH:
                calendar.add(Calendar.MONTH, amount);
                break;

            case UNIT_YEAR:
                calendar.add(Calendar.MONTH, amount * 12);
                break;

            default : // Invalid unit input
                System.out.print("Invalid Time Unit");
        }

        Date date = calendar.getTime();
        return date;
    }

}
