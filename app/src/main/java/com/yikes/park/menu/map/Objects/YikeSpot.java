package com.yikes.park.menu.map.Objects;

public class YikeSpot {
    private String name;
    private double lat;
    private double lon;
    private String creator;
    private String picture;
    private String id;

    public YikeSpot() { }

    public YikeSpot(String name, double lat, double lon, String creator, String picture, String id) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.creator = creator;
        this.picture = picture;
        this.id = id;
    }

    @Override
    public String toString() {
        return "YikeSpot{" +
                "spotName='" + name + '\'' +
                ", spotLat=" + lat +
                ", spotLong=" + lon +
                ", spotCreator='" + creator + '\'' +
                ", spotPicture='" + picture + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
