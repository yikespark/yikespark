package com.yikes.park.menu.map.Objects;

public class SkatePark {
    private String name;
    private double lat;
    private double lon;
    private String type;
    private String id;

    public SkatePark(){

    }

    @Override
    public String toString() {
        return "SkatePark{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public SkatePark(String name, double lat, double lon, String type, String id) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.id = id;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}