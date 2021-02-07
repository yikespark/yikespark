package com.yikes.park.CL;

public class SkatePark {
    public String parkName;
    public double parkLat;
    public double parkLong;

    public SkatePark(){

    }

    public SkatePark(String parkName, double parkLat, double parkLong) {
        this.parkName = parkName;
        this.parkLat = parkLat;
        this.parkLong = parkLong;
    }

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