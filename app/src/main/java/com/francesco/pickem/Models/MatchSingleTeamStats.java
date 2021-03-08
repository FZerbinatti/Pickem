package com.francesco.pickem.Models;

import java.util.ArrayList;

public class MatchSingleTeamStats {

    Integer barons;
    ArrayList<String> dragons;
    Integer inhibitors;
    ArrayList<MatchPlayersStats> players;
    Integer totalGold;
    Integer totalKills;
    Integer towers;

    public MatchSingleTeamStats() {

    }

    public MatchSingleTeamStats(Integer barons, ArrayList<String> dragons, Integer inhibitors, ArrayList<MatchPlayersStats> players, Integer totalGold, Integer totalKills, Integer towers) {
        this.barons = barons;
        this.dragons = dragons;
        this.inhibitors = inhibitors;
        this.players = players;
        this.totalGold = totalGold;
        this.totalKills = totalKills;
        this.towers = towers;
    }

    public Integer getBarons() {
        return barons;
    }

    public void setBarons(Integer barons) {
        this.barons = barons;
    }

    public ArrayList<String> getDragons() {
        return dragons;
    }

    public void setDragons(ArrayList<String> dragons) {
        this.dragons = dragons;
    }

    public Integer getInhibitors() {
        return inhibitors;
    }

    public void setInhibitors(Integer inhibitors) {
        this.inhibitors = inhibitors;
    }

    public ArrayList<MatchPlayersStats> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<MatchPlayersStats> players) {
        this.players = players;
    }

    public Integer getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(Integer totalGold) {
        this.totalGold = totalGold;
    }

    public Integer getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(Integer totalKills) {
        this.totalKills = totalKills;
    }

    public Integer getTowers() {
        return towers;
    }

    public void setTowers(Integer towers) {
        this.towers = towers;
    }
}
