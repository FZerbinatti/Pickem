package com.francesco.pickem.Models;

public class RegionDetails {

    private Integer region_ID;
    private String region_logo;
    private String region_name;
    private String region_name_ext;

    public RegionDetails(Integer region_ID, String region_logo, String region_name, String region_name_ext) {
        this.region_ID = region_ID;
        this.region_logo = region_logo;
        this.region_name = region_name;
        this.region_name_ext = region_name_ext;
    }

    public RegionDetails() {

    }

    public Integer getRegion_ID() {
        return region_ID;
    }

    public void setRegion_ID(Integer region_ID) {
        this.region_ID = region_ID;
    }

    public String getRegion_logo() {
        return region_logo;
    }

    public void setRegion_logo(String region_logo) {
        this.region_logo = region_logo;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public String getRegion_name_ext() {
        return region_name_ext;
    }

    public void setRegion_name_ext(String region_name_ext) {
        this.region_name_ext = region_name_ext;
    }
}
