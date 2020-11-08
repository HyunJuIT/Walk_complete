package com.example.walk;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class WalkInformation implements Serializable {
    private ArrayList<location> line;
    private location latLng;
    private double km;
    private double speed;
    private double cul;
    private int time;

    public WalkInformation(){

    }

    public WalkInformation( location latLng, double km, double speed, double cul, int time) {
        this.latLng = latLng;
        this.km = km;
        this.speed = speed;
        this.cul = cul;
        this.time = time;
    }

    public ArrayList<location> getLine() {
        return line;
    }

    public void setLine(ArrayList<location> line) {
        this.line = line;
    }

    public location getLatLng() {
        return latLng;
    }

    public void setLatLng(location latLng) {
        this.latLng = latLng;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCul() {
        return cul;
    }

    public void setCul(double cul) {
        this.cul = cul;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
