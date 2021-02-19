package com.francesco.pickem.Models;

public class RegionSelected {

    String region;
    Integer selected;

    public RegionSelected(String region, Integer selected) {
        this.region = region;
        this.selected = selected;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }
}
