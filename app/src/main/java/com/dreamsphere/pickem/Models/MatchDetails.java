package com.dreamsphere.pickem.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class MatchDetails implements Comparable<MatchDetails>{

    private String id;
    private String datetime;
    private String team1;
    private String team2;
    private String winner;
    private Long team1_score;
    private Long team2_score;
    private String team1_image;
    private String team2_image;
    private String state;



    public MatchDetails() {

    }

    public MatchDetails(String id, String datetime, String team1, String team2, String winner, Long team1_score, Long team2_score, String team1_image, String team2_image) {
        this.id = id;
        this.datetime = datetime;
        this.team1 = team1;
        this.team2 = team2;
        this.winner = winner;
        this.team1_score = team1_score;
        this.team2_score = team2_score;
        this.team1_image = team1_image;
        this.team2_image = team2_image;
    }

    public MatchDetails(String id, String datetime, String team1, String team2, String winner, Long team1_score, Long team2_score) {
        this.id = id;
        this.datetime = datetime;
        this.team1 = team1;
        this.team2 = team2;
        this.winner = winner;
        this.team1_score = team1_score;
        this.team2_score = team2_score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Long getTeam1_score() {
        return team1_score;
    }

    public void setTeam1_score(Long team1_score) {
        this.team1_score = team1_score;
    }

    public Long getTeam2_score() {
        return team2_score;
    }

    public void setTeam2_score(Long team2_score) {
        this.team2_score = team2_score;
    }

    public String getTeam1_image() {
        return team1_image;
    }

    public void setTeam1_image(String team1_image) {
        this.team1_image = team1_image;
    }

    public String getTeam2_image() {
        return team2_image;
    }

    public void setTeam2_image(String team2_image) {
        this.team2_image = team2_image;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "MatchDetails{" +
                "id='" + id + '\'' +
                ", datetime='" + datetime + '\'' +
                ", team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                ", winner='" + winner + '\'' +
                ", team1_score=" + team1_score +
                ", team2_score=" + team2_score +
                '}';
    }

    @Override
    public int compareTo(MatchDetails matchDetails) {
        return 0;
    }

        public  static class ByDatetime implements Comparator <MatchDetails>{

        @Override
        public int compare(MatchDetails t1 ,MatchDetails t2) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date matchDateTime1 = null;
            Date matchDateTime2 = null;
            try {
                matchDateTime1 = sdf.parse(t1.getDatetime());
                matchDateTime2 = sdf.parse(t2.getDatetime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return matchDateTime2.before(matchDateTime1) ? 1 :(matchDateTime1.before(matchDateTime2) ? -1 :0);
            }
        }
}
