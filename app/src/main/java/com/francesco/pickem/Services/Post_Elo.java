package com.francesco.pickem.Services;



public class Post_Elo {

           String leagueId;
           String queueType;
           String tier;
           String rank;
           String summonerId;
           String summonerName;
           Integer leaguePoints;
           Integer wins;
           Integer losses;
           Boolean veteran;
           Boolean inactive;
           Boolean freshBlood;
           Boolean hotStreak;

    public String getLeagueId() {
        return leagueId;
    }

    public String getQueueType() {
        return queueType;
    }

    public String getTier() {
        return tier;
    }

    public String getRank() {
        return rank;
    }

    public String getSummonerId() {
        return summonerId;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public Integer getLeaguePoints() {
        return leaguePoints;
    }

    public Integer getWins() {
        return wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public Boolean getVeteran() {
        return veteran;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public Boolean getFreshBlood() {
        return freshBlood;
    }

    public Boolean getHotStreak() {
        return hotStreak;
    }
}
