package com.uol.yt120.lecampus.model.restDomain;

import com.google.gson.annotations.SerializedName;

public class CrimeGeoFence {

    private int serverId;
    private String category;
    @SerializedName("latitude")
    private String lat;
    @SerializedName("longitude")
    private String lng;
    private String location;
    private String timeInMonth;

    public int getServerId() {
        return serverId;
    }

    public String getCategory() {
        return category;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getLocation() {
        return location;
    }

    public String getTimeInMonth() {
        return timeInMonth;
    }
}
