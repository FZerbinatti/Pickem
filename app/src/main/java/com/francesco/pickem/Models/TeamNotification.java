package com.francesco.pickem.Models;

public class TeamNotification {


    Integer notification_team_morning_reminder;
    Integer notification_team_as_team_plays;
    String team_name;

    public TeamNotification(Integer notification_team_morning_reminder, Integer notification_team_as_team_plays, String team_name) {
        this.notification_team_morning_reminder = notification_team_morning_reminder;
        this.notification_team_as_team_plays = notification_team_as_team_plays;
        this.team_name = team_name;
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

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }
}
