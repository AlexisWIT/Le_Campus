package com.uol.yt120.lecampus.model.restDomain;

public class BuildingGeoFence {
    private int serverId;
    private String building;
    private String address;
    private String imageURL;
    private String websiteURL;
    private String collegeName;
    private String directionPoint; // the coordinate in the middle
    private String nodeList;

    public int getServerId() {
        return serverId;
    }

    public String getBuilding() {
        return building;
    }

    public String getAddress() {
        return address;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getDirectionPoint() {
        return directionPoint;
    }

    public String getNodeList() {
        return nodeList;
    }
}
