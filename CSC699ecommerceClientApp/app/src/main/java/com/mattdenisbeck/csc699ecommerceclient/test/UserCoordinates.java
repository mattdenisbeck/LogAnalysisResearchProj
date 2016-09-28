package com.mattdenisbeck.csc699ecommerceclient.test;

public class UserCoordinates {
    public final double latitude;
    public final double longitude;

    public UserCoordinates(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }
}
