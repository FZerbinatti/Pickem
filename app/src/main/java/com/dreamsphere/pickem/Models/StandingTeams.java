package com.dreamsphere.pickem.Models;

public class StandingTeams {

    Integer place;
    String team;
    Integer wins;
    Integer losses;

    public StandingTeams() {

    }

    public StandingTeams(Integer place, String team, Integer wins, Integer losses) {
        this.place = place;
        this.team = team;
        this.wins = wins;
        this.losses = losses;
    }

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }
}
