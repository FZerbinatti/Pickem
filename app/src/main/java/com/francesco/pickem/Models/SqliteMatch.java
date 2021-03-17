package com.francesco.pickem.Models;

public class SqliteMatch {

    private String year;
    private String region;
    private String day_id;
    private String match_datetime;
    private String team1;
    private String team2;
    private String winner;

    public SqliteMatch() {
    }

    public SqliteMatch(String year, String region, String day_id, String match_datetime, String team1, String team2, String winner) {
        this.year = year;
        this.region = region;
        this.day_id = day_id;
        this.match_datetime = match_datetime;
        this.team1 = team1;
        this.team2 = team2;
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDay_id() {
        return day_id;
    }

    public void setDay_id(String day_id) {
        this.day_id = day_id;
    }

    public String getMatch_datetime() {
        return match_datetime;
    }

    public void setMatch_datetime(String match_datetime) {
        this.match_datetime = match_datetime;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
