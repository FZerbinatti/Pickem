package com.francesco.pickem.Models;

import java.util.ArrayList;

public class RegionNotifications {

    String region_name;
    Integer notification_first_match_otd;
    Integer notification_morning_reminder;
    Integer no_choice_made;

    public RegionNotifications(String region_name, Integer notification_first_match_otd, Integer notification_morning_reminder, Integer no_choice_made) {
        this.region_name = region_name;
        this.notification_first_match_otd = notification_first_match_otd;
        this.notification_morning_reminder = notification_morning_reminder;
        this.no_choice_made = no_choice_made;
    }

    public RegionNotifications() {

    }


    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
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

    public Integer getNo_choice_made() {
        return no_choice_made;
    }

    public void setNo_choice_made(Integer no_choice_made) {
        this.no_choice_made = no_choice_made;
    }
}
