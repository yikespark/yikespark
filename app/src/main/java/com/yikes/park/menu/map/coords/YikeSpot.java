package com.yikes.park.menu.map.coords;

public class YikeSpot {
    public String spotName;
    public double spotLat;
    public double spotLong;

    public YikeSpot() { }

    public YikeSpot(String spotName, double spotLat, double spotLong) {
        this.spotName = spotName;
        this.spotLat = spotLat;
        this.spotLong = spotLong;
    }

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
