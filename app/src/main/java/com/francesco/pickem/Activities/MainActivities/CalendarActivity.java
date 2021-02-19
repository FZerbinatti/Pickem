package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Adapters.Region_selection_Adapter_calendar;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity{

    Context context;
    RecyclerView viewPagerRegions;
    private String TAG ="CalendarActivity";
    ProgressBar calendar_progressbar;
    Region_selection_Adapter_calendar adapterRegions;
    String imageRegionPath;
    private ArrayList<String> selectedCalendarRegions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        viewPagerRegions = findViewById(R.id.recyclerview_horizontal_calendarActivity);
        calendar_progressbar = findViewById(R.id.calendar_progressbar);
        context = this;
        imageRegionPath = context.getFilesDir().getAbsolutePath() + (getString(R.string.folder_regions_images));
        calendar_progressbar.setVisibility(View.VISIBLE);

        setupBottomNavView();

        downloadAllRegions();

    }

    private void downloadAllRegions() {




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Matches));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                int int_user_regions_selected = (int) dataSnapshot.getChildrenCount();
                int counter = 0;
                ArrayList <String> allRegions = new ArrayList<>();

                //prendi tutte le regioni
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    counter++;

                    String userRegion = snapshot.getKey();
                    allRegions.add(userRegion);

                }
                if (counter==int_user_regions_selected){
                    //solo quando hai downloddato tutte le user regions carichi il viewpager

                    loadViewPagerRegion(allRegions);
                }



            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    public void loadViewPagerRegion(ArrayList<String> allRegions){
        Log.d(TAG, "loadViewPagerRegion: "+allRegions.size());


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("UserCalendar");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                int int_user_regions_selected = (int) dataSnapshot.getChildrenCount();
                int counter = 0;
                ArrayList <String> userRegions = new ArrayList<>();
                selectedCalendarRegions = new ArrayList<>();
                //prendi tutte le regioni di interesse dello user
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    counter++;
                    String userRegion = snapshot.getValue(String.class);
                    Log.d(TAG, "onDataChange: calendar region: "+userRegion);
                    selectedCalendarRegions.add(userRegion);

                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                viewPagerRegions.setItemViewCacheSize(25);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                viewPagerRegions.setLayoutManager(layoutManager);
                adapterRegions = new Region_selection_Adapter_calendar(allRegions, imageRegionPath, context, selectedCalendarRegions);
                viewPagerRegions.setAdapter(adapterRegions);
                adapterRegions.notifyDataSetChanged();

                calendar_progressbar.setVisibility(View.GONE);

                loadMatchesForThisLeagues(selectedCalendarRegions);

            }
        }, 1000);
    }

    private void loadMatchesForThisLeagues(ArrayList<String> selectedCalendarRegions) {



    }


    private void setupBottomNavView() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.button_calendar);
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