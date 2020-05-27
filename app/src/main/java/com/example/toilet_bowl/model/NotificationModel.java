package com.example.toilet_bowl.model;



public class NotificationModel {
    public String to;

    public Notification notification=new Notification();
    public static class Notification{
        public String title;
        public String text;
        public String documentId;
    }
}
