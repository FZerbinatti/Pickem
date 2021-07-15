package com.dreamsphere.pickem.Models;

import java.util.Comparator;

public class AnalistPersonChoosen implements Comparator<AnalistPersonChoosen>{
    String image;
    String region;
    String userId;
    String username;
    Boolean choosen;

    public AnalistPersonChoosen() {

    }

    public AnalistPersonChoosen(String image, String region, String userId, String username, Boolean choosen) {
        this.image = image;
        this.region = region;
        this.userId = userId;
        this.username = username;
        this.choosen = choosen;
    }

    public Boolean getChoosen() {
        return choosen;
    }

    public void setChoosen(Boolean choosen) {
        this.choosen = choosen;
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




    @Override
    public int compare(AnalistPersonChoosen analistPersonChoosen, AnalistPersonChoosen t1) {
        return 0;
    }


}
