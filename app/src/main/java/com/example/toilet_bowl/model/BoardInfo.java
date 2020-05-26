package com.example.toilet_bowl.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardInfo {
    private String title;
    private String content;
    private String uid;
    private String documentId;
    private String deleted_at;
    @ServerTimestamp
    private Date date;
    private List<String> uidList;
    private int viewcount;

    public BoardInfo(String title, String content, String uid, String documentId,  Date date, String deleted_at, List<String> uidList, int viewcount) {
        this.title = title;
        this.content = content;
        this.uid = uid;
        this.documentId = documentId;
        this.deleted_at = deleted_at;
        this.date = date;
        this.uidList = uidList;
        this.viewcount = viewcount;
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
                ", uidList=" + uidList +
                ", viewcount=" + viewcount +
                '}';
    }

    public int getViewcount() {
        return viewcount;
    }

    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }

    public BoardInfo(String title, String content, String uid, String documentId, Date date, String deleted_at, List<String> uidList) {
        this.title = title;
        this.content = content;
        this.uid = uid;
        this.documentId = documentId;
        this.deleted_at = deleted_at;
        this.date = date;
        this.uidList=uidList;
    }

    public List<String> getUidList() {
        return uidList;
    }

    public void setUidList(List<String> uidList) {
        this.uidList = uidList;
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
