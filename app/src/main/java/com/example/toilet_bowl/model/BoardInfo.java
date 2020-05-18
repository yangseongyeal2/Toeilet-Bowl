package com.example.toilet_bowl.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BoardInfo {
    private String title;
    private String content;
    private String uid;
    private String documentId;
    private String deleted_at;
    

    public BoardInfo(String title, String content, String uid, String documentId,  Date date,String deleted_at) {
        this.title = title;
        this.content = content;
        this.uid = uid;
        this.documentId = documentId;
        this.deleted_at = deleted_at;
        this.date = date;
    }

    @Override
    public String toString() {
        return "BoardInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", uid='" + uid + '\'' +
                ", documentId='" + documentId + '\'' +
                ", deleted_at='" + deleted_at + '\'' +
                ", date=" + date +
                '}';
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getDocumentId() {
        return documentId;
    }

    public BoardInfo() {

    }


//
//    public BoardInfo(String title, String content, String uid, Date date, String documentId) {
//        this.title = title;
//        this.content = content;
//        this.uid = uid;
//        this.documentId = documentId;
//        this.date = date;
//    }

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
