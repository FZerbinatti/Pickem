package com.francesco.pickem.Models;

import com.francesco.pickem.Activities.PicksActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class MatchDayDetails {



    String id;


    public MatchDayDetails(String id) {
        this.id = id;

    }

    public MatchDayDetails() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}
