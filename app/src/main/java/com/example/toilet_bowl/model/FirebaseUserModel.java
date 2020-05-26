package com.example.toilet_bowl.model;

public class FirebaseUserModel {
    private String token;
    private String uid;
    private int likecount;

    private String nickname;

    public FirebaseUserModel(String token, String uid, int likecount,  String nickname) {
        this.token = token;
        this.uid = uid;
        this.likecount = likecount;

        this.nickname = nickname;
    }
    public FirebaseUserModel(){}

    @Override
    public String toString() {
        return "FirebaseUserModel{" +
                "token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                ", likecount=" + likecount +
                ", nickname='" + nickname + '\'' +
                '}';
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
