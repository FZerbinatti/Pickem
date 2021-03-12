package com.francesco.pickem.Models;

public class AnalistPerson {

    String region;
    String userId;
    String username;

    public AnalistPerson() {

    }

    public AnalistPerson(String region, String userId, String username) {
        this.region = region;
        this.userId = userId;
        this.username = username;
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
