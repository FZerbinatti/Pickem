package com.francesco.pickem.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class ItemAnalistRecyclerVIew implements Comparable<ItemAnalistRecyclerVIew>{

    String datetime;
    String team1;
    String team2;
    String prediction;

    public ItemAnalistRecyclerVIew() {

    }

    public ItemAnalistRecyclerVIew(String datetime, String team1, String team2, String prediction) {
        this.datetime = datetime;
        this.team1 = team1;
        this.team2 = team2;
        this.prediction = prediction;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
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
        return "ItemAnalistRecyclerVIew{" +
                "datetime='" + datetime + '\'' +
                ", team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                ", prediction='" + prediction + '\'' +
                '}';
    }


    @Override
    public int compareTo(ItemAnalistRecyclerVIew itemAnalistRecyclerVIew) {
        return 0;
    }

    public  static class ByDatetime implements Comparator<ItemAnalistRecyclerVIew> {

        @Override
        public int compare(ItemAnalistRecyclerVIew t1 ,ItemAnalistRecyclerVIew t2) {
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
