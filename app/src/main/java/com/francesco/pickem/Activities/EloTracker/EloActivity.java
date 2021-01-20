package com.francesco.pickem.Activities.EloTracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.francesco.pickem.R;

import java.util.ArrayList;

public class EloActivity extends AppCompatActivity {

    Integer int_Challenger=2900;
    Integer int_Grandmaster=2600;
    Integer int_Master=2400;
    Integer int_Diamond_1=2300;
    Integer int_Diamond_2=2200;
    Integer int_Diamond_3=2100;
    Integer int_Diamond_4=2000;
    Integer int_Platinum_1=1900;
    Integer int_Platinum_2=1800;
    Integer int_Platinum_3=1700;
    Integer int_Platinum_4=1600;
    Integer int_Gold_1=1500;
    Integer int_Gold_2=1400;
    Integer int_Gold_3=1300;
    Integer int_Gold_4=1200;
    Integer int_Silver_1=1100;
    Integer int_Silver_2=1000;
    Integer int_Silver_3=900;
    Integer int_Silver_4=800;
    Integer int_Bronze_1=700;
    Integer int_Bronze_2=600;
    Integer int_Bronze_3=500;
    Integer int_Bronze_4=400;
    Integer int_Iron_1=300;
    Integer int_Iron_2=200;
    Integer int_Iron_3=100;
    Integer int_Iron_4=0;

    String Challenger;
    String Grandmaster;
    String Master;
    String Diamond_1;
    String Diamond_2;
    String Diamond_3;
    String Diamond_4;
    String Platinum_1;
    String Platinum_2;
    String Platinum_3;
    String Platinum_4;
    String Gold_1;
    String Gold_2;
    String Gold_3;
    String Gold_4;
    String Silver_1;
    String Silver_2;
    String Silver_3;
    String Silver_4;
    String Bronze_1;
    String Bronze_2;
    String Bronze_3;
    String Bronze_4;
    String Iron_1;
    String Iron_2;
    String Iron_3;
    String Iron_4;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elo);

        context = this;

        Challenger   =getResources().getString(R.string.Challenger);
        Grandmaster  =getResources().getString(R.string.Grandmaster);
        Master       =getResources().getString(R.string.Master);
        Diamond_1    =getResources().getString(R.string.Diamond_1);
        Diamond_2    =getResources().getString(R.string.Diamond_2);
        Diamond_3    =getResources().getString(R.string.Diamond_3);
        Diamond_4    =getResources().getString(R.string.Diamond_4);
        Platinum_1   =getResources().getString(R.string.Platinum_1);
        Platinum_2   =getResources().getString(R.string.Platinum_2);
        Platinum_3   =getResources().getString(R.string.Platinum_3);
        Platinum_4   =getResources().getString(R.string.Platinum_4);
        Gold_1       =getResources().getString(R.string.Gold_1);
        Gold_2       =getResources().getString(R.string.Gold_2);
        Gold_3       =getResources().getString(R.string.Gold_3);
        Gold_4       =getResources().getString(R.string.Gold_4);
        Silver_1     =getResources().getString(R.string.Silver_1);
        Silver_2     =getResources().getString(R.string.Silver_2);
        Silver_3     =getResources().getString(R.string.Silver_3);
        Silver_4     =getResources().getString(R.string.Silver_4);
        Bronze_1     =getResources().getString(R.string.Bronze_1);
        Bronze_2     =getResources().getString(R.string.Bronze_2);
        Bronze_3     =getResources().getString(R.string.Bronze_3);
        Bronze_4     =getResources().getString(R.string.Bronze_4);
        Iron_1       =getResources().getString(R.string.Iron_1);
        Iron_2       =getResources().getString(R.string.Iron_2);
        Iron_3       =getResources().getString(R.string.Iron_3);
        Iron_4       =getResources().getString(R.string.Iron_4);

    }

    public ArrayList<String> getAllElos (Context context){
        ArrayList<String> allElos = new ArrayList<String>();
        allElos.add(context.getResources().getString(R.string.Challenger));
        allElos.add(context.getResources().getString(R.string.Grandmaster));
        allElos.add(context.getResources().getString(R.string.Master));
        allElos.add(context.getResources().getString(R.string.Diamond_1));
        allElos.add(context.getResources().getString(R.string.Diamond_2));
        allElos.add(context.getResources().getString(R.string.Diamond_3));
        allElos.add(context.getResources().getString(R.string.Diamond_4));
        allElos.add(context.getResources().getString(R.string.Platinum_1));
        allElos.add(context.getResources().getString(R.string.Platinum_2));
        allElos.add(context.getResources().getString(R.string.Platinum_3));
        allElos.add(context.getResources().getString(R.string.Platinum_4));
        allElos.add(context.getResources().getString(R.string.Gold_1));
        allElos.add(context.getResources().getString(R.string.Gold_2));
        allElos.add(context.getResources().getString(R.string.Gold_3));
        allElos.add(context.getResources().getString(R.string.Gold_4));
        allElos.add(context.getResources().getString(R.string.Silver_1));
        allElos.add(context.getResources().getString(R.string.Silver_2));
        allElos.add(context.getResources().getString(R.string.Silver_3));
        allElos.add(context.getResources().getString(R.string.Silver_4));
        allElos.add(context.getResources().getString(R.string.Bronze_1));
        allElos.add(context.getResources().getString(R.string.Bronze_2));
        allElos.add(context.getResources().getString(R.string.Bronze_3));
        allElos.add(context.getResources().getString(R.string.Bronze_4));
        allElos.add(context.getResources().getString(R.string.Iron_1));
        allElos.add(context.getResources().getString(R.string.Iron_2));
        allElos.add(context.getResources().getString(R.string.Iron_3));
        allElos.add(context.getResources().getString(R.string.Iron_4));
        return allElos;
    }

    public Integer getEloPoints (String eloName, Context context){

        Log.d("TAG", "getEloPoints: eloName:"+eloName);
        Integer points=0;
        Log.d("TAG", "getEloPoints: context.getResources().getString(R.string.Challenger)"+context.getResources().getString(R.string.Challenger));
        if (eloName.equals(context.getResources().getString(R.string.Challenger))){    points=  getInt_Challenger ();}else
        if (eloName.equals(context.getResources().getString(R.string.Grandmaster))){   points=  getInt_Grandmaster();}else
        if (eloName.equals(context.getResources().getString(R.string.Master))){        points=  getInt_Master     ();}else
        if (eloName.equals(context.getResources().getString(R.string.Diamond_1))){     points=  getInt_Diamond_1  ();}else
        if (eloName.equals(context.getResources().getString(R.string.Diamond_2))){     points=  getInt_Diamond_2  ();}else
        if (eloName.equals(context.getResources().getString(R.string.Diamond_3))){     points=  getInt_Diamond_3  ();}else
        if (eloName.equals(context.getResources().getString(R.string.Diamond_4))){     points=  getInt_Diamond_4  ();}else
        if (eloName.equals(context.getResources().getString(R.string.Platinum_1))){    points=  getInt_Platinum_1 ();}else
        if (eloName.equals(context.getResources().getString(R.string.Platinum_2))){    points=  getInt_Platinum_2 ();}else
        if (eloName.equals(context.getResources().getString(R.string.Platinum_3))){    points=  getInt_Platinum_3 ();}else
        if (eloName.equals(context.getResources().getString(R.string.Platinum_4))){    points=  getInt_Platinum_4 ();}else
        if (eloName.equals(context.getResources().getString(R.string.Gold_1))){        points=  getInt_Gold_1     ();}else
        if (eloName.equals(context.getResources().getString(R.string.Gold_2))){        points=  getInt_Gold_2     ();}else
        if (eloName.equals(context.getResources().getString(R.string.Gold_3))){        points=  getInt_Gold_3     ();}else
        if (eloName.equals(context.getResources().getString(R.string.Gold_4))){        points=  getInt_Gold_4     ();}else
        if (eloName.equals(context.getResources().getString(R.string.Silver_1))){      points=  getInt_Silver_1   ();}else
        if (eloName.equals(context.getResources().getString(R.string.Silver_2))){      points=  getInt_Silver_2   ();}else
        if (eloName.equals(context.getResources().getString(R.string.Silver_3))){      points=  getInt_Silver_3   ();}else
        if (eloName.equals(context.getResources().getString(R.string.Silver_4))){      points=  getInt_Silver_4   ();}else
        if (eloName.equals(context.getResources().getString(R.string.Bronze_1))){      points=  getInt_Bronze_1   ();}else
        if (eloName.equals(context.getResources().getString(R.string.Bronze_2))){      points=  getInt_Bronze_2   ();}else
        if (eloName.equals(context.getResources().getString(R.string.Bronze_3))){      points=  getInt_Bronze_3   ();}else
        if (eloName.equals(context.getResources().getString(R.string.Bronze_4))){      points=  getInt_Bronze_4   ();}else
        if (eloName.equals(context.getResources().getString(R.string.Iron_1))){        points=  getInt_Iron_1     ();}else
        if (eloName.equals(context.getResources().getString(R.string.Iron_2))){        points=  getInt_Iron_2     ();}else
        if (eloName.equals(context.getResources().getString(R.string.Iron_3))){        points=  getInt_Iron_3     ();}else
        if (eloName.equals(context.getResources().getString(R.string.Iron_4))){        points=  getInt_Iron_4     ();}

        Log.d("TAG", "getEloPoints: points:"+points);
            return points;


    }

    public String getChallenger() {
        return Challenger;
    }
    public String getGrandmaster() {
        return Grandmaster;
    }
    public String getMaster() {
        return Master;
    }
    public String getDiamond_1() {
        return Diamond_1;
    }
    public String getDiamond_2() {
        return Diamond_2;
    }
    public String getDiamond_3() {
        return Diamond_3;
    }
    public String getDiamond_4() {
        return Diamond_4;
    }
    public String getPlatinum_1() {
        return Platinum_1;
    }
    public String getPlatinum_2() {
        return Platinum_2;
    }
    public String getPlatinum_3() {
        return Platinum_3;
    }
    public String getPlatinum_4() {
        return Platinum_4;
    }
    public String getGold_1() {
        return Gold_1;
    }
    public String getGold_2() {
        return Gold_2;
    }
    public String getGold_3() {
        return Gold_3;
    }
    public String getGold_4() {
        return Gold_4;
    }
    public String getSilver_1() {
        return Silver_1;
    }
    public String getSilver_2() {
        return Silver_2;
    }
    public String getSilver_3() {
        return Silver_3;
    }
    public String getSilver_4() {
        return Silver_4;
    }
    public String getBronze_1() {
        return Bronze_1;
    }
    public String getBronze_2() {
        return Bronze_2;
    }
    public String getBronze_3() {
        return Bronze_3;
    }
    public String getBronze_4() {
        return Bronze_4;
    }
    public String getIron_1() {
        return Iron_1;
    }
    public String getIron_2() {
        return Iron_2;
    }
    public String getIron_3() {
        return Iron_3;
    }
    public String getIron_4() {
        return Iron_4;
    }
    public Integer getInt_Challenger() {
        return int_Challenger;
    }
    public Integer getInt_Grandmaster() {
        return int_Grandmaster;
    }
    public Integer getInt_Master() {
        return int_Master;
    }
    public Integer getInt_Diamond_1() {
        return int_Diamond_1;
    }
    public Integer getInt_Diamond_2() {
        return int_Diamond_2;
    }
    public Integer getInt_Diamond_3() {
        return int_Diamond_3;
    }
    public Integer getInt_Diamond_4() {
        return int_Diamond_4;
    }
    public Integer getInt_Platinum_1() {
        return int_Platinum_1;
    }
    public Integer getInt_Platinum_2() {
        return int_Platinum_2;
    }
    public Integer getInt_Platinum_3() {
        return int_Platinum_3;
    }
    public Integer getInt_Platinum_4() {
        return int_Platinum_4;
    }
    public Integer getInt_Gold_1() {
        return int_Gold_1;
    }
    public Integer getInt_Gold_2() {
        return int_Gold_2;
    }
    public Integer getInt_Gold_3() {
        return int_Gold_3;
    }
    public Integer getInt_Gold_4() {
        return int_Gold_4;
    }
    public Integer getInt_Silver_1() {
        return int_Silver_1;
    }
    public Integer getInt_Silver_2() {
        return int_Silver_2;
    }
    public Integer getInt_Silver_3() {
        return int_Silver_3;
    }
    public Integer getInt_Silver_4() {
        return int_Silver_4;
    }
    public Integer getInt_Bronze_1() {
        return int_Bronze_1;
    }
    public Integer getInt_Bronze_2() {
        return int_Bronze_2;
    }
    public Integer getInt_Bronze_3() {
        return int_Bronze_3;
    }
    public Integer getInt_Bronze_4() {
        return int_Bronze_4;
    }
    public Integer getInt_Iron_1() {
        return int_Iron_1;
    }
    public Integer getInt_Iron_2() {
        return int_Iron_2;
    }
    public Integer getInt_Iron_3() {
        return int_Iron_3;
    }
    public Integer getInt_Iron_4() {
        return int_Iron_4;
    }
}