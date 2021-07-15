package com.dreamsphere.pickem.Models;

public class EloTrackerNotifications {

    Integer summoner_elotracker;
    String summoner_name;
    String summoner_server;
    String timer_elotracker;

    public EloTrackerNotifications() {}


    public EloTrackerNotifications(Integer summoner_elotracker, String summoner_name, String summoner_server, String timer_elotracker) {
        this.summoner_elotracker = summoner_elotracker;
        this.summoner_name = summoner_name;
        this.summoner_server = summoner_server;
        this.timer_elotracker = timer_elotracker;
    }

    public Integer getSummoner_elotracker() {
        return summoner_elotracker;
    }

    public void setSummoner_elotracker(Integer summoner_elotracker) {
        this.summoner_elotracker = summoner_elotracker;
    }

    public String getSummoner_name() {
        return summoner_name;
    }

    public void setSummoner_name(String summoner_name) {
        this.summoner_name = summoner_name;
    }

    public String getSummoner_server() {
        return summoner_server;
    }

    public void setSummoner_server(String summoner_server) {
        this.summoner_server = summoner_server;
    }

    public String getTimer_elotracker() {
        return timer_elotracker;
    }

    public void setTimer_elotracker(String timer_elotracker) {
        this.timer_elotracker = timer_elotracker;
    }
}
