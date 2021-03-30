package com.yikes.park.menu.map.coords;

public class YikeSpot {
    public String spotName;
    public double spotLat;
    public double spotLong;
    public String spotCreator;
    public String spotPicture;
    public String id;

    public YikeSpot() { }

    public YikeSpot(String spotName, double spotLat, double spotLong, String spotCreator, String spotPicture, String id) {
        this.spotName = spotName;
        this.spotLat = spotLat;
        this.spotLong = spotLong;
        this.spotCreator = spotCreator;
        this.spotPicture = spotPicture;
        this.id = id;
    }

    @Override
    public String toString() {
        return "YikeSpot{" +
                "spotName='" + spotName + '\'' +
                ", spotLat=" + spotLat +
                ", spotLong=" + spotLong +
                ", spotCreator='" + spotCreator + '\'' +
                ", spotPicture='" + spotPicture + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getSpotPicture() {
        return spotPicture;
    }

    public void setSpotPicture(String spotPicture) {
        this.spotPicture = spotPicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpotCreator() { return spotCreator; }

    public void setSpotCreator(String spotCreator) { this.spotCreator = spotCreator; }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public double getSpotLat() {
        return spotLat;
    }

    public void setSpotLat(double spotLat) {
        this.spotLat = spotLat;
    }

    public double getSpotLong() {
        return spotLong;
    }

    public void setSpotLong(double spotLong) {
        this.spotLong = spotLong;
    }
}
