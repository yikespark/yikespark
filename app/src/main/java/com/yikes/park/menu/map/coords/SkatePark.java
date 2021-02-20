package com.yikes.park.menu.map.coords;

public class SkatePark {
    public String parkName;
    public double parkLat;
    public double parkLong;
    public String parkType;

    public SkatePark(){

    }

    public SkatePark(String parkName, double parkLat, double parkLong, String parkType) {
        this.parkName = parkName;
        this.parkLat = parkLat;
        this.parkLong = parkLong;
        this.parkType = parkType;
    }

    public String getParkType() { return parkType; }

    public void setParkType(String parkType) { this.parkType = parkType; }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public double getParkLat() {
        return parkLat;
    }

    public void setParkLat(double parkLat) {
        this.parkLat = parkLat;
    }

    public double getParkLong() {
        return parkLong;
    }

    public void setParkLong(double parkLong) {
        this.parkLong = parkLong;
    }
}