package com.dreamsphere.pickem.Models.StatsModels;

public class Prediction {

    private String prediction;

    public Prediction(String prediction) {
        this.prediction = prediction;
    }

    public Prediction() {

    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }
}
