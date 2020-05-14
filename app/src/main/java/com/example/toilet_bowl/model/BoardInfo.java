package com.example.toilet_bowl.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BoardInfo {
    private String title;
    private String content;
    private String uid;
    private String documentId;

    public String getDocumentId() {
        return documentId;
    }

    public BoardInfo() {

    }

    @Override
    public String toString() {
        return "BoardInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", uid='" + uid + '\'' +
                ", documentId='" + documentId + '\'' +
                ", date=" + date +
                '}';
    }



    public BoardInfo(String title, String content, String uid, Date date, String documentId) {
        this.title = title;
        this.content = content;
        this.uid = uid;
        this.documentId = documentId;
        this.date = date;
    }

    @ServerTimestamp
    private Date date;



    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public BoardInfo(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
