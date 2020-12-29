package com.francesco.pickem.Models;

public class TeamDetails {

    private int image;
    private String league_name;

    public TeamDetails(int image, String league_name) {
        this.image = image;
        this.league_name = league_name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLeague_name() {
        return league_name;
    }

    public void setLeague_name(String league_name) {
        this.league_name = league_name;
    }
}
