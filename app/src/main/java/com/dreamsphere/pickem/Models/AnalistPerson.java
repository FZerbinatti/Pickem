package com.dreamsphere.pickem.Models;

public class AnalistPerson {
    String image;
    String region;
    String userId;
    String username;

    public AnalistPerson() {

    }

    public AnalistPerson(String image, String region, String userId, String username) {
        this.image = image;
        this.region = region;
        this.userId = userId;
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
