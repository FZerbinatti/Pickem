package com.dreamsphere.pickem.Models;

public class RegionDetails {

    private String id;
    private String image;
    private String name;
    private Integer priority;
    private String region;
    private String slug;

    public RegionDetails() {

    }

    public RegionDetails(String id, String image, String name, Integer priority, String region, String slug) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.priority = priority;
        this.region = region;
        this.slug = slug;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
