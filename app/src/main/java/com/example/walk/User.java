package com.example.walk;

public class User {
    private static String nickName;
    private static byte[] profileImg;
    private static String ID;
    private static String PW;

    public static String getNickName() {
        return nickName;
    }

    public static void setNickName(String nickName) {
        User.nickName = nickName;
    }

    public static byte[] getProfileImg() {
        return profileImg;
    }

    public static void setProfileImg(byte[] profileImg) {
        User.profileImg = profileImg;
    }

    public static String getID() {
        return ID;
    }

    public static void setID(String ID) {
        User.ID = ID;
    }

    public static String getPW() {
        return PW;
    }

    public static void setPW(String PW) {
        User.PW = PW;
    }
}
