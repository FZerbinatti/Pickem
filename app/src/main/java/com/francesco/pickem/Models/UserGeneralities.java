package com.francesco.pickem.Models;

import java.util.ArrayList;

public class UserGeneralities {

    String email;
    String username;
    ArrayList <String> choosen_regions;

    public UserGeneralities(String email, String username, ArrayList<String> choosen_regions) {
        this.email = email;
        this.username = username;
        this.choosen_regions = choosen_regions;
    }

    public UserGeneralities() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getChoosen_regions() {
        return choosen_regions;
    }

    public void setChoosen_regions(ArrayList<String> choosen_regions) {
        this.choosen_regions = choosen_regions;
    }
}
