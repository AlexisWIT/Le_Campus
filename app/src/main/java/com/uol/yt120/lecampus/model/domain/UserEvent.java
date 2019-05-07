package com.uol.yt120.lecampus.model.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewEvent.Style;
import com.alamkanak.weekview.WeekViewEvent.Style.Builder;
import com.uol.yt120.lecampus.R;
import com.uol.yt120.lecampus.utility.DateTimeCalculator;
import com.uol.yt120.lecampus.utility.DateTimeFormatter;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Calendar;

@Entity(tableName = "userevent_table")
public class UserEvent implements WeekViewDisplayable<UserEvent>{


    @PrimaryKey(autoGenerate = true)
    private Integer localId;    // As the primary key in local database
    private Integer serverId;   // As the primary key of this event on the server database

    private String holdBy;      // University, Community, Personal or Organization
    private String eventType;   // Lecture, Seminar or Social Event
    private String eventTitle;  // 'Cryptography and Internet Security' or 'Neon Night: Riders vs Rocks'
    private String eventDesc;
    private String eventCode;

    private String location;    // 'Attenborough' or 'The Belmont Hotel'
    private String address;     // 'ATT LT3' or '20 De Montfort Square'
    private String postCode;
    private String city;        // Leicester or London
    private String region;      // 'England', 'East Midland' or 'Leicestershire'
    private String country;     // 'UK' or 'US'

    private String lat;         // Latitude
    private String lon;         // Longitude

    private String startTime;
    private String endTime;
    private String duration;
    private String weekDay;     // '1' Monday, '2' Tuesday ..
    private Integer isAllDay = 0;   // '1' all-day event. '0' not
    private Integer isDisabled = 0; // '1' disabled (completed or cancelled). '0' not

    private String host;        // Lecturer or Organizer in name
    private String email;


    private String detailUrl;
    private String imageUrl;
    private String offers;      // JsonObject -> String
    public UserEvent() { }

    @Ignore
    public UserEvent(String eventType, String location, String lat, String lon, String startTime, String endTime) {
        this.eventType = eventType;
        this.location = location;
        this.lat = lat;
        this.lon = lon;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Ignore
    public UserEvent(String holdBy, String eventType, String eventTitle, String eventDesc, String eventCode, String location, String address, String postCode, String city, String region, String country, String lat, String lon, String startTime, String endTime, String duration, String weekDay, String host, String email) {
        this.holdBy = holdBy;
        this.eventType = eventType;
        this.eventTitle = eventTitle;
        this.eventDesc = eventDesc;
        this.eventCode = eventCode;
        this.location = location;
        this.address = address;
        this.postCode = postCode;
        this.city = city;
        this.region = region;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.weekDay = weekDay;
        this.host = host;
        this.email = email;
    }



    public Integer getLocalId() {
        return localId;
    }

    public void setLocalId(Integer localId) {
        this.localId = localId;
    }

    public String getHoldBy() {
        return holdBy;
    }

    public void setHoldBy(String holdBy) {
        this.holdBy = holdBy;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getIsAllDay() { return isAllDay; }

    public void setIsAllDay(Integer isAllDay) { this.isAllDay = isAllDay; }

    public Integer getIsDisabled() { return isDisabled; }

    public void setIsDisabled(Integer isDisabled) { this.isDisabled = isDisabled; }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOffers() {
        return offers;
    }

    public void setOffers(String offers) {
        this.offers = offers;
    }


    @Ignore
    private Context context;
    @Ignore
    private Calendar startTimeWeekView;
    @Ignore
    private Calendar endTimeWeekView;
    @Ignore
    private boolean allDay;
    @Ignore
    private int color;
    @Ignore
    private boolean cancelled;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Calendar getStartTimeWeekView() {
        return startTimeWeekView;
    }

    public void setStartTimeWeekView(Calendar startTimeWeekView) {
        this.startTimeWeekView = startTimeWeekView;
    }

    public Calendar getEndTimeWeekView() {
        return endTimeWeekView;
    }

    public void setEndTimeWeekView(Calendar endTimeWeekView) {
        this.endTimeWeekView = endTimeWeekView;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * This constructor is for weekView item, I know this is
     * very messy but if I have time to do refactoring I will do it
     *
     * @param
     * @param eventTitle
     * @param location
     * @param startTimeWeekView
     * @param endTimeWeekView
     * @param allDay
     * @param color
     * @param cancelled
     */
    @Ignore
    public UserEvent(String eventTitle, String location, Calendar startTimeWeekView, Calendar endTimeWeekView, boolean allDay, int color, boolean cancelled) {
        //this.localId = localId;
        this.eventTitle = eventTitle;
        this.location = location;
        this.startTimeWeekView = startTimeWeekView;
        this.endTimeWeekView = endTimeWeekView;
        this.allDay = allDay;
        this.color = color;
        this.cancelled = cancelled;
    }

    @NonNull
    //@Override
    public UserEvent toWeekViewEvent(Context context){
        //this.context = context;
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
        DateTimeCalculator dateTimeCalculator = new DateTimeCalculator();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        Integer allday = this.isAllDay;
        boolean isAllDay = allday == 1;
        Integer cancelled = this.isDisabled;
        boolean isCancelled = cancelled == 1;

        try {
            startDate.setTime(dateTimeFormatter.parseStringToDate(this.startTime, "default"));
            endDate.setTime(dateTimeFormatter.parseStringToDate(this.endTime, "default"));

        } catch (ParseException e) {
            e.printStackTrace();
            //return null;
        }
        int viewStyle = getColor(this.context);

        UserEvent userEvent = new UserEvent();
        userEvent.setLocalId(this.localId);
        userEvent.setEventTitle(this.eventTitle);
        userEvent.setStartTimeWeekView(startDate);
        userEvent.setEndTimeWeekView(endDate);
        userEvent.setLocation(this.location);
        userEvent.setAllDay(isAllDay);
        userEvent.setColor(viewStyle);
        userEvent.setCancelled(isCancelled);

        //Log.w("[User Event]", "User event to Weekview event");

        return userEvent;
    }

    private int getColor(Context context) {
        String type = getEventType();

        final int color_lecture = context.getResources().getColor(R.color.event_color_lecture);
        final int color_surgery = context.getResources().getColor(R.color.event_color_surgery);
        final int color_computer_class = context.getResources().getColor(R.color.event_color_computer_class);
        final int color_test = context.getResources().getColor(R.color.event_color_test);
        final int color_seminar = context.getResources().getColor(R.color.event_color_seminar);
        final int color_workshop = context.getResources().getColor(R.color.event_color_workshop);
        final int color_text = context.getResources().getColor(R.color.color_text_dark);

        int bgColor;
        int defaultColor = Color.GRAY;

        boolean isEnd = isDisabled == 1;
        int borderWidth = !isEnd ? 0 : 2;


        switch (type) {
            case "Lecture":
                bgColor = color_lecture;
                break;
            case "Surgery":
                bgColor = color_surgery;
                break;
            case "Computer Class":
                bgColor = color_computer_class;
                break;
            case "Test":
                bgColor = color_test;
                break;
            case "Workshop":
                bgColor = color_workshop;
                break;
            case "Seminar":
                bgColor = color_seminar;
                break;
            default:
                bgColor = defaultColor;
                break;
        }

        return bgColor;
    }

    @NotNull
    @Override
    public WeekViewEvent<UserEvent> toWeekViewEvent() {
        //WeekViewEvent<UserEvent> result = toWeekViewEvent(getContext());
//        final int color_lecture = Color.LTGRAY;
//        final int color_surgery = Color.YELLOW;
//        final int color_computer_class = Color.CYAN;
//        final int color_test = Color.GREEN;
//        final int color_seminar = Color.MAGENTA;
//        final int color_workshop = Color.BLUE;
        final int color_text = Color.BLACK;

        Integer cancelled = this.isDisabled;
        boolean isCancelled = cancelled == 1;
        int bgColor;


        Style style = new Builder().setBackgroundColor(this.color).setTextColor(color_text).setTextStrikeThrough(isCancelled).build();

        WeekViewEvent<UserEvent> event = new WeekViewEvent.Builder<UserEvent>()
                .setId(this.localId)
                .setTitle(this.eventTitle)
                .setStartTime(this.startTimeWeekView)
                .setEndTime(this.endTimeWeekView)
                .setLocation(this.location)
                .setAllDay(this.allDay)
                .setStyle(style)
                .setData(this)
                .build();

        //Log.w("[User Event]", "Weekview event to Weekview event");
        //Log.w("[UserEvent]", event.toString());
        return event;
    }
}
