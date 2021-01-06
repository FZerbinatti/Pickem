package com.francesco.pickem.Models;

public class TeamDetails {

    private String code;
    private int id;
    private String name;
    private String image;

    public TeamDetails() {
    }

    public TeamDetails(String code, int id, String name, String image) {
        this.code = code;
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
