package com.francesco.pickem.Models;

import java.util.ArrayList;

public class User {

    public String username;
    public String email_adress;
    public ArrayList<String> choosen_regions;

    public User() {
    }

    public User(String username, String email_adress, ArrayList<String> choosen_regions) {
        this.username = username;
        this.email_adress = email_adress;
        this.choosen_regions = choosen_regions;
    }
}
