package com.dreamsphere.pickem.Models;

public class MatchPlayersStats {

    Integer assists;
    String championName;
    Integer creepScore;
    Integer deaths;
    Integer kills;
    Integer level;
    String role;
    String summonerName;
    Integer totalGold;

    public MatchPlayersStats() {

    }

    public MatchPlayersStats(Integer assists, String championName, Integer creepScore, Integer deaths, Integer kills, Integer level, String role, String summonerName, Integer totalGold) {
        this.assists = assists;
        this.championName = championName;
        this.creepScore = creepScore;
        this.deaths = deaths;
        this.kills = kills;
        this.level = level;
        this.role = role;
        this.summonerName = summonerName;
        this.totalGold = totalGold;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public String getChampionName() {
        return championName;
    }

    public void setChampionName(String championName) {
        this.championName = championName;
    }

    public Integer getCreepScore() {
        return creepScore;
    }

    public void setCreepScore(Integer creepScore) {
        this.creepScore = creepScore;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public Integer getKills() {
        return kills;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    public Integer getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(Integer totalGold) {
        this.totalGold = totalGold;
    }
}
