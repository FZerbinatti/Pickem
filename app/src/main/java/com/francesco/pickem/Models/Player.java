package com.francesco.pickem.Models;

public class Player {

    String firstName;
    String id;
    String image;
    String lastName;
    String role;
    String summonerName;

    public Player() {

    }

    public Player(String firstName, String id, String image, String lastName, String role, String summonerName) {
        this.firstName = firstName;
        this.id = id;
        this.image = image;
        this.lastName = lastName;
        this.role = role;
        this.summonerName = summonerName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }
}
