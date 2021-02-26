package com.francesco.pickem.Models;

import java.util.ArrayList;

public class UserGeneralities {

    String email;
    String summoner_name;
    String summoner_server;
    Integer summoner_elotracker;
    ArrayList <String> choosen_regions;
    String id;


    public UserGeneralities() {

    }


    public UserGeneralities(String email, String summoner_name, String summoner_server, Integer summoner_elotracker, ArrayList<String> choosen_regions, String id) {
        this.email = email;
        this.summoner_name = summoner_name;
        this.summoner_server = summoner_server;
        this.summoner_elotracker = summoner_elotracker;
        this.choosen_regions = choosen_regions;
        this.id = id;
    }

    public String getSummoner_server() {
        return summoner_server;
    }

    public void setSummoner_server(String summoner_server) {
        this.summoner_server = summoner_server;
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

    public Integer getSummoner_elotracker() {
        return summoner_elotracker;
    }

    public void setSummoner_elotracker(Integer summoner_elotracker) {
        this.summoner_elotracker = summoner_elotracker;
    }
}
