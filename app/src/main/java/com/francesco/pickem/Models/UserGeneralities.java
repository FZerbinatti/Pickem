package com.francesco.pickem.Models;

import java.util.ArrayList;

public class UserGeneralities {

    String email;
    String summoner_name;
    String server;
    ArrayList <String> choosen_regions;
    String id;


    public UserGeneralities() {

    }

    public UserGeneralities(String email, String summoner_name, String server, ArrayList<String> choosen_regions, String id) {
        this.email = email;
        this.summoner_name = summoner_name;
        this.server = server;
        this.choosen_regions = choosen_regions;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSummoner_name() {
        return summoner_name;
    }

    public void setSummoner_name(String summoner_name) {
        this.summoner_name = summoner_name;
    }

    public ArrayList<String> getChoosen_regions() {
        return choosen_regions;
    }

    public void setChoosen_regions(ArrayList<String> choosen_regions) {
        this.choosen_regions = choosen_regions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
