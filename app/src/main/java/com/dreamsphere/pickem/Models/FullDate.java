package com.dreamsphere.pickem.Models;

public class FullDate {

    String id;
    String localDateTime;
    String date;
    String time;
    String coolDate;

    public FullDate() {

    }

    public FullDate(String id, String localDateTime, String date, String time, String coolDate) {
        this.id = id;
        this.localDateTime = localDateTime;
        this.date = date;
        this.time = time;
        this.coolDate = coolDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCoolDate() {
        return coolDate;
    }

    public void setCoolDate(String coolDate) {
        this.coolDate = coolDate;
    }
}
