package com.dreamsphere.pickem.Models;

public class MatchPrediction {

    Integer match_id;
    String match_prediction;

    public MatchPrediction() {

    }

    public MatchPrediction(Integer match_id, String match_prediction) {
        this.match_id = match_id;
        this.match_prediction = match_prediction;
    }

    public Integer getMatch_id() {
        return match_id;
    }

    public void setMatch_id(Integer match_id) {
        this.match_id = match_id;
    }

    public String getMatch_prediction() {
        return match_prediction;
    }

    public void setMatch_prediction(String match_prediction) {
        this.match_prediction = match_prediction;
    }
}
