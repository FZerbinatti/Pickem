package com.francesco.pickem.Models;

public class Sqlite_Match {

    private String region;
    private String day_id;
    private String match_datetime;

    public Sqlite_Match() {
    }

    public Sqlite_Match(String region, String day_id, String match_datetime) {
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
}
