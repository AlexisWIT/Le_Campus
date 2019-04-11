package com.uol.yt120.lecampus.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "userevent_table")
public class UserEvent {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

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

    private String host;        // Lecturer or Organizer in name
    private String email;

    private Integer publicEventId;
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



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getPublicEventId() {
        return publicEventId;
    }

    public void setPublicEventId(Integer publicEventId) {
        this.publicEventId = publicEventId;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOffers() {
        return offers;
    }

    public void setOffers(String offers) {
        this.offers = offers;
    }
}
