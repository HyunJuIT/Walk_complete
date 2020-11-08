package com.example.walk;

import android.location.Location;

import java.util.ArrayList;

public class MemoVO {
    private boolean memoCheck;
    private String title;
    private String memo;
    private String date;
    private String time;
    private String adress;
    private double latitude;
    private double longitude;
    private ArrayList<String> imgs;
    private WalkInformation information;
    private String WalkTitle;
    private String Diary;

    public MemoVO(boolean memoCheck, String title, String memo, String date, String time, String adress, double latitude, double longitude, ArrayList<String> imgs) {
        this.memoCheck = memoCheck;
        this.title = title;
        this.memo = memo;
        this.date = date;
        this.time = time;
        this.adress = adress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgs = imgs;
    }

    public MemoVO() {
    }

    public String getWalkTitle() {
        return WalkTitle;
    }

    public void setWalkTitle(String walkTitle) {
        WalkTitle = walkTitle;
    }

    public String getDiary() {
        return Diary;
    }

    public void setDiary(String diary) {
        Diary = diary;
    }

    public WalkInformation getInformation() {
        return information;
    }

    public void setInformation(WalkInformation information) {
        this.information = information;
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

    public boolean isMemoCheck() {
        return memoCheck;
    }

    public void setMemoCheck(boolean memoCheck) {
        this.memoCheck = memoCheck;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }
}
