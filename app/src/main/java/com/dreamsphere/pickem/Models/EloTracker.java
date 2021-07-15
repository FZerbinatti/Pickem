package com.dreamsphere.pickem.Models;

public class EloTracker {

    String  ID;
    String  date;
    String  elo;
    Integer lps;


    public EloTracker() {
    }

    public EloTracker(String ID, String date, String elo, Integer lps) {
        this.ID = ID;
        this.date = date;
        this.elo = elo;
        this.lps = lps;

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getElo() {
        return elo;
    }

    public void setElo(String elo) {
        this.elo = elo;
    }

    public Integer getLps() {
        return lps;
    }

    public void setLps(Integer lps) {
        this.lps = lps;
    }


}
