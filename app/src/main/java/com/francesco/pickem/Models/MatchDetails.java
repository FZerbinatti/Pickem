package com.francesco.pickem.Models;

public class MatchDetails {

    private String id;
    private String datetime;
    private String team1;
    private String team2;
    private String winner;


    public MatchDetails() {

    }

    public MatchDetails(String id, String datetime, String team1, String team2, String winner) {
        this.id = id;
        this.datetime = datetime;
        this.team1 = team1;
        this.team2 = team2;
        this.winner = winner;
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

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
