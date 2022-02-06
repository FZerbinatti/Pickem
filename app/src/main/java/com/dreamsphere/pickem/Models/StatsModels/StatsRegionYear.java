package com.dreamsphere.pickem.Models.StatsModels;

import java.util.ArrayList;

public class StatsRegionYear {

    private ArrayList <Prediction> predictionsArrayList;

    public StatsRegionYear(ArrayList<Prediction> predictionsArrayList) {
        this.predictionsArrayList = predictionsArrayList;
    }

    public StatsRegionYear() {

    }

    public ArrayList<Prediction> getPredictionsArrayList() {
        return predictionsArrayList;
    }

    public void setPredictionsArrayList(ArrayList<Prediction> predictionsArrayList) {
        this.predictionsArrayList = predictionsArrayList;
    }
}
