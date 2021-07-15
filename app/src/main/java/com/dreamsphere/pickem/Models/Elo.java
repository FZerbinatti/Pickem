package com.dreamsphere.pickem.Models;

public class Elo {

    String elo_id;
    String elo_name;
    String elo_image;
    String elo_tier;

    public Elo( String elo_name, String elo_image, String elo_tier) {
        this.elo_name = elo_name;
        this.elo_image = elo_image;
        this.elo_tier = elo_tier;
    }

    public String getElo_id() {
        return elo_id;
    }

    public void setElo_id(String elo_id) {
        this.elo_id = elo_id;
    }

    public String getElo_name() {
        return elo_name;
    }

    public void setElo_name(String elo_name) {
        this.elo_name = elo_name;
    }

    public String getElo_image() {
        return elo_image;
    }

    public void setElo_image(String elo_image) {
        this.elo_image = elo_image;
    }

    public String getElo_tier() {
        return elo_tier;
    }

    public void setElo_tier(String elo_tier) {
        this.elo_tier = elo_tier;
    }
}
