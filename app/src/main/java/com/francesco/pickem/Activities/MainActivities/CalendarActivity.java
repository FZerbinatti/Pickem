package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Adapters.RecyclerView_Calendar_Adapter;
import com.francesco.pickem.Adapters.RecyclerView_Picks_Adapter;
import com.francesco.pickem.Adapters.Region_selection_Adapter_calendar;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.CurrentNumber;
import com.francesco.pickem.Models.CurrentTeam;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarActivity extends AppCompatActivity{

    Context context;
    RecyclerView viewPagerRegions;
    private String TAG ="CalendarActivity";
    ProgressBar calendar_progressbar;
    Region_selection_Adapter_calendar adapterRegions;
    String imageRegionPath;
    private ArrayList<String> selectedCalendarRegions;
    DatabaseHelper databaseHelper;
    SwitchCompat switch_day_week;
    ImageButton previous, next, choose_date_calendar;
    Boolean switch_is_day;
    String selectedDate;
    Calendar myCalendar;
    String year;
    RecyclerView calendar_recyclerView;
    RecyclerView_Calendar_Adapter adapterRecycler;
    SwipeRefreshLayout pullToRefreshCalendar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        viewPagerRegions = findViewById(R.id.recyclerview_horizontal_calendarActivity);
        calendar_progressbar = findViewById(R.id.calendar_progressbar);
        switch_day_week = findViewById(R.id.switch_calendar);
        previous = findViewById(R.id.calendar_previous);
        next = findViewById(R.id.calendar_next);
        choose_date_calendar = findViewById(R.id.choose_date_calendar);
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));
        calendar_recyclerView = findViewById(R.id.calendar_recyclerView);
        pullToRefreshCalendar = findViewById(R.id.pullToRefreshCalendar);

        switch_is_day = true;
        selectedDate = "";



        context = this;
        imageRegionPath = context.getFilesDir().getAbsolutePath() + (getString(R.string.folder_regions_images));
        calendar_progressbar.setVisibility(View.VISIBLE);
        databaseHelper = new DatabaseHelper(this);

        setupBottomNavView();
        downloadAllRegions();

        switch_day_week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    switch_is_day = false;
                    Log.d(TAG, "onCheckedChanged: switch_day:"+ switch_is_day);
                }else {
                    switch_is_day = true;
                    Log.d(TAG, "onCheckedChanged: switch_day:"+ switch_is_day);
                }
            }
        });
        
        
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: previous");
            }
        });
        
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: next");
            }
        });

        pullToRefreshCalendar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMatchesForThisLeagues(selectedCalendarRegions);
                pullToRefreshCalendar.setRefreshing(false);
            }
        });

    }

    private void downloadAllRegions() {
        
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Matches));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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

        // prendi il valore dello switch
        if (switch_is_day){
            //se sono in modalità di visualizzazione only day , prendi le region, il current day (se "" -> today)
            if (selectedDate.equals("")){
                selectedDate = getTodayDate();
            }

            ArrayList <MatchDetails> allMatchDetailsForDate = new ArrayList<>();
            //scarica tutti i match ID di tutte le regioni selezionate, filtra quelli che corrispondono alla data selezionata
            CurrentNumber sum_all_matches_all_regions = new CurrentNumber();
            CurrentNumber global_counter = new CurrentNumber();
            sum_all_matches_all_regions.setNumber(0);
            global_counter.setNumber(0);
            for(int i=0; i<selectedCalendarRegions.size(); i++){

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Matches))
                        .child(selectedCalendarRegions.get(i))
                        .child(selectedCalendarRegions.get(i)+year);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                        int int_user_regions_selected = (int) dataSnapshot.getChildrenCount();
                        sum_all_matches_all_regions.setNumber(sum_all_matches_all_regions.getNumber()+int_user_regions_selected);
                        Log.d(TAG, "onDataChange: sum_all_matches_all_regions: "+sum_all_matches_all_regions.getNumber());


                        CurrentTeam currentDate = new CurrentTeam();
                        currentDate.setRegion("");
                        //prendi tutte le regioni
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            global_counter.setNumber(global_counter.getNumber()+1);
                            Log.d(TAG, "onDataChange: global_counter:"+global_counter.getNumber() + "/" +sum_all_matches_all_regions.getNumber());
                            String match_ID = snapshot.getKey();
                            // solo se la data corrisponde allora scarichi i dettagli
                            String localDate = getLocalDateFromDateTime(match_ID);

                            // get local date from datetime
                            Log.d(TAG, "onDataChange: does: "+getLocalDateFromDateTime(match_ID) + " equals  "+ selectedDate +  " ?");
                            if (localDate.equals(selectedDate)){

                                Log.d(TAG, "onDataChange: yes");
                                MatchDetails matchDetails = snapshot.getValue(MatchDetails.class);
                                //Log.d(TAG, "onDataChange: LOCAL DATE: "+localDate+ " CURRENT DATE: "+currentDate.getRegion());
/*                                if (!localDate.equals(currentDate.getRegion())){
                                    matchDetails.setState("1");
                                    Log.d(TAG, "onDataChange: this is the first match");
                                }else {
                                    matchDetails.setState("0");
                                    Log.d(TAG, "onDataChange: this is NOT the first match");}*/

                                //currentDate.setRegion(localDate);
                                //Log.d(TAG, "onDataChange: LOCAL DATE: "+localDate+ " CURRENT DATE: "+currentDate.getRegion());
                                matchDetails.setState("0");
                                matchDetails.setDatetime(getLocalDateTimeFromDateTime(matchDetails.getDatetime()));
                                Log.d(TAG, "onDataChange: "+matchDetails.getId() +" "+matchDetails.getDatetime() +" "+matchDetails.getTeam1());
                                allMatchDetailsForDate.add(matchDetails);
                                Log.d(TAG, "onDataChange: size list: "+allMatchDetailsForDate.size());
                                //counter end
                            }

                            if (global_counter.getNumber().toString().equals(sum_all_matches_all_regions.getNumber().toString())){
                                Log.d(TAG, "onDataChange: global_counter.getNumber()==sum_all_matches_all_regions.getNumber() ");
                                //hai passato tutti i match di tutte le regioni per il giorno scelto, utilizza i dati
                                Log.d(TAG, "onDataChange: allMatchDetailsForDate.size():  "+allMatchDetailsForDate.size());
/*                                for(int i=0; i<allMatchDetailsForDate.size(); i++){
                                    Log.d(TAG, "onDataChange: "+allMatchDetailsForDate.get(i).getId() +" "+allMatchDetailsForDate.get(i).getDatetime() +" "+allMatchDetailsForDate.get(i).getTeam1());

                                }*/
                                loadRecyclerViewCalendar(allMatchDetailsForDate);

                            }



                        }


                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                    }
                });

            }





        }

    }

    private void loadRecyclerViewCalendar(ArrayList<MatchDetails> allMatchDetailsForDate) {
        allMatchDetailsForDate.get(0).setState("1");

        adapterRecycler = new RecyclerView_Calendar_Adapter(this, allMatchDetailsForDate);
        adapterRecycler.notifyDataSetChanged();
        calendar_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        calendar_recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemAnimator animator = calendar_recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        calendar_recyclerView.setAdapter(adapterRecycler);

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

    public String getTodayDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        return date;
    }

    private String getLocalDateFromDateTime(String datetime) {
        //Log.d(TAG, "getLocalDateFromDateTimeeee: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);
        //Log.d(TAG, "getLocalDateFromDateTimeeeee: localDatetime: "+localDatetime);

        return localDatetime;
    }

    private String getLocalDateTimeFromDateTime(String datetime) {
        //Log.d(TAG, "getLocalDateTimeFromDateTime: datetime: " + datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);

        return localDatetime;
    }


}