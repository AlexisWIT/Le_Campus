package com.uol.yt120.lecampus.model.restDomain;

public class EmergencyRequest {
    private int serverId;
    private String userName;
    private String time;
    private String latitude;
    private String longitude;
    private String type; // Medical or Security

    public EmergencyRequest(String userName, String time, String latitude, String longitude, String type) {
        this.userName = userName;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public int getServerId() {
        return serverId;
    }

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        return time;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }
}
