package com.uol.yt120.lecampus.model.restDomain;

public class PublicEvent {
    private int serverId;	// ID in server database
    private Integer localId;	// Id in client device database

    private String holdBy;
    private String eventType;
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
    private String weekDay;			// '1' Monday, '2' Tuesday ..
    private Integer isAllDay;   // '1' all-day event. '0' not
    private Integer isDisabled; // '1' disabled (completed or cancelled). '0' not

    private String host;			// Lecturer or Organizer in name
    private String email;

    private String detailUrl;
    private String imageUrl;
    private String offers;

    public int getServerId() {
        return serverId;
    }

    public Integer getLocalId() {
        return localId;
    }

    public String getHoldBy() {
        return holdBy;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public String getEventCode() {
        return eventCode;
    }

    public String getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public Integer getIsAllDay() {
        return isAllDay;
    }

    public Integer getIsDisabled() {
        return isDisabled;
    }

    public String getHost() {
        return host;
    }

    public String getEmail() {
        return email;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOffers() {
        return offers;
    }
}
