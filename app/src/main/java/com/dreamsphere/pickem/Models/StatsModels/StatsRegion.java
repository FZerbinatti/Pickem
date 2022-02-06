package com.dreamsphere.pickem.Models.StatsModels;

import java.util.ArrayList;

public class StatsRegion {

    private ArrayList<StatsRegionYear> statsRegionYearArrayList;

    public StatsRegion(ArrayList<StatsRegionYear> statsRegionYearArrayList) {
        this.statsRegionYearArrayList = statsRegionYearArrayList;
    }

    public StatsRegion() {

    }


    public ArrayList<StatsRegionYear> getStatsRegionYearArrayList() {
        return statsRegionYearArrayList;
    }

    public void setStatsRegionYearArrayList(ArrayList<StatsRegionYear> statsRegionYearArrayList) {
        this.statsRegionYearArrayList = statsRegionYearArrayList;
    }
}
