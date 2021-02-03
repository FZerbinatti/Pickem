package com.francesco.pickem.Models;

public class DisplayMatch {

    private String datetime;
    private String year;
    private String region;
    private String id;
    private String date;
    private String time;
    private String team1;
    private String team2;
    private String urlLogoteam1;
    private String urlLogoteam2;
    private String prediction;
    private String winner;
    private Long team1_score;
    private Long team2_score;

    public DisplayMatch() {
    }


    public DisplayMatch(String datetime, String year, String region, String id, String date, String time, String team1, String team2, String urlLogoteam1, String urlLogoteam2, String prediction, String winner, Long team1_score, Long team2_score) {
        this.datetime = datetime;
        this.year = year;
        this.region = region;
        this.id = id;
        this.date = date;
        this.time = time;
        this.team1 = team1;
        this.team2 = team2;
        this.urlLogoteam1 = urlLogoteam1;
        this.urlLogoteam2 = urlLogoteam2;
        this.prediction = prediction;
        this.winner = winner;
        this.team1_score = team1_score;
        this.team2_score = team2_score;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getUrlLogoteam1() {
        return urlLogoteam1;
    }

    public void setUrlLogoteam1(String urlLogoteam1) {
        this.urlLogoteam1 = urlLogoteam1;
    }

    public String getUrlLogoteam2() {
        return urlLogoteam2;
    }

    public void setUrlLogoteam2(String urlLogoteam2) {
        this.urlLogoteam2 = urlLogoteam2;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Long getTeam1_score() {
        return team1_score;
    }

    public void setTeam1_score(Long team1_score) {
        this.team1_score = team1_score;
    }

    public Long getTeam2_score() {
        return team2_score;
    }

    public void setTeam2_score(Long team2_score) {
        this.team2_score = team2_score;
    }
}
