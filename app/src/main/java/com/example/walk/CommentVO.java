package com.example.walk;

public class CommentVO {
    private String ID;
    private String profile;    //프로필
    private String nickName;    //닉네임
    private String date;    //날짜
    private String time;  //시간
    private String comment;  //피드 내용

    public CommentVO(String ID, String profile, String nickName, String date, String time, String comment) {
        this.ID = ID;
        this.profile = profile;
        this.nickName = nickName;
        this.date = date;
        this.time = time;
        this.comment = comment;
    }

    public CommentVO() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
