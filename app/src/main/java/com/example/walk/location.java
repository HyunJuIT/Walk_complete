package com.example.walk;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class location {

    private double latitude;
    private double longitude;

    public location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public location(LatLng latLng){
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public location(){

    }

    public LatLng getLatLng(){

        LatLng latLngs = new LatLng(latitude,longitude);

        return latLngs;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
