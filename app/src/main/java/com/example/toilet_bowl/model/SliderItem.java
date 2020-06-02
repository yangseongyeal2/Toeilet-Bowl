package com.example.toilet_bowl.model;

public class SliderItem {

    // private String description;
    private String imageUrl;

    public SliderItem(String imageUrl){this.imageUrl=imageUrl;}


    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String  imageUrl) {
        this.imageUrl = imageUrl;
    }
}
