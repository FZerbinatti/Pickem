package com.francesco.pickem.Models;

public class SingleMatch {

    private String nameTeam1;
    private String nameTeam2;
    private String urlLogoTeam1;
    private String urlLogoTeam2;

    public SingleMatch(String nameTeam1, String urlLogoTeam1, String nameTeam2, String urlLogoTeam2) {
        this.nameTeam1 = nameTeam1;
        this.urlLogoTeam1 = urlLogoTeam1;

        this.nameTeam2 = nameTeam2;
        this.urlLogoTeam2 = urlLogoTeam2;
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
}
