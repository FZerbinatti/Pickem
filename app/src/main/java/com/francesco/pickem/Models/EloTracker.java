package com.francesco.pickem.Models;

public class EloTracker {

    String  ID;
    String  date;
    Integer wins;
    Integer losses;
    String  elo;
    Integer lps;

    public EloTracker() {
    }

    public EloTracker(String ID, String date, Integer wins, Integer losses, String elo, Integer lps) {
        this.ID = ID;
        this.date = date;
        this.wins = wins;
        this.losses = losses;
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

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
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
