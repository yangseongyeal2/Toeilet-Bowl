package com.example.toilet_bowl.model;

import java.util.Date;

public class FirebaseUserModel {
    private String token;
    private String uid;
    private int likecount;
    private String UserNickName;
    private String nickname;

    private Date date;

    public FirebaseUserModel(String token, String uid, int likecount,  String nickname,String UserNickName,Date date) {
        this.token = token;
        this.uid = uid;
        this.likecount = likecount;
        this.nickname = nickname;
        this.UserNickName=UserNickName;
        this.date=date;
    }
    public FirebaseUserModel(){}

    @Override
    public String toString() {
        return "FirebaseUserModel{" +
                "token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                ", likecount=" + likecount +
                ", UserNickName='" + UserNickName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", date=" + date +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String userNickName) {
        UserNickName = userNickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
