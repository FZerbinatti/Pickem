package com.francesco.pickem.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MatchSingleTeamStats {

    Integer barons;
    ArrayList<String> dragons;
    Integer inhibitors;
    HashMap<String, MatchPlayersStats> participant;
    String teamCode;
    Integer totalGold;
    Integer totalKills;
    Integer towers;

    public MatchSingleTeamStats() {

    }

    public MatchSingleTeamStats(Integer barons, ArrayList<String> dragons, Integer inhibitors, HashMap<String, MatchPlayersStats> participant, String teamCode, Integer totalGold, Integer totalKills, Integer towers) {
        this.barons = barons;
        this.dragons = dragons;
        this.inhibitors = inhibitors;
        this.participant = participant;
        this.teamCode = teamCode;
        this.totalGold = totalGold;
        this.totalKills = totalKills;
        this.towers = towers;
    }

    public HashMap<String, MatchPlayersStats> getParticipant() {
        return participant;
    }

    public void setParticipant(HashMap<String, MatchPlayersStats> participant) {
        this.participant = participant;
    }






    public ArrayList<String> getDragons() {
        return dragons;
    }

    public void setDragons(ArrayList<String> dragons) {
        this.dragons = dragons;
    }

    public Integer getBarons() {
        return barons;
    }

    public void setBarons(Integer barons) {
        this.barons = barons;
    }



    public Integer getInhibitors() {
        return inhibitors;
    }

    public void setInhibitors(Integer inhibitors) {
        this.inhibitors = inhibitors;
    }


    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
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
