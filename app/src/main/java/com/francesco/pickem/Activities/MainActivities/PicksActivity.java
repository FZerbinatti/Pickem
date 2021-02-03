package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Activities.AccountActivities.LoginActivity;
import com.francesco.pickem.Adapters.Day_selection_Adapter;
import com.francesco.pickem.Adapters.Region_selection_Adapter;
import com.francesco.pickem.Adapters.RecyclerView_Picks_Adapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Interfaces.OnGetDataListener;
import com.francesco.pickem.Models.DisplayMatch;
import com.francesco.pickem.Models.FullDate;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.RegionDetails;
import com.francesco.pickem.NotificationsService.SetNotificationFor24Hours;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.PreferencesData;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class PicksActivity extends AppCompatActivity  {

    ViewPager viewPager, viewPager_match_day;
    Region_selection_Adapter adapterRegions;
    Day_selection_Adapter adapterDays;
    RecyclerView_Picks_Adapter adapterRecycler;
    ConstraintLayout pick_background;
    ImageButton notification, picks, calendar, stats;
    private String TAG ="PicksActivity";
    Context context;
    ImageView pick_backgroundimage;
    RequestOptions options;
    ProgressBar pick_progressbar, pick_progressbar_matches;
    String selected_region_name;
    ArrayList<String> regionmatchDates;
    ArrayList <RegionDetails> displayRegions;
    ArrayList<FullDate> allFullDates;
    ArrayList<MatchDetails> matchListSplit;
    ArrayList<DisplayMatch> displayMatchListSplit;
    ArrayList<String> selectedRegions;
    ArrayList <RegionDetails> allRegionsDetails;
    RecyclerView recyclerView;
    FullDate day_selected_fullDay;
    Calendar myCalendar;
    String year;
    String logo_URL;
    TextView no_match_found;
    ArrayList <String> list_of_splits;
    SetNotificationFor24Hours setNotificationFor24Hours;
    ImageView test;
    String imageRegionPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picks);
        recyclerView = findViewById(R.id.picksactivity_recyclerview);
        pick_backgroundimage = findViewById(R.id.pick_backgroundimage);
        pick_progressbar = findViewById(R.id.pick_progressbar);
        pick_progressbar_matches = findViewById(R.id.pick_progressbar_matches);
        no_match_found= findViewById(R.id.no_match_found);
        viewPager_match_day = findViewById(R.id.viewPager_match_day);
        test = findViewById(R.id.topper_pick_backgroundimage);


        if(isUserAlreadyLogged()){
            Log.d(TAG, "onCreate: ");
            downloadSelectedRegions();
            startBackgorundTasks();
        }
        selected_region_name ="";
        context = this;
        pick_progressbar.setVisibility(View.VISIBLE);
        pick_progressbar_matches.setVisibility(View.VISIBLE);
        imageRegionPath = context.getFilesDir().getAbsolutePath() + "/images/regions/";

        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));
        changeNavBarColor();
        setupBottomNavView();

/*
        String imagepath = context.getFilesDir().getAbsolutePath() + "/images/regions/" +"BelgianLeague" +".png";
        //test.setImageBitmap(BitmapFactory.decodeFile(imagepath));
        Glide.with(context)
                .load(new File(imagepath)) // Uri of the picture
                .into(test);

*/



    }

    private void startBackgorundTasks() {

        ComponentName componentName = new ComponentName(this, SetNotificationFor24Hours.class);
        JobInfo info1 = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic( 24* 60 * 60 * 1000)
                .build();
        JobScheduler scheduler1 = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode1 =  scheduler1.schedule(info1);
        if (resultCode1 == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "onCreate: SUCCESS JOB SCHEDULER");
        }else {
            Log.d(TAG, "onCreate: DIO PORCO");
        }

        Log.d(TAG, "startBackgorundTasks: ");
        JobInfo info2 = new JobInfo.Builder(2, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(7* 24* 60* 60* 1000)
                .build();
        JobScheduler scheduler2 = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode2 = scheduler2.schedule(info2);
        if (resultCode2 == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "onCreate: SUCCESS JOB SCHEDULER");
        }else {
            Log.d(TAG, "onCreate: DIO PORCO");
        }



    }
    
    private boolean isUserAlreadyLogged() {

         //PreferencesData.setUserLoggedInStatus(getApplicationContext(),false);
        Log.d(TAG, "isUserAlreadyLogged: "+PreferencesData.getUserLoggedInStatus(this));
        if ( !PreferencesData.getUserLoggedInStatus(this) ||   FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())  ==null ){
            Intent intent = new Intent(PicksActivity.this, LoginActivity.class);
            pick_progressbar.setVisibility(View.GONE);
            startActivity(intent);
            return false;
        }else {return true;}
    }

    private void downloadSelectedRegions() {

        //Log.d(TAG, "initializeLeagueSelection: ");

        selectedRegions = new ArrayList<String>();

        viewPager = findViewById(R.id.viewPager_picksActivity);
        pick_background = findViewById(R.id.pick_background);

        //Log.d(TAG, "initializeLeagueSelection: "+selectedRegions.size());

        //Log.d(TAG, "initializeLeagueSelection: UID: "+FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_generealities))
                .child(getString(R.string.firebase_user_choosen_regions));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                int int_user_regions_selected = (int) dataSnapshot.getChildrenCount();
                //Log.d(TAG, "onDataChange: int_user_regions_selected: "+int_user_regions_selected);
                //prendi tutte le regioni di interesse dello user
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String regionDetails = snapshot.getValue(String.class);
                    selectedRegions.add(regionDetails);
                    //Log.d(TAG, "onDataChange: adding region to arraylist:"+regionDetails);

                }
                //inizializza il top viewpager con le opzioni trovate
                //if (selectedRegions.size()== (int_user_regions_selected-1)){
                    //loadViewPagerGiornate(selectedRegions);

                getAllRegionDetails(selectedRegions);



            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getAllRegionDetails(ArrayList<String> userSelectedRegions){

        allRegionsDetails = new ArrayList<>();

        // load da firebase i servers
        DatabaseReference referenceRegions = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_regions));
        referenceRegions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RegionDetails regionDetails = snapshot.getValue(RegionDetails.class);
                    if (regionDetails!=null){
                        //Log.d(TAG, "onDataChange: regionDetails.getName()"+regionDetails.getName());
                        if (userSelectedRegions.contains(regionDetails.getName())){
                            allRegionsDetails.add(regionDetails);
                        }

                    }
                }
                loadViewPagerRegions(allRegionsDetails);
                pick_progressbar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });


    }

    public void loadViewPagerRegions(ArrayList<RegionDetails> userSelectedRegions){

        adapterRegions = new Region_selection_Adapter(userSelectedRegions, PicksActivity.this);

        viewPager = findViewById(R.id.viewPager_picksActivity);
        viewPager.setAdapter(adapterRegions);
        viewPager.setPadding(410, 0, 400, 0);

        viewPager.setCurrentItem(0);

        selected_region_name=userSelectedRegions.get(0).getName();
        loadSplitMatchesForThisRegion(selected_region_name);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                String local_image =userSelectedRegions.get(position).getName().replace(" ", "")+".png";
                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.ic_load);

                Glide.with(context)
                        .load(new File(imageRegionPath+local_image)) // Uri of the picture
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(pick_backgroundimage);



            }

            @Override
            public void onPageSelected(int position) {

                selected_region_name = userSelectedRegions.get(position).getName();

                loadSplitMatchesForThisRegion(selected_region_name);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state ==1 ){
                    selected_region_name="";
                }

            }
        });

    }

    public void loadSplitMatchesForThisRegion(String selected_region){
        Log.d(TAG, "loadSplitMatchesForThisRegion: ");

        //Log.d(TAG, "ADESSO STO CERCANDO DELLE GIORNATE PER LA REGIONE SELEZIONATA: "+selected_region);
        String firebase_section = getString(R.string.firebase_Matches);



        // load da firebase le regioni
        //Log.d(TAG, "loadSplitMatchesForThisRegion: path: "+ firebase_section +"/"+ selected_region +"/"+ selected_region + year +"/"+selected_region + year+split);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(firebase_section)
                .child(selected_region)
                .child(selected_region + year);



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                allFullDates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // prendi tutti i giorni disponibili per quell'anno per quella regione per quello split
                    String matchDayDetails = (snapshot.getKey().toString());
                    if(!allFullDates.contains(getFullDateFromUnivDate(matchDayDetails))){
                        allFullDates.add(getFullDateFromUnivDate(matchDayDetails));
                    }
                    Log.d(TAG, "onDataChange: "+allFullDates.size());




                }
                //Log.d(TAG, "onDataChange: $$$$$$$$$$$$$$$$$$$ allFullDates.size(1)"+allFullDates.size());
                if (allFullDates.isEmpty()){
                    no_match_found.setVisibility(View.VISIBLE);
                    pick_progressbar_matches.setVisibility(View.GONE);
                    pick_progressbar.setVisibility(View.GONE);
                    viewPager_match_day.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }else {
                    no_match_found.setVisibility(View.INVISIBLE);
                    viewPager_match_day.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    //ora  setta le date in alto passando matchdays ma scopri quale di queste date è la attuale o prossima alla attuale
/*                    Log.d(TAG, "onDataChange: ECCO LE DATE CHE HO TROVATO PER QUESTA REGION: "+selected_region);
                    for (int i=0; i<allFullDates.size();i++){
                        Log.d(TAG, "onDataChange: ID: "+allFullDates.get(i).getId()+" date: "+ allFullDates.get(i).getLocalDateTime());
                    }*/
                    createUserPicksForThisRegionIfNotExist(allFullDates, selected_region);
                    loadViewPagerMatchDays( filterFullDates(allFullDates), allFullDates, selected_region);
                }



            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createUserPicksForThisRegionIfNotExist(ArrayList<FullDate> allFullDates, String selected_region) {

        for (int i=0; i<allFullDates.size();i++){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getResources().getString(R.string.firebase_users_picks))
                .child(selected_region)
                .child(selected_region+year)
                .child(allFullDates.get(i).getId());



            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    String user_pick = snapshot.getValue(String.class);
                    if (user_pick == null){
                        reference.setValue("unpicked");
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });



        }



    }

    private ArrayList<FullDate> filterFullDates(ArrayList<FullDate> fullMatchDaysList){

        //Log.d(TAG, "filterFullDates: fullMatchDaysList.size(y): "+fullMatchDaysList.size());

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
        //Log.d(TAG, "filterFullDates: +uniqueFullMatchDays.size(x) : "+uniqueFullMatchDays.size());
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

    private void loadViewPagerMatchDays(ArrayList<FullDate> matchDays, ArrayList <FullDate> allFullDates, String selected_region_name) {
        //Log.d(TAG, "loadViewPagerMatchDays: $$$$$$$$"+matchDays.size());

            Integer selectedPage = selectMatchDay(matchDays);

            viewPager_match_day = findViewById(R.id.viewPager_match_day);
                adapterDays = new Day_selection_Adapter((matchDays), PicksActivity.this);

              //  Log.d(TAG, "initializeLeagueSelection: "+selectedRegions.size());

                viewPager_match_day.setAdapter(adapterDays);
                viewPager_match_day.setPadding(300, 0, 300, 0);

                viewPager_match_day.setCurrentItem(selectedPage);
                day_selected_fullDay = matchDays.get(selectedPage);
                //Log.d(TAG, "loadViewPagerMatchDays: "+day_selected_fullDay.getId());
                // ATTENTION DANGER MODIFY
                //loadRecyclerView(allMatchesForThisDate(allFullDates, day_selected_fullDay ),selected_region_name);

                viewPager_match_day.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        day_selected_fullDay = matchDays.get(position);
                        loadRecyclerView(allMatchesForThisDate(allFullDates,day_selected_fullDay),selected_region_name);

                    }

                    @Override
                    public void onPageSelected(int position) {


                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

    }

    private ArrayList<String> allMatchesForThisDate (ArrayList<FullDate> fullDates, FullDate day_selected_fullDay ){

/*        Log.d(TAG, "allMatchesForThisDate: ADESSO TI BECCO ");
        for (int i=0; i<fullDates.size(); i++){
            Log.d(TAG, "allMatchesForThisDate: ID: "+ fullDates.get(i).getId() + " date: "+ fullDates.get(i).getLocalDateTime());
        }*/
        FullDate fullDate_campione = day_selected_fullDay;

/*        Log.d(TAG, "1 allMatchesForThisDate: fullDates.size():"+fullDates.size());
        Log.d(TAG, "2 allMatchesForThisDate: fullDate_campione:"+fullDate_campione.getId());*/

        ArrayList <String> listOfIDs = new ArrayList<>();

        for (int i=0; i<fullDates.size(); i++){
/*            Log.d(TAG, "3 allMatchesForThisDate: fullDates.get(i).getDate(): "+fullDates.get(i).getId());
            Log.d(TAG, "4 allMatchesForThisDate: fullDate_campione.getDate(): "+fullDate_campione.getId());*/
            if (fullDates.get(i).getDate().equals(fullDate_campione.getDate())){
                listOfIDs.add(fullDates.get(i).getId());

                //Log.d(TAG, "allMatchesForThisDate: ID: "+ fullDates.get(i).getId() +" - date: "+ fullDates.get(i).getDate() +" time: "+ fullDates.get(i).getTime());

            }

        }
        //Log.d(TAG, "allMatchesForThisDate: listOfIDs.size():"+listOfIDs.size());
        return listOfIDs;


    }

    private void loadRecyclerView(ArrayList <String> loadThisMatchesID, String selected_region_name){
        Log.d(TAG, "initRecyclerView: loadThisMatchesID.size(): "+loadThisMatchesID.size());

        //matchDetails
        matchListSplit = new ArrayList<>();
        //DisplayMatch
        displayMatchListSplit = new ArrayList<>();



        for (int i =0; i < loadThisMatchesID.size(); i++){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Matches))
                    .child(selected_region_name)
                    .child(selected_region_name + year)
                    .child(loadThisMatchesID.get(i));

            //Log.d(TAG, "loadRecyclerView: "+selected_region_name+"/"+selected_region_name + year+"/"+selected_region_name + year + split+"/"+loadThisMatchesID.get(i));

            readData(reference, new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    //Log.d(TAG, "onSuccess: ");
                    MatchDetails matchDetails = dataSnapshot.getValue(MatchDetails.class);
                    //Log.d(TAG, "onSuccess: "+matchDetails.getDatetime());
                    if (matchDetails!=null){

                        //Log.d(TAG, "onDataChange: £££££££££££££££££££££"+matchDetails.getId());
                        matchListSplit.add(matchDetails);
                        //Log.d(TAG, "onSuccess: matchListSplit.size(): "+matchListSplit.size());
                        if (matchListSplit.size()==loadThisMatchesID.size()){
                            Log.d(TAG, "loadRecyclerView: "+matchListSplit.size());
                            fromMatchDaysToDisplayMatch(matchListSplit);
                        }

                    }

                }

                @Override
                public void onStart() {
                    //Log.d(TAG, "onStart: ");
                }

                @Override
                public void onFailure() {
                    //Log.d(TAG, "onFailure: ");
                }
            });


        }




    }

    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                listener.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                listener.onFailure();
            }
            });

    }

    private void fromMatchDaysToDisplayMatch(ArrayList<MatchDetails> matchListForThisDay){
        //Log.d(TAG, "loadRecyclerView: %%%%%%%%%%%%%%%%%%%%%%"+matchListForThisDay.size());

        displayMatchListSplit= new ArrayList<>();

        for (int i =0; i < matchListForThisDay.size(); i++){

            //Log.d(TAG, "fromMatchDaysToDisplayMatch: ID: "+ matchListForThisDay.get(i).getId() +" - datetime: "+ matchListForThisDay.get(i).getDatetime() +" match: "+ matchListForThisDay.get(i).getTeam1()  +" vs "+ matchListForThisDay.get(i).getTeam2() );
            DisplayMatch displayMatch = new DisplayMatch();
            FullDate fullDate = getFullDateFromUnivDate(matchListForThisDay.get(i).getDatetime());
            displayMatch.setDatetime(matchListForThisDay.get(i).getDatetime());
            displayMatch.setDate(fullDate.getDate());
            displayMatch.setTime(fullDate.getTime());
            displayMatch.setTeam1(matchListForThisDay.get(i).getTeam1());
            displayMatch.setTeam2(matchListForThisDay.get(i).getTeam2());
            displayMatch.setId(matchListForThisDay.get(i).getId());
            displayMatch.setWinner(matchListForThisDay.get(i).getWinner());
            displayMatch.setPrediction("null");
            displayMatch.setRegion(selected_region_name);
            displayMatch.setYear(selected_region_name+year);
            displayMatch.setTeam1_score(matchListForThisDay.get(i).getTeam1_score());
            displayMatch.setTeam2_score(matchListForThisDay.get(i).getTeam2_score());
            //Log.d(TAG, "fromMatchDaysToDisplayMatch: "+displayMatch.getDatetime());

            if (displayMatch.getTeam1()== null){}else {
                displayMatchListSplit.add(displayMatch);
            }



        }



        //Log.d(TAG, "loadRecyclerView: $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+displayMatchListSplit.size());
        adapterRecycler = new RecyclerView_Picks_Adapter(this, displayMatchListSplit);
        adapterRecycler.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
/*        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }*/
        recyclerView.setAdapter(adapterRecycler);

        pick_progressbar_matches.setVisibility(View.GONE);

    }

    public Integer selectMatchDay(ArrayList<FullDate> matchDays) {
        //in base agli ID dell'array list, trova la data sucessiva o coincidente a quella attuale
        Integer itemPosition=0;

        for (int i=0; i< matchDays.size();i++){
           // Log.d(TAG, "loadMatchDays: "+matchDays.get(i));

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date strDate = null;
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            Date todays_date= null;

            try {
                strDate = sdf.parse(matchDays.get(i).getDate());
                todays_date = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            // Log.d(TAG, "selectMatchDay: System.currentTimeMillis(): "+System.currentTimeMillis());;

            long matchTimeMillis = strDate.getTime();
            long todays_date_millis = todays_date.getTime();

            //Log.d(TAG, "selectMatchDay: todays_date_millis:"+todays_date_millis);
            //Log.d(TAG, "selectMatchDay: matchTimeMillis:"+matchTimeMillis);

            if (todays_date_millis <= matchTimeMillis) {
                itemPosition = i;
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

    public void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }

}
