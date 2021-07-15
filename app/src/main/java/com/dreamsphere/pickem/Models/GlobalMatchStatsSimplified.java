package com.dreamsphere.pickem.Models;

public class GlobalMatchStatsSimplified {

    String region;
    String match_id;
    String ended;
    String winner;
    String year;
    String datetime;
    MatchSingleTeamStats team1;
    MatchSingleTeamStats team2;

    public GlobalMatchStatsSimplified(String region, String match_id, String ended, String winner, String year, String datetime, MatchSingleTeamStats team1, MatchSingleTeamStats team2) {
        this.region = region;
        this.match_id = match_id;
        this.ended = ended;
        this.winner = winner;
        this.year = year;
        this.datetime = datetime;
        this.team1 = team1;
        this.team2 = team2;
    }

    public GlobalMatchStatsSimplified() {

    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public MatchSingleTeamStats getTeam1() {
        return team1;
    }

    public void setTeam1(MatchSingleTeamStats team1) {
        this.team1 = team1;
    }

    public MatchSingleTeamStats getTeam2() {
        return team2;
    }

    public void setTeam2(MatchSingleTeamStats team2) {
        this.team2 = team2;
    }


    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }
}
