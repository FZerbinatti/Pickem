package com.francesco.pickem.Models;

public class SingleMatch {

    private String nameTeam1;
    private String nameTeam2;
    private String urlLogoTeam1;
    private String urlLogoTeam2;
    private String match_time;

    public SingleMatch(String nameTeam1, String urlLogoTeam1, String nameTeam2,  String urlLogoTeam2, String match_time) {
        this.nameTeam1 = nameTeam1;
        this.nameTeam2 = nameTeam2;
        this.urlLogoTeam1 = urlLogoTeam1;
        this.urlLogoTeam2 = urlLogoTeam2;
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

    public String getMatch_time() {
        return match_time;
    }

    public void setMatch_time(String match_time) {
        this.match_time = match_time;
    }
}
