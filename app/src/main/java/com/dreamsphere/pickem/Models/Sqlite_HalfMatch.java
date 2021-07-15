package com.dreamsphere.pickem.Models;

public class Sqlite_HalfMatch {

    private String year;
    private String region;
    private String day_id;
    private String match_datetime;

    public Sqlite_HalfMatch() {
    }

    public Sqlite_HalfMatch(String year, String region, String day_id, String match_datetime) {
        this.year = year;
        this.region = region;
        this.day_id = day_id;
        this.match_datetime = match_datetime;
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
