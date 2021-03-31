package com.francesco.pickem.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class MatchNotification implements Comparable<MatchNotification>{

    private String region;
    private String id;
    private String datetime;
    private String team1;
    private String team2;



    public MatchNotification() {

    }


    public MatchNotification(String region, String id, String datetime, String team1, String team2) {
        this.region = region;
        this.id = id;
        this.datetime = datetime;
        this.team1 = team1;
        this.team2 = team2;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    @Override
    public String toString() {
        return "MatchNotification{" +
                "region='" + region + '\'' +
                ", id='" + id + '\'' +
                ", datetime='" + datetime + '\'' +
                ", team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                '}';
    }

    @Override
    public int compareTo(MatchNotification matchNotification) {
        return 0;
    }

    public  static class ByDatetime implements Comparator<MatchNotification> {

        @Override
        public int compare(MatchNotification t1 , MatchNotification t2) {
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
