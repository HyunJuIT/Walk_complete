package com.example.walk;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class MyRouteVO implements Serializable {
    private String id;
    private String nickname;
    private String date;
    private String RouteTitle;
    private String RouteExplanation;
    private String adress;
    private float satisfaction;   //만족도
    private ArrayList<location> latLngs;
    private ArrayList<String> imgs;
    private WalkInformation information;
    private boolean share;
    private ArrayList<String> bookMark = new ArrayList<>();

    public MyRouteVO() {
    }

    public MyRouteVO(String id, String nickname, String date, String routeTitle, String routeExplanation, String adress, float satisfaction, ArrayList<location> latLngs, ArrayList<String> imgs, WalkInformation information, boolean share) {
        this.id = id;
        this.nickname = nickname;
        this.date = date;
        RouteTitle = routeTitle;
        RouteExplanation = routeExplanation;
        this.adress = adress;
        this.satisfaction = satisfaction;
        this.latLngs = latLngs;
        this.imgs = imgs;
        this.information = information;
        this.share = share;
    }

    public ArrayList<String> getBookMark() {
        return bookMark;
    }

    public void setBookMark(ArrayList<String> bookMark) {
        this.bookMark = bookMark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRouteTitle() {
        return RouteTitle;
    }

    public void setRouteTitle(String routeTitle) {
        RouteTitle = routeTitle;
    }

    public String getRouteExplanation() {
        return RouteExplanation;
    }

    public void setRouteExplanation(String routeExplanation) {
        RouteExplanation = routeExplanation;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public float getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(float satisfaction) {
        this.satisfaction = satisfaction;
    }

    public ArrayList<location> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(ArrayList<location> latLngs) {
        this.latLngs = latLngs;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public WalkInformation getInformation() {
        return information;
    }

    public void setInformation(WalkInformation information) {
        this.information = information;
    }
}
