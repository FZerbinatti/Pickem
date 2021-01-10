package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Adapters.EloTrackerRecyclerViewAdapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.RecyclerItemClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class EloTrackerActivity extends AppCompatActivity {

    Context context;
    ImageButton button_add_elotracker;
    RecyclerView recycler_eloTracker;
    EloTrackerRecyclerViewAdapter adapter;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String userID;
    private String TAG = "EloTRackerActivity";
    ProgressBar elotrack_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elo_tracker);
        context = this;
        addElo();
        recycler_eloTracker = findViewById(R.id.recycler_eloTracker);
        elotrack_progressbar = findViewById(R.id.elotrack_progressbar);

        elotrack_progressbar.setVisibility(View.VISIBLE);

        setupBottomNavView();
        loadFirebaseDataEloTracker();


    }

    private void loadFirebaseDataEloTracker() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        Log.d(TAG, "onClick: year:"+year);

        ArrayList<EloTracker> elotracker_list = new ArrayList<>();

        // load da firebase le regioni
        FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_elotracker))
                .child(String.valueOf(year))
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){


                    EloTracker eloTracker = snapshot.getValue(EloTracker.class);
                    elotracker_list.add(eloTracker);

                }
                loadListview(elotracker_list);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "getAllRegions: regions.size(): "+ elotracker_list.size());



    }

    private void loadListview(ArrayList<EloTracker> eloTrackerArrayList) {


        // set up the RecyclerView

        recycler_eloTracker.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EloTrackerRecyclerViewAdapter(getApplicationContext(), eloTrackerArrayList);
        recycler_eloTracker.setAdapter(adapter);

        elotrack_progressbar.setVisibility(View.GONE);

        recycler_eloTracker.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recycler_eloTracker , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


    }

    private void addElo() {
        button_add_elotracker = findViewById(R.id.button_add_elotracker);
        button_add_elotracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EloTrackerActivity.this, NewTrackEloDay.class);
                startActivity(intent);
            }
        });


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