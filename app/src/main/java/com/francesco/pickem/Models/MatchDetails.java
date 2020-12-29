package com.francesco.pickem.Models;

public class MatchDetails {

    private Integer match_id;
    private String match_date;
    private String match_time;

    private String nameTeam1;
    private String nameTeam2;
    private String urlLogoTeam1;
    private String urlLogoTeam2;

    // le prediction sono un integer tra 1 e 2, -1 se non c'è ancora
    private Integer match_prediction;
    private Integer match_winner;

    public MatchDetails(Integer match_id, String match_date, String match_time,
                        String nameTeam1, String nameTeam2, String urlLogoTeam1, String urlLogoTeam2,
                        Integer match_prediction, Integer match_winner) {

        this.match_id = match_id;
        this.match_date = match_date;
        this.match_time = match_time;
        this.nameTeam1 = nameTeam1;
        this.nameTeam2 = nameTeam2;
        this.urlLogoTeam1 = urlLogoTeam1;
        this.urlLogoTeam2 = urlLogoTeam2;
        this.match_prediction = match_prediction;
        this.match_winner = match_winner;
    }

    public Integer getMatch_id() {
        return match_id;
    }

    public void setMatch_id(Integer match_id) {
        this.match_id = match_id;
    }

    public String getMatch_date() {
        return match_date;
    }

    public void setMatch_date(String match_date) {
        this.match_date = match_date;
    }

    public String getMatch_time() {
        return match_time;
    }

    public void setMatch_time(String match_time) {
        this.match_time = match_time;
    }

    public String getNameTeam1() {
        return nameTeam1;
    }

    public void setNameTeam1(String nameTeam1) {
        this.nameTeam1 = nameTeam1;
    }

    public String getNameTeam2() {
        return nameTeam2;
    }

    public void setNameTeam2(String nameTeam2) {
        this.nameTeam2 = nameTeam2;
    }

    public String getUrlLogoTeam1() {
        return urlLogoTeam1;
    }

    public void setUrlLogoTeam1(String urlLogoTeam1) {
        this.urlLogoTeam1 = urlLogoTeam1;
    }

    public String getUrlLogoTeam2() {
        return urlLogoTeam2;
    }

    public void setUrlLogoTeam2(String urlLogoTeam2) {
        this.urlLogoTeam2 = urlLogoTeam2;
    }

    public Integer getMatch_prediction() {
        return match_prediction;
    }

    public void setMatch_prediction(Integer match_prediction) {
        this.match_prediction = match_prediction;
    }

    public Integer getMatch_winner() {
        return match_winner;
    }

    public void setMatch_winner(Integer match_winner) {
        this.match_winner = match_winner;
    }
}
