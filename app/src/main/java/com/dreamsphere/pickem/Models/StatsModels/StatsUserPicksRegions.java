package com.dreamsphere.pickem.Models.StatsModels;

import java.util.ArrayList;

public class StatsUserPicksRegions {

    private ArrayList<StatsRegion> statsUserPicksRegionArrayList;

    public StatsUserPicksRegions(ArrayList<StatsRegion> statsUserPicksRegionArrayList) {
        this.statsUserPicksRegionArrayList = statsUserPicksRegionArrayList;
    }

    public StatsUserPicksRegions() {
    }

    public ArrayList<StatsRegion> getStatsUserPicksRegionArrayList() {
        return statsUserPicksRegionArrayList;
    }

    public void setStatsUserPicksRegionArrayList(ArrayList<StatsRegion> statsUserPicksRegionArrayList) {
        this.statsUserPicksRegionArrayList = statsUserPicksRegionArrayList;
    }
}
