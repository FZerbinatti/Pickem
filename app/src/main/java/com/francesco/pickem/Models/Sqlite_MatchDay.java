package com.francesco.pickem.Models;

public class Sqlite_MatchDay {

    private String year;
    private String region;
    private String match_day;

    public Sqlite_MatchDay() {

    }

    public Sqlite_MatchDay(String year, String region, String match_day) {
        this.year = year;
        this.region = region;
        this.match_day = match_day;
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

    public String getMatch_day() {
        return match_day;
    }

    public void setMatch_day(String match_day) {
        this.match_day = match_day;
    }
}
