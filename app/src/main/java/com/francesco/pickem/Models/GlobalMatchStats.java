package com.francesco.pickem.Models;

public class GlobalMatchStats {

    String ended;
    String winner;
    MatchSingleTeamStats team1;
    MatchSingleTeamStats team2;

    public GlobalMatchStats(String ended, String winner, MatchSingleTeamStats team1, MatchSingleTeamStats team2) {
        this.ended = ended;
        this.winner = winner;
        this.team1 = team1;
        this.team2 = team2;
    }

    public GlobalMatchStats() {

    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
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
}
