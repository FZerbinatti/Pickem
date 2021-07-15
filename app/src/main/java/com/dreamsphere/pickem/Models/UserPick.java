package com.dreamsphere.pickem.Models;

public class UserPick {

    String region;
    String match_ID;
    String userPick;

    public UserPick() {

    }

    public UserPick(String region, String match_ID, String userPick) {
        this.region = region;
        this.match_ID = match_ID;
        this.userPick = userPick;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMatch_ID() {
        return match_ID;
    }

    public void setMatch_ID(String match_ID) {
        this.match_ID = match_ID;
    }

    public String getUserPick() {
        return userPick;
    }

    public void setUserPick(String userPick) {
        this.userPick = userPick;
    }
}
