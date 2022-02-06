package com.dreamsphere.pickem.Activities.Statistics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dreamsphere.pickem.Adapters.RecyclerView_Statistics_Adapter;
import com.dreamsphere.pickem.Models.RegionStats;
import com.dreamsphere.pickem.Models.StatsModels.StatsUserPicksRegions;
import com.dreamsphere.pickem.R;
import com.dreamsphere.pickem.Services.AndroidDatabaseManager;
import com.dreamsphere.pickem.Services.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

public class StatsPicksActivity extends AppCompatActivity{
    private static final String TAG = "Stats Activity";
    Context context;
    Button button_manual_elo_tracking;
    ProgressBar stats_progressbar;
    RecyclerView stats_recyclerview;
    DatabaseHelper databaseHelper;
    RecyclerView_Statistics_Adapter adapter_recyclerView_statistics;
    Calendar myCalendar;
    String currentYear;
    TextView global_statistic_item_correct , global_statistic_item_totals, global_statistic_item_percentage;
    ProgressBar global_progressbar_statistics_item;
    TextView textView5;
    AndroidDatabaseManager adb;
    ImageView reload;
    StatsUserPicksRegions statsUserPicksRegions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stattistics_picks);

        stats_progressbar = findViewById(R.id.stats_progressbar_matches);
        stats_recyclerview = findViewById(R.id.stats_recyclerview);
        reload = findViewById(R.id.icon_reload);

        global_statistic_item_correct = findViewById(R.id.global_statistic_item_correct);
        global_statistic_item_totals = findViewById(R.id.global_statistic_item_totals);
        global_statistic_item_percentage = findViewById(R.id.global_statistic_item_percentage);
        global_progressbar_statistics_item = findViewById(R.id.global_progressbar_statistics_item);

        textView5=findViewById(R.id.textView5);


        databaseHelper = new DatabaseHelper(this);
        myCalendar = Calendar.getInstance();
        currentYear = String.valueOf(myCalendar.get(Calendar.YEAR));
        Log.d(TAG, "onCreate: current year: "+ currentYear);

        globalStats(currentYear);

        stats_progressbar.setVisibility(View.VISIBLE);
        downloadUserRegions();
        context = this;


        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adb= new AndroidDatabaseManager();
                Intent intent= new Intent(StatsPicksActivity.this, AndroidDatabaseManager.class);
                startActivity(intent);

            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //scarica tutte le prediction dello user
                downloadUserStats();
                //update il database locale
                //ricarica la pagina
            }
        });
    }

    private void downloadUserStats() {
        // prende tutti i dati della sezione userPrediction, li splitta, li filtra per anno e fa l'update del database locale
        statsUserPicksRegions = new StatsUserPicksRegions();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_picks));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                    //Log.d(TAG, "onDataChange: dataSnapshot1.getKey():"+dataSnapshot1.getKey());
                        for (DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){
                            //Log.d(TAG, "onDataChange: dataSnapshot2.getKey():"+dataSnapshot2.getKey());
                            String region = dataSnapshot2.getKey().split("202")[0];
                            String year =dataSnapshot2.getKey().split("202")[1];
                                if (year.equals("2")){
                                    //Log.d(TAG, "onDataChange: "+dataSnapshot2);
                                    for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()){
                                        String match_ID= dataSnapshot3.getKey().toString();
                                        String userPrediction = dataSnapshot3.getValue(String.class);
                                        Log.d(TAG, "onDataChange: match id: "+match_ID + " userpredcition: "+userPrediction);
                                        databaseHelper.updatePrediction(region, match_ID, userPrediction);
                                    }
                                }
                        }
                }
                //finito l'update del db locale, ricarica la pagina
                globalStats(currentYear);
                downloadUserRegions();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    private void globalStats(String year) {
        RegionStats globalStats = databaseHelper.getGlobalStats(year);
        Integer correctPicks = globalStats.getCorrectPicks();
        Integer totalPicks = globalStats.getTotalPicks();






        float percentage =0f;
        BigDecimal bd = BigDecimal.valueOf(0);
        if (correctPicks>0){

            percentage =  ( (float) correctPicks/totalPicks)*100f;
            bd = new BigDecimal(Float.toString(percentage));
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

        }


        global_progressbar_statistics_item.setProgress(Math.round(percentage));
        ColorStateList colorStateList = ContextCompat.getColorStateList(global_progressbar_statistics_item.getContext(), progressBarColor(Math.round(percentage)));
        global_progressbar_statistics_item.setProgressTintList(colorStateList);

        global_statistic_item_correct.setText(correctPicks.toString()+"/");
        global_statistic_item_percentage.setText(String.valueOf(bd)+"%");
        global_statistic_item_totals.setText(totalPicks.toString());

    }


    //RecyclerView con le regioni selected
    private void downloadUserRegions() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_generealities))
                .child(getString(R.string.firebase_user_choosen_regions));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                int int_user_regions_selected = (int) dataSnapshot.getChildrenCount();
                int counter = 0;
                ArrayList<String> userRegions = new ArrayList<>();

                //prendi tutte le regioni di interesse dello user
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    counter++;
                    String userRegion = snapshot.getValue(String.class);
                    userRegions.add(userRegion);

                }
                if (counter==int_user_regions_selected){
                    //solo quando hai downloddato tutte le user regions carichi il viewpager
                    stats_progressbar.setVisibility(View.INVISIBLE);
                    loadStatsForUserRegions(userRegions);
                }



            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadStatsForUserRegions(ArrayList<String> userRegions) {
        ArrayList <RegionStats> listOfRegionStats = new ArrayList<>();
        RegionStats regionStats = new RegionStats();

        for(int i=0; i<userRegions.size(); i++){
            regionStats = databaseHelper.getRegionStats(currentYear, userRegions.get(i));
            listOfRegionStats.add(regionStats);
        }
        loadRecyclerViewStats(listOfRegionStats);
    }

    private void loadRecyclerViewStats(ArrayList<RegionStats> listOfRegionStats) {


        adapter_recyclerView_statistics = new RecyclerView_Statistics_Adapter(this, listOfRegionStats);
        adapter_recyclerView_statistics.notifyDataSetChanged();
        stats_recyclerview.setLayoutManager(new LinearLayoutManager(this));


        stats_recyclerview.setAdapter(adapter_recyclerView_statistics);

        stats_progressbar.setVisibility(View.GONE);

    }

    private Integer progressBarColor(Integer potenza_segnale){

        if (potenza_segnale < 20 ){
            return (R.color.r1);
        }else if (potenza_segnale >= 20 && potenza_segnale <40){
            return (R.color.r2);
        }else if (potenza_segnale >= 40 && potenza_segnale <55){
            return (R.color.y1);
        } else if (potenza_segnale >= 55 && potenza_segnale <70){
            return (R.color.y2);
        }else if (potenza_segnale >= 70 && potenza_segnale <80){
            return (R.color.g1);
        }else if (potenza_segnale >= 80 && potenza_segnale <90){
            return (R.color.g2);
        }else if (potenza_segnale >=90){
            return (R.color.g3);
        }else return R.color.transparent;
    }




}