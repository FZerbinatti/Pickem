package com.dreamsphere.pickem.Models;

public class RegionNotifications {


    Integer no_choice_made;
    Integer notification_first_match_otd;
    Integer notification_morning_reminder;
    String region_name;



    public RegionNotifications() {

    }

    public RegionNotifications(Integer no_choice_made, Integer notification_first_match_otd, Integer notification_morning_reminder, String region_name) {
        this.no_choice_made = no_choice_made;
        this.notification_first_match_otd = notification_first_match_otd;
        this.notification_morning_reminder = notification_morning_reminder;
        this.region_name = region_name;
    }

    public RegionNotifications(Integer no_choice_made, Integer notification_first_match_otd, Integer notification_morning_reminder) {
        this.no_choice_made = no_choice_made;
        this.notification_first_match_otd = notification_first_match_otd;
        this.notification_morning_reminder = notification_morning_reminder;

    }

    public Integer getNo_choice_made() {
        return no_choice_made;
    }

    public void setNo_choice_made(Integer no_choice_made) {
        this.no_choice_made = no_choice_made;
    }

    public Integer getNotification_first_match_otd() {
        return notification_first_match_otd;
    }

    public void setNotification_first_match_otd(Integer notification_first_match_otd) {
        this.notification_first_match_otd = notification_first_match_otd;
    }

    public Integer getNotification_morning_reminder() {
        return notification_morning_reminder;
    }

    public void setNotification_morning_reminder(Integer notification_morning_reminder) {
        this.notification_morning_reminder = notification_morning_reminder;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }
}
