package com.example.toilet_bowl.model;

public class BoardInfo {
    private String title;
    private String content;
    private String uid;

    public BoardInfo(String title, String content, String uid) {
        this.title = title;
        this.content = content;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "BoardInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", uid='" + uid + '\'' +
                '}';
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
