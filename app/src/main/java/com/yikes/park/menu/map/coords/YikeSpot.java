package com.yikes.park.menu.map.coords;

public class YikeSpot {
    public String spotName;
    public double spotLat;
    public double spotLong;
    public String spotCreator;
    public String spotPicture;

    public YikeSpot() { }

    public YikeSpot(String spotName, double spotLat, double spotLong, String spotCreator, String spotPicture) {
        this.spotName = spotName;
        this.spotLat = spotLat;
        this.spotLong = spotLong;
        this.spotCreator = spotCreator;
        this.spotPicture = spotPicture;
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

    public String getspotPicture() { return spotPicture; }

    public void setspotPicture(String spotPicture) { this.spotPicture = spotPicture; }
}
