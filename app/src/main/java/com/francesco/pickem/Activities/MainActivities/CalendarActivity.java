package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Activities.Statistics.StatsPicksActivity;
import com.francesco.pickem.Adapters.RecyclerView_Calendar_Adapter;
import com.francesco.pickem.Adapters.Region_selection_Adapter_calendar;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.CurrentNumber;
import com.francesco.pickem.Models.CurrentTeam;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.YearMonthDay;
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
import java.util.Collections;
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
    ProgressBar progressbar_calendar;
    TextView description_calendar, add_eloday_date;
    ImageView refresh_icon;
    Integer currentWeek, currentDay;


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
        progressbar_calendar = findViewById(R.id.progressbar_calendar);
        refresh_icon = findViewById(R.id.refresh_icon);
        description_calendar = findViewById(R.id.description_calendar);
        add_eloday_date = findViewById(R.id.add_eloday_date);


        currentWeek =0;
        currentDay =0;



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
                    loadWeekMatches(selectedCalendarRegions, currentWeek);
                }else {
                    switch_is_day = true;
                    Log.d(TAG, "onCheckedChanged: switch_day:"+ switch_is_day);
                    loadMatchesForThisLeagues(selectedCalendarRegions,0);
                }
            }
        });
        
        
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: previous");
                if (switch_is_day){
                    currentDay=-1;
                    loadMatchesForThisLeagues(selectedCalendarRegions,currentDay);
                }else {
                    currentWeek-=1;
                    loadWeekMatches(selectedCalendarRegions, currentWeek);
                }


            }
        });
        
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: next");

                if (switch_is_day){
                    currentDay=1;
                    loadMatchesForThisLeagues(selectedCalendarRegions,currentDay);
                }else {
                    currentWeek+=1;
                    loadWeekMatches(selectedCalendarRegions, currentWeek);
                }
            }
        });

        pullToRefreshCalendar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (switch_is_day){
                    loadMatchesForThisLeagues(selectedCalendarRegions, currentDay);
                }else {
                    loadWeekMatches(selectedCalendarRegions, currentWeek);
                }

                pullToRefreshCalendar.setRefreshing(false);
            }
        });

        choose_date_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                // all'ok del datepicker apri il dialog dell'ora e setta il textview
                                monthOfYear = monthOfYear + 1;
                                //Log.d(TAG, "onDateSet: "+String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear) + "/" + year);
                                selectedDate = year+"-"+String.format("%02d", monthOfYear)+"-"+String.format("%02d", dayOfMonth);

                                loadMatchesForThisLeagues(selectedCalendarRegions,0);
                                // timepicker
                                final Calendar c = Calendar.getInstance();

                            }
                        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }

        });

    }

    private void loadWeekMatches(ArrayList<String> selectedCalendarRegions, Integer weekBeforeAfter) {
        ArrayList <String> days = new ArrayList<>();
        //scarica le date per settimana
        //prendi il lunedì passato piu vicino a oggi
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Log.d(TAG, "loadMatchesForThisLeagues: "+c.getTime());
        //converto la data in yyyy-MM-dd di tutti i giorni di quella settimana

        c.add(Calendar.DAY_OF_MONTH, weekBeforeAfter*7);
        for(int i=0; i<7; i++){
            String d1 = sdf.format(c.getTime());
            days.add(d1);
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        /*String d2 = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH,1);
        String d3 = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH,1);
        String d4 = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH,1);
        String d5 = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH,1);
        String d6 = sdf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH,1);
        String d7 = sdf.format(c.getTime());*/

        loadMatchesForThisLeaguesThisWeek(selectedCalendarRegions, days);

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
                if (int_user_regions_selected==0){
                    viewPagerRegions.setItemViewCacheSize(25);
                    selectedCalendarRegions = new ArrayList<>();
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    viewPagerRegions.setLayoutManager(layoutManager);
                    adapterRegions = new Region_selection_Adapter_calendar(allRegions, imageRegionPath, context, selectedCalendarRegions);
                    viewPagerRegions.setAdapter(adapterRegions);
                    adapterRegions.notifyDataSetChanged();

                    calendar_progressbar.setVisibility(View.GONE);
                }else {
                    int counter = 0;
                    ArrayList <String> userRegions = new ArrayList<>();
                    selectedCalendarRegions = new ArrayList<>();
                    //prendi tutte le regioni di interesse dello user
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        counter++;
                        String userRegion = snapshot.getValue(String.class);
                        Log.d(TAG, "onDataChange: calendar region: "+userRegion);
                        selectedCalendarRegions.add(userRegion);
                        if (int_user_regions_selected==counter){
                            if (selectedCalendarRegions.size()>0){
                                // Actions to do after 10 seconds
                                viewPagerRegions.setItemViewCacheSize(25);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                                viewPagerRegions.setLayoutManager(layoutManager);
                                adapterRegions = new Region_selection_Adapter_calendar(allRegions, imageRegionPath, context, selectedCalendarRegions);
                                viewPagerRegions.setAdapter(adapterRegions);
                                adapterRegions.notifyDataSetChanged();

                                calendar_progressbar.setVisibility(View.GONE);

                                loadMatchesForThisLeagues(selectedCalendarRegions,0);
                            }
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });

    }

    private void loadMatchesForThisLeagues(ArrayList<String> selectedCalendarRegions, Integer currentDay) {

        if (selectedCalendarRegions.size()==0){

            calendar_recyclerView.setVisibility(View.INVISIBLE);

            description_calendar.setVisibility(View.VISIBLE);
            description_calendar.setText("Select Regions to show data");

        }else {
            calendar_recyclerView.setVisibility(View.INVISIBLE);
            progressbar_calendar.setVisibility(View.VISIBLE);
            description_calendar.setVisibility(View.VISIBLE);
            description_calendar.setText("Loading data");

            progressbar_calendar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.g2));

            // prendi il valore dello switch

                //se sono in modalità di visualizzazione only day , prendi le region, il current day (se "" -> today)
                if (selectedDate.equals("")){
                    selectedDate = getTodayDate();
                }

                // add / sottrai da selectedDate il currentDay

                Calendar c = Calendar.getInstance();
                YearMonthDay yearMonthDay = new YearMonthDay();
                yearMonthDay = getYearMonthDayFromDateString(selectedDate);
                c.set(Calendar.YEAR, yearMonthDay.getYear());
                c.set(Calendar.MONTH, yearMonthDay.getMonth());
                c.set(Calendar.DAY_OF_MONTH, yearMonthDay.getDay());
                c.add(Calendar.MONTH, -1);

                c.add(Calendar.DATE, currentDay);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = sdf.format(c.getTime());




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
                            progressbar_calendar.setMax(sum_all_matches_all_regions.getNumber());
                            //Log.d(TAG, "onDataChange: sum_all_matches_all_regions: "+sum_all_matches_all_regions.getNumber());

                            CurrentTeam currentDate = new CurrentTeam();
                            currentDate.setRegion("");
                            //prendi tutte le regioni
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                global_counter.setNumber(global_counter.getNumber()+1);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    progressbar_calendar.setProgress(global_counter.getNumber(),true);
                                }
                                //Log.d(TAG, "onDataChange: global_counter:"+global_counter.getNumber() + "/" +sum_all_matches_all_regions.getNumber());
                                String match_ID = snapshot.getKey();

                                // solo se la data corrisponde allora scarichi i dettagli
                                String localDate = getLocalDateFromDateTime(match_ID);

                                // get local date from datetime
                                //Log.d(TAG, "onDataChange: does: "+getLocalDateFromDateTime(match_ID) + " equals  "+ selectedDate +  " ?");
                                if (localDate.equals(selectedDate)){

                                    //Log.d(TAG, "onDataChange: yes");
                                    MatchDetails matchDetails = snapshot.getValue(MatchDetails.class);
                                    //Log.d(TAG, "onDataChange: LOCAL DATE: "+localDate+ " CURRENT DATE: "+currentDate.getRegion());
                                if (!localDate.equals(currentDate.getRegion())){
                                    matchDetails.setState("1");
                                    Log.d(TAG, "onDataChange: this is the first match");
                                }else {
                                    matchDetails.setState("0");
                                    Log.d(TAG, "onDataChange: this is NOT the first match");}


                                    //currentDate.setRegion(localDate);
                                    //Log.d(TAG, "onDataChange: LOCAL DATE: "+localDate+ " CURRENT DATE: "+currentDate.getRegion());
                                    matchDetails.setState("0");
                                    matchDetails.setDatetime(getLocalDateTimeFromDateTime(matchDetails.getDatetime()));
                                    //Log.d(TAG, "onDataChange: "+matchDetails.getId() +" "+matchDetails.getDatetime() +" "+matchDetails.getTeam1());
                                    allMatchDetailsForDate.add(matchDetails);
                                    //Log.d(TAG, "onDataChange: size list: "+allMatchDetailsForDate.size());
                                    //counter end
                                }

                                if (global_counter.getNumber().toString().equals(sum_all_matches_all_regions.getNumber().toString())){

/*                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {*/
                                            // Actions to do after 10 seconds

                                            //Log.d(TAG, "onDataChange: global_counter.getNumber()==sum_all_matches_all_regions.getNumber() ");
                                            //hai passato tutti i match di tutte le regioni per il giorno scelto, utilizza i dati
                                            //Log.d(TAG, "onDataChange: allMatchDetailsForDate.size():  "+allMatchDetailsForDate.size());
                                            if (allMatchDetailsForDate.size()>0){
                                                loadRecyclerViewCalendar(allMatchDetailsForDate);
                                            }else {
                                                description_calendar.setText("No matches fond for: "+selectedDate);
                                                progressbar_calendar.setVisibility(View.GONE);
                                            }
/*                                        }
                                    }, 1000);*/
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

    private void loadMatchesForThisLeaguesThisWeek(ArrayList<String> selectedCalendarRegions, ArrayList<String>dates) {



        for(int i=0; i<dates.size(); i++){
            Log.d(TAG, "loadMatchesForThisLeaguesThisWeek: date: "+dates.get(i));
        }



        if (selectedCalendarRegions.size()==0){

            calendar_recyclerView.setVisibility(View.INVISIBLE);

            description_calendar.setVisibility(View.VISIBLE);
            description_calendar.setText("Select Regions to show data");

        }else {
            calendar_recyclerView.setVisibility(View.INVISIBLE);
            progressbar_calendar.setVisibility(View.VISIBLE);
            description_calendar.setVisibility(View.VISIBLE);
            description_calendar.setText("Loading data");

            progressbar_calendar.setProgressTintList(ContextCompat.getColorStateList(this, R.color.g2));

            // prendi il valore dello switch

                //se sono in modalità di visualizzazione only day , prendi le region, il current day (se "" -> today)


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
                            progressbar_calendar.setMax(sum_all_matches_all_regions.getNumber());
                            Log.d(TAG, "onDataChange: sum_all_matches_all_regions: "+sum_all_matches_all_regions.getNumber());


                            CurrentTeam currentDate = new CurrentTeam();
                            currentDate.setRegion("");
                            //prendi tutte le regioni
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                global_counter.setNumber(global_counter.getNumber()+1);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    progressbar_calendar.setProgress(global_counter.getNumber(),true);
                                }
                                Log.d(TAG, "onDataChange: global_counter:"+global_counter.getNumber() + "/" +sum_all_matches_all_regions.getNumber());
                                String match_ID = snapshot.getKey();
                                // solo se la data corrisponde allora scarichi i dettagli
                                String localDate = getLocalDateFromDateTime(match_ID);

                                // get local date from datetime
                                Log.d(TAG, "onDataChange: does: "+getLocalDateFromDateTime(match_ID) + " equals  "+ selectedDate +  " ?");
                                if (localDate.equals(dates.get(0))
                                        ||localDate.equals(dates.get(1))
                                        ||localDate.equals(dates.get(2))
                                        ||localDate.equals(dates.get(3))
                                        ||localDate.equals(dates.get(4))
                                        ||localDate.equals(dates.get(5))
                                        ||localDate.equals(dates.get(6))){

                                    Log.d(TAG, "onDataChange: yes");
                                    MatchDetails matchDetails = snapshot.getValue(MatchDetails.class);
                                    //Log.d(TAG, "onDataChange: LOCAL DATE: "+localDate+ " CURRENT DATE: "+currentDate.getRegion());
                                    /*if (!localDate.equals(currentDate.getRegion())){
                                        matchDetails.setState("1");
                                        Log.d(TAG, "onDataChange: this is the first match");
                                    }else {
                                        matchDetails.setState("0");
                                        Log.d(TAG, "onDataChange: this is NOT the first match");
                                    }*/

                                        //currentDate.setRegion(localDate);
                                        //Log.d(TAG, "onDataChange: LOCAL DATE: "+localDate+ " CURRENT DATE: "+currentDate.getRegion());
                                        //matchDetails.setState("0");
                                        matchDetails.setDatetime(getLocalDateTimeFromDateTime(matchDetails.getDatetime()));
                                        Log.d(TAG, "onDataChange: "+matchDetails.getId() +" "+matchDetails.getDatetime() +" "+matchDetails.getTeam1());
                                        allMatchDetailsForDate.add(matchDetails);
                                        Log.d(TAG, "onDataChange: size list: "+allMatchDetailsForDate.size());
                                        //counter end
                                }

                                if (global_counter.getNumber().toString().equals(sum_all_matches_all_regions.getNumber().toString())){

/*                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {*/
                                            // Actions to do after 10 seconds

                                            Log.d(TAG, "onDataChange: global_counter.getNumber()==sum_all_matches_all_regions.getNumber() ");
                                            //hai passato tutti i match di tutte le regioni per il giorno scelto, utilizza i dati
                                            Log.d(TAG, "onDataChange: allMatchDetailsForDate.size():  "+allMatchDetailsForDate.size());
                                            if (allMatchDetailsForDate.size()>0){
                                                loadRecyclerViewCalendar(allMatchDetailsForDate);
                                            }else {
                                                description_calendar.setText("No matches fond");
                                                progressbar_calendar.setVisibility(View.INVISIBLE);
                                            }




/*                                        }
                                    }, 1000);*/


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


        Collections.sort(allMatchDetailsForDate, new MatchDetails.ByDatetime());
        //allMatchDetailsForDate.get(0).setState("1");

        // al primo, state 1, salva il giorno del match
        // se il giorno è uguale al giorno salvato, set a 0, aggiorna il giorno del match
        String dateCycle = "";
        for(int i=0; i<allMatchDetailsForDate.size(); i++){
            String dateInside = getLocalDateFromDateTime(allMatchDetailsForDate.get(i).getDatetime());
            if (dateInside.equals(dateCycle)){
                allMatchDetailsForDate.get(i).setState("0");
            }else {
                allMatchDetailsForDate.get(i).setState("1");
            }
            dateCycle = dateInside;


        }
        calendar_recyclerView.setItemViewCacheSize(40);
        calendar_recyclerView.setVisibility(View.VISIBLE);
        progressbar_calendar.setVisibility(View.INVISIBLE);
        description_calendar.setVisibility(View.INVISIBLE);

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
                        Intent intentStats= new Intent(context, AllStatsActivity.class);
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

    private String getDateFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTime: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MM dd'T'HH:mm:ss");
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
        Log.d(TAG, "getLocalDateFromDateTime: localDatetime: "+localDatetime);

        return localDatetime;
    }

    YearMonthDay getYearMonthDayFromDateString(String dateString){
        //Log.d(TAG, "getYearMonthDayFromDateString: dateString: "+dateString);

        YearMonthDay yearMonthDay = new YearMonthDay();

        String[] datetime = dateString.split("-");
        Integer day = 0;
        Integer month =0;
        Integer year = 0;


        if (datetime.length ==3){
            year = Integer.parseInt(datetime[0]);
            yearMonthDay.setYear(year);
            month = Integer.parseInt(datetime[1]);
            yearMonthDay.setMonth(month);
            day = Integer.parseInt(datetime[2]);
            yearMonthDay.setDay(day);
            //Log.d(TAG, "getYearMonthDayFromDateString: year: "+year +" month: "+month + " day: "+day);
        }

        return yearMonthDay;

    }


}