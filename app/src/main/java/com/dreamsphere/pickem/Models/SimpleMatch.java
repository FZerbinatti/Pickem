package com.dreamsphere.pickem.Models;

public class SimpleMatch {

    String datetime;
    String team1;
    String team2;


    public SimpleMatch() {

    }

    public SimpleMatch(String datetime, String team1, String team2) {
        this.datetime = datetime;
        this.team1 = team1;
        this.team2 = team2;
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
