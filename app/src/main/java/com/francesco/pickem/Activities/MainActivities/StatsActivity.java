package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Activities.EloTracker.EloTrackerActivity;
import com.francesco.pickem.Adapters.RecyclerView_Statistics_Adapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.RegionStats;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class StatsActivity extends AppCompatActivity{
    Context context;
    Button button_manual_elo_tracking;
    ProgressBar stats_progressbar;
    RecyclerView stats_recyclerview;
    DatabaseHelper databaseHelper;
    RecyclerView_Statistics_Adapter adapter_recyclerView_statistics;
    Calendar myCalendar;
    String year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        button_manual_elo_tracking = findViewById(R.id.button_manual_elo_tracking);
        stats_progressbar = findViewById(R.id.stats_progressbar_matches);
        stats_recyclerview = findViewById(R.id.stats_recyclerview);
        databaseHelper = new DatabaseHelper(this);
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));

        stats_progressbar.setVisibility(View.VISIBLE);
        downloadUserRegions();
        context = this;
        setupBottomNavView();
        navigateEloTracker();
    }

    private void navigateEloTracker(){

        button_manual_elo_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatsActivity.this, EloTrackerActivity.class);
                startActivity(intent);
            }
        });

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
            regionStats = databaseHelper.getCorrectPicksPercentageForRegion(year, userRegions.get(i));
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


    private void setupBottomNavView() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.button_statistics);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.button_picks:
                        Intent intentPicks= new Intent(context, PicksActivity.class);
                        startActivity(intentPicks);
                        Animatoo.animateFade(context);
                        break;
                    case R.id.button_calendar:
                        Intent intentCalendar= new Intent(context, CalendarActivity.class);
                        startActivity(intentCalendar);
                        Animatoo.animateFade(context);
                        break;

                    case R.id.button_statistics:
                        Intent intentStats= new Intent(context, StatsActivity.class);
                        startActivity(intentStats);
                        Animatoo.animateFade(context);
                        break;

                    case R.id.button_settings:
                        Intent intentNotif= new Intent(context, SettingsActivity.class);
                        startActivity(intentNotif);
                        Animatoo.animateFade(context);
                        break;
                }
                return true;
            }
        });

    }

}