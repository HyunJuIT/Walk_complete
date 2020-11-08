package com.example.walk;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedVO implements Serializable {
    private String ID;  //아이디
    private String profile;    //프로필
    private String nickName;    //닉네임
    private String time;  //시간
    private String feed;  //피드 내용
    private ArrayList<String> imgs = new ArrayList<>(); //사진들이 들어있음.
    private int favorite = 0; //좋아요 갯수 텍스트뷰
    private ArrayList<String> favorite_main = new ArrayList<>();  //좋아요 누른 아이디들이 들어있음.
    private ArrayList<CommentVO> comments = new ArrayList<>();

/*    public FeedVO(String profile, String nickName, String time, String feed, ArrayList<String> imgs) {
        this.profile = profile;
        this.nickName = nickName;
        this.time = time;
        this.feed = feed;
        this.imgs = imgs;
    }*/

    public FeedVO(String ID, String profile, String nickName, String time, String feed, ArrayList<String> imgs) {
        this.ID = ID;
        this.profile = profile;
        this.nickName = nickName;
        this.time = time;
        this.feed = feed;
        this.imgs = imgs;
    }

/*    public FeedVO(String profile, String nickName, String time, String feed, ArrayList<String> imgs, int favorite, boolean favorite_main, ArrayList<CommentVO> comments) {
        this.profile = profile;
        this.nickName = nickName;
        this.time = time;
        this.feed = feed;
        this.imgs = imgs;
        this.favorite = favorite;
        this.favorite_main = favorite_main;
        this.comments = comments;
    }

    public FeedVO(String profile, String nickName, String time, String feed, int favorite, boolean favorite_main) {
        this.profile = profile;
        this.nickName = nickName;
        this.time = time;
        this.feed = feed;
        this.favorite = favorite;
        this.favorite_main = favorite_main;
    }*/

    public FeedVO() {

    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ArrayList<String> getFavorite_main() {
        return favorite_main;
    }

    public void setFavorite_main(ArrayList<String> favorite_main) {
        this.favorite_main = favorite_main;
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

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public ArrayList<CommentVO> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentVO> comments) {
        this.comments = comments;
    }
}
