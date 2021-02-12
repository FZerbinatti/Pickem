package com.francesco.pickem.Models;

public class TeamNotification {


    Integer notification_team_morning_reminder;
    Integer notification_team_as_team_plays;
    String team;
    String region;

    public TeamNotification(Integer notification_team_morning_reminder, Integer notification_team_as_team_plays, String team, String region) {
        this.notification_team_morning_reminder = notification_team_morning_reminder;
        this.notification_team_as_team_plays = notification_team_as_team_plays;
        this.team = team;
        this.region = region;
    }

    public TeamNotification() {

    }

    public Integer getNotification_team_morning_reminder() {
        return notification_team_morning_reminder;
    }

    public void setNotification_team_morning_reminder(Integer notification_team_morning_reminder) {
        this.notification_team_morning_reminder = notification_team_morning_reminder;
    }

    public Integer getNotification_team_as_team_plays() {
        return notification_team_as_team_plays;
    }

    public void setNotification_team_as_team_plays(Integer notification_team_as_team_plays) {
        this.notification_team_as_team_plays = notification_team_as_team_plays;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
