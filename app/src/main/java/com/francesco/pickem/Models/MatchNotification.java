package com.francesco.pickem.Models;

public class MatchNotification {

    private String region;
    private String id;
    private String datetime;
    private String team1;
    private String team2;



    public MatchNotification() {

    }


    public MatchNotification(String region, String id, String datetime, String team1, String team2) {
        this.region = region;
        this.id = id;
        this.datetime = datetime;
        this.team1 = team1;
        this.team2 = team2;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }
}
