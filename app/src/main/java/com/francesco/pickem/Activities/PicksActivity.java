package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Adapters.Day_selection_Adapter;
import com.francesco.pickem.Adapters.Region_selection_Adapter;
import com.francesco.pickem.Adapters.RecyclerView_Picks_Adapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.DisplayMatch;
import com.francesco.pickem.Models.FullDate;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.RegionDetails;
import com.francesco.pickem.Models.TeamDetails;
import com.francesco.pickem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PicksActivity extends AppCompatActivity  {

    ViewPager viewPager, viewPager_match_day;
    Region_selection_Adapter adapterRegions;
    Day_selection_Adapter adapterDays;

    RecyclerView_Picks_Adapter adapterRecycler;
    List<String> selectedRegions;
    ArrayList <RegionDetails> displayRegions;
    ConstraintLayout pick_background;
    ImageButton notification, picks, calendar, stats;
    RecyclerView recyclerView;
    ArrayList<MatchDetails> matchListSplit;
    ArrayList<DisplayMatch> displayMatchListSplit;
    private String TAG ="PicksActivity";
    Context context;
    ImageView pick_backgroundimage;
    RequestOptions options;
    ProgressBar pick_progressbar, pick_progressbar_matches;
    String selected_region_name;
    ArrayList<String> regionmatchDates;

    ArrayList<FullDate> allFullDates;
    FullDate day_selected_fullDay;
    String split;
    Calendar myCalendar;
    String year;
    String logo_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picks);
        recyclerView = findViewById(R.id.picksactivity_recyclerview);
        pick_backgroundimage = findViewById(R.id.pick_backgroundimage);
        pick_progressbar = findViewById(R.id.pick_progressbar);
        pick_progressbar_matches = findViewById(R.id.pick_progressbar_matches);
        selectedRegions = new ArrayList<String>();
        displayRegions = new ArrayList<RegionDetails>();
        selected_region_name ="";
        String day_selected = "";
        context = this;
        pick_progressbar.setVisibility(View.VISIBLE);
        pick_progressbar_matches.setVisibility(View.VISIBLE);

        split = "S1";

        myCalendar = Calendar.getInstance();
         year = String.valueOf(myCalendar.get(Calendar.YEAR));

        changeNavBarColor();
        setupBottomNavView();

        // 1) tira giù le Region scelte, seleziona la prima [region_selected]
        downloadSelectedRegions();
        // 2) della Region scelta, tira giù gli ID dello split corrente, seleziona il gioro corrente o la data prossima piu vicina [day_selected]

        // 3) carica i dati della data selezionata
        //loadRecyclerView(allMatchesForThisDate(allFullDates));

    }

    private void downloadSelectedRegions() {
        Log.d(TAG, "initializeLeagueSelection: ");

        viewPager = findViewById(R.id.viewPager_picksActivity);
        pick_background = findViewById(R.id.pick_background);

        Log.d(TAG, "initializeLeagueSelection: "+selectedRegions.size());

        Log.d(TAG, "initializeLeagueSelection: UID: "+FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_generealities))
                .child(getString(R.string.firebase_user_choosen_regions));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String regionDetails = snapshot.getValue(String.class);
                    selectedRegions.add(regionDetails);
                    Log.d(TAG, "onDataChange: adding region to arraylist:"+regionDetails);

                }


                Log.d(TAG, "initializeLeagueSelection: selectedRegions.size(2)"+selectedRegions.size());

                for (int i=0; i<selectedRegions.size(); i++){
                    // per ogni nome nella lista, prendi l'oggetto corrispondente in /Regions
                    Log.d(TAG, "onDataChange: SEARCHING FOR THIS REGION IN REGIONS: "+ selectedRegions.get(i));
                    DatabaseReference referenceRegions = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_regions))
                            .child(selectedRegions.get(i));

                    referenceRegions.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                            RegionDetails regionDetails = dataSnapshot.getValue(RegionDetails.class);
                            if (regionDetails!=null){
                                displayRegions.add(regionDetails);
                            }
                            adapterRegions.notifyDataSetChanged();
                            pick_progressbar.setVisibility(View.GONE);
                            selected_region_name = displayRegions.get(0).getName();

                            Log.d(TAG, "onDataChange: selected_region_name: "+selected_region_name);
                            loadSplitMatchesForThisRegion(selected_region_name);

                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                        }

                    });

                }


                adapterRegions = new Region_selection_Adapter(displayRegions, PicksActivity.this);

                Log.d(TAG, "initializeLeagueSelection: "+selectedRegions.size());

                viewPager = findViewById(R.id.viewPager_picksActivity);
                viewPager.setAdapter(adapterRegions);
                viewPager.setPadding(410, 0, 400, 0);

                viewPager.setCurrentItem(0);

                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        RequestOptions options = new RequestOptions()
                                .fitCenter()
                                .error(R.drawable.ic_load);

                        Glide.with(context).load(displayRegions.get(position).getImage()).apply(options).transition(DrawableTransitionOptions.withCrossFade(500)).into(pick_backgroundimage);
                        //selected_region_name = displayRegions.get(position).getName();
                        //getAllMatchDaysForThisRegion(selected_region_name);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        selected_region_name = displayRegions.get(position).getName();
                        Log.d(TAG, "onPageSelected:selected_region_name: "+selected_region_name);

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loadSplitMatchesForThisRegion(String selected_region){

        Log.d(TAG, "getAllMatchDays: selected_region: "+selected_region);
        String firebase_section = getString(R.string.match);

        allFullDates = new ArrayList<>();

        // load da firebase le regioni
        Log.d(TAG, "getAllMatchDays: path: "+ firebase_section +"/"+ selected_region +"/"+ selected_region + year +"/"+selected_region + year+split);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(firebase_section)
                .child(selected_region)
                .child(selected_region + year)
                .child(selected_region + year+split);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // prendi tutti i giorni disponibili per quell'anno per quella regione per quello split
                    String matchDayDetails = (snapshot.getKey().toString());
                    allFullDates.add(getFullDateFromUnivDate(matchDayDetails));


                }
                Log.d(TAG, "onDataChange: $$$$$$$$$$$$$$$$$$$ allFullDates.size(1)"+allFullDates.size());

                //ora  setta le date in alto passando matchdays ma scopri quale di queste date è la attuale o prossima alla attuale
                loadViewPagerMatchDays( filterFullDates(allFullDates));


            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<FullDate> filterFullDates(ArrayList<FullDate> fullMatchDaysList){

        Log.d(TAG, "filterFullDates: fullMatchDaysList.size(y): "+fullMatchDaysList.size());

        //qui di tutti gli id tiri fuori solo le giornate convertite in locale
        ArrayList <FullDate> uniqueFullMatchDays = new ArrayList<>();
        String uniqueDate = "";

        for (int i=0; i< fullMatchDaysList.size(); i++){

            //crea una lista con solo le date univoche
            if (!uniqueDate.equals(fullMatchDaysList.get(i).getDate())){
                uniqueFullMatchDays.add(fullMatchDaysList.get(i));
            }

            uniqueDate = fullMatchDaysList.get(i).getDate();
        }
        Log.d(TAG, "filterFullDates: +uniqueFullMatchDays.size(x) : "+uniqueFullMatchDays.size());
        return uniqueFullMatchDays;

    }

    private FullDate getFullDateFromUnivDate(String dateString) {

        FullDate fullDate = new FullDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat coolDateFormatter = new SimpleDateFormat("EEE, d MMM  ");
        dateFormatter.setTimeZone(TimeZone.getDefault());
        coolDateFormatter.setTimeZone(TimeZone.getDefault());
        String localDatetime = dateFormatter.format(value);
        String coolLocalDatetime = coolDateFormatter.format(value);

        String[] datetime = localDatetime.split(" ");

        String data ="";
        String ora = "";


        if (datetime.length ==2){
            data = datetime[0];
            ora = datetime[1];
        }

        fullDate.setId(dateString);
        fullDate.setLocalDateTime(localDatetime);
        fullDate.setDate(data);
        fullDate.setTime(ora);
        fullDate.setCoolDate(coolLocalDatetime);

/*        Log.d(TAG, "getFullDateFromUnivDate: Id: "+fullDate.getId());
        Log.d(TAG, "getFullDateFromUnivDate: local: : "+fullDate.getLocalDateTime());
        Log.d(TAG, "getFullDateFromUnivDate: date: "+fullDate.getDate());
        Log.d(TAG, "getFullDateFromUnivDate: time: "+fullDate.getTime());*/

        return fullDate;
    }

    private void loadViewPagerMatchDays(ArrayList<FullDate> matchDays) {

        Integer selectedPage = selectMatchDay(matchDays);

            viewPager_match_day = findViewById(R.id.viewPager_match_day);
                adapterDays = new Day_selection_Adapter((matchDays), PicksActivity.this);

              //  Log.d(TAG, "initializeLeagueSelection: "+selectedRegions.size());

                viewPager_match_day.setAdapter(adapterDays);
                viewPager_match_day.setPadding(300, 0, 300, 0);



                viewPager_match_day.setCurrentItem(selectedPage);
                day_selected_fullDay = matchDays.get(selectedPage);



                //loadRecyclerView(allMatchesForThisDate(allFullDates, day_selected_fullDay));



                viewPager_match_day.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        day_selected_fullDay = matchDays.get(position);
                        Log.d(TAG, "onPageScrolled: id day selected: "+ day_selected_fullDay);
                        // 1 ) eerore in all matches for this Date
                        // 2 ) all full dates not resetted correctly
                        loadRecyclerView(allMatchesForThisDate(allFullDates));

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

    }

    private ArrayList<String> allMatchesForThisDate (ArrayList<FullDate> fullDates ){

        FullDate fullDate_campione = day_selected_fullDay;

        Log.d(TAG, "1 allMatchesForThisDate: fullDates.size():"+fullDates.size());
        Log.d(TAG, "2 allMatchesForThisDate: fullDate_campione:"+fullDate_campione.getId());

        ArrayList <String> listOfIDs = new ArrayList<>();

        for (int i=0; i<fullDates.size(); i++){
            Log.d(TAG, "3 allMatchesForThisDate: fullDates.get(i).getDate(): "+fullDates.get(i).getId());
            Log.d(TAG, "4 allMatchesForThisDate: fullDate_campione.getDate(): "+fullDate_campione.getId());
            if (fullDates.get(i).getDate().equals(fullDate_campione.getDate())){
                listOfIDs.add(fullDates.get(i).getId());
            }

        }
        Log.d(TAG, "allMatchesForThisDate: listOfIDs.size():"+listOfIDs.size());
        return listOfIDs;


    }

    private void loadRecyclerView(ArrayList <String> loadThisMatchesID){
        Log.d(TAG, "initRecyclerView: loadThisMatchesID.size(): "+loadThisMatchesID.size());

        //matchDetails
        matchListSplit = new ArrayList<>();
        //DisplayMatch
        displayMatchListSplit = new ArrayList<>();

        // 1) prendi gli oggetti di tipo MatchDetails con gli id loadThisMatchesID
        //ciclo for di un sigleEventListener che prende un oggetto da ficcare in matchListSplit
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.match))
                .child(selected_region_name)
                .child(selected_region_name + year)
                .child(selected_region_name + year + split);

        for (int i =0; i < loadThisMatchesID.size(); i++){

            Log.d(TAG, "loadRecyclerView: "+loadThisMatchesID.get(i));

            reference.child(loadThisMatchesID.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                    MatchDetails matchDetails = dataSnapshot.getValue(MatchDetails.class);
                    if (matchDetails!=null){
                        Log.d(TAG, "onDataChange: £££££££££££££££££££££"+matchDetails.getId());
                        matchListSplit.add(matchDetails);

                    }

                }


                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                }

            });

        }

        // 2) trasformali in ArrayList di DisplayMatch


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                loadItemsRecyclerVIew(matchListSplit);
            }
        }, 2000);

    }

    private void loadItemsRecyclerVIew (ArrayList<MatchDetails> matchListSplit){
        Log.d(TAG, "loadRecyclerView: %%%%%%%%%%%%%%%%%%%%%%"+matchListSplit.size());
        displayMatchListSplit= new ArrayList<>();

        for (int i =0; i < matchListSplit.size(); i++){

            DisplayMatch displayMatch = new DisplayMatch();
            FullDate fullDate = getFullDateFromUnivDate(matchListSplit.get(i).getDatetime());
            displayMatch.setDate(fullDate.getDate());
            displayMatch.setTime(fullDate.getTime());
            displayMatch.setTeam1(matchListSplit.get(i).getTeam1());
            displayMatch.setTeam2(matchListSplit.get(i).getTeam2());
            displayMatch.setId(matchListSplit.get(i).getId());
            displayMatch.setWinner(matchListSplit.get(i).getWinner());
            displayMatch.setPrediction("null");
            displayMatch.setRegion(selected_region_name);

            displayMatchListSplit.add(displayMatch);


        }

        //load the views

        Log.d(TAG, "loadRecyclerView: $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+displayMatchListSplit.size());
        adapterRecycler = new RecyclerView_Picks_Adapter(this, displayMatchListSplit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterRecycler);
        adapterRecycler.notifyDataSetChanged();
        pick_progressbar_matches.setVisibility(View.GONE);

    }

/*    private String getImageForThisTeam(String teamName){

        logo_URL="";

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_teams))
                .child(selected_region_name)
                .child(teamName)
                .child(getString(R.string.team_image));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                String teamImage = dataSnapshot.getValue(String.class);
                if (teamImage!= null){
                    logo_URL = teamImage;
                }
            }




            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }


        });

        return logo_URL;

    }*/



    public Integer selectMatchDay(ArrayList<FullDate> matchDays) {
        //in base agli ID dell'array list, trova la data sucessiva o coincidente a quella attuale
        Integer itemPosition=0;

        for (int i=0; i< matchDays.size();i++){
           // Log.d(TAG, "loadMatchDays: "+matchDays.get(i));

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date strDate = null;
            try {
                strDate = sdf.parse(matchDays.get(i).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            // Log.d(TAG, "selectMatchDay: System.currentTimeMillis(): "+System.currentTimeMillis());;

            long matchTimeMillis = strDate.getTime();

           // Log.d(TAG, "selectMatchDay: matchTimeMillis:"+matchTimeMillis);
            if (System.currentTimeMillis() <= matchTimeMillis) {
                itemPosition = i-1;
                //Log.d(TAG, "selectMatchDay: itemPosition"+itemPosition);
                if (itemPosition <0){return 0;}else{return itemPosition;}


            }

        }

       return 0;
    }

    private void setupBottomNavView() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.button_picks);
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
                        Intent intentCalendar= new Intent(context, Calendar.class);
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

    public void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }

}