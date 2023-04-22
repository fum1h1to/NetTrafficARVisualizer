package com.fum1h1to.NetTrafficARVisualizer.capture.Geoip;

public class Coordinate {
    private double latitude;
    private double longitude;

    public Coordinate(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
