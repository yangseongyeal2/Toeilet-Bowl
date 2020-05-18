package com.example.toilet_bowl.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class LikeInfo {
   private String date;
   private String uid;

    public LikeInfo(String date, String uid) {
        this.date = date;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "LikeInfo{" +
                "date='" + date + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
