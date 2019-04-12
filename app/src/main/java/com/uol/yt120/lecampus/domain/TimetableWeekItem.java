package com.uol.yt120.lecampus.domain;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.Date;

public class TimetableWeekItem{
    private long id;
    private String title;
    private Date startTime;
    private Date endTime;
    private String location;
    private int color;

    /* ... */

//    @Override
//    public WeekViewEvent<TimetableWeekItem> toWeekViewEvent() {
//        // Note: It's important to pass "this" as the last argument to WeekViewEvent's constructor.
//        // This way, the EventClickListener can return this object in its onEventClick() method.
//        //boolean isAllDay = DateUtils.isAllDay(startTime, endTime);
//        return new WeekViewEvent<>(
//                id, title, startTime.toGregorianCalendar(),
//                endTime.toGregorianCalendar(), location, color, isAllDay, this
//        );
//    }
}
