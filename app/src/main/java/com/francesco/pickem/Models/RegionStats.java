package com.francesco.pickem.Models;

public class RegionStats {

    String regionName;
    Integer correctPicks;
    Integer totalPicks;

    public RegionStats(String regionName, Integer correctPicks, Integer totalPicks) {
        this.regionName = regionName;
        this.correctPicks = correctPicks;
        this.totalPicks = totalPicks;
    }

    public RegionStats() {

    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Integer getCorrectPicks() {
        return correctPicks;
    }

    public void setCorrectPicks(Integer correctPicks) {
        this.correctPicks = correctPicks;
    }

    public Integer getTotalPicks() {
        return totalPicks;
    }

    public void setTotalPicks(Integer totalPicks) {
        this.totalPicks = totalPicks;
    }
}
