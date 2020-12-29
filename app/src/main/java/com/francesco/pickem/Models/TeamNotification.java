package com.francesco.pickem.Models;

public class TeamNotification {

    String team_name;
    Integer morning_reminder;
    Integer as_team_plays_reminder;

    public TeamNotification(String team_name, Integer morning_reminder, Integer as_team_plays_reminder) {
        this.team_name = team_name;
        this.morning_reminder = morning_reminder;
        this.as_team_plays_reminder = as_team_plays_reminder;
    }

    public TeamNotification() {

    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public Integer getMorning_reminder() {
        return morning_reminder;
    }

    public void setMorning_reminder(Integer morning_reminder) {
        this.morning_reminder = morning_reminder;
    }

    public Integer getAs_team_plays_reminder() {
        return as_team_plays_reminder;
    }

    public void setAs_team_plays_reminder(Integer as_team_plays_reminder) {
        this.as_team_plays_reminder = as_team_plays_reminder;
    }
}
