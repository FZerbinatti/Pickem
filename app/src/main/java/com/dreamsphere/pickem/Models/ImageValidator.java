package com.dreamsphere.pickem.Models;

public class ImageValidator {

    String name;
    Long date;

    public ImageValidator() {
    }

    public ImageValidator(String name, Long date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
