 package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
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
import com.francesco.pickem.NotificationsService.BackgroundTasks;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.PreferencesData;
import com.francesco.pickem.Services.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PicksActivity extends AppCompatActivity  {

    ViewPager viewPagerRegions, viewPager_match_day;
    Region_selection_Adapter adapterRegions;
    Day_selection_Adapter adapterDays;
    RecyclerView_Picks_Adapter adapterRecycler;
    ConstraintLayout pick_background;
    ImageButton notification, picks, calendar, stats;
    public static final String TAG ="Activity";
    Context context;
    ImageView pick_backgroundimage;
    RequestOptions options;
    ProgressBar pick_progressbar, pick_progressbar_matches;
    String selected_region_name;
    ArrayList<String> regionmatchDates;
    ArrayList <RegionDetails> displayRegions;
    ArrayList<FullDate> allFullDates;
    ArrayList<MatchDetails> matchListSplit;
    ArrayList <String> matchesForThisDate;
    ArrayList<DisplayMatch> displayMatchListSplit;
    ArrayList<String> selectedRegions;
    ArrayList <RegionDetails> allRegionsDetails;
    RecyclerView recyclerView;
    String day_selected;
    Calendar myCalendar;
    String year;
    String logo_URL;
    TextView no_match_found;
    ArrayList <String> list_of_splits;
    BackgroundTasks backgroundTasks;
    ImageView test;
    String imageRegionPath;
    DatabaseHelper databaseHelper;
    Integer selectedPage;
    SwipeRefreshLayout pullToRefresh;


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
        viewPagerRegions = findViewById(R.id.viewPager_picksActivity);
        databaseHelper = new DatabaseHelper(this);
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));
        pick_background = findViewById(R.id.pick_background);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        selected_region_name ="";
        context = this;
        pick_progressbar.setVisibility(View.VISIBLE);
        pick_progressbar_matches.setVisibility(View.VISIBLE);
        imageRegionPath = context.getFilesDir().getAbsolutePath() + (getString(R.string.folder_regions_images));


        if(isUserAlreadyLogged()){
            startBackgorundTasks();

            downloadUserRegions();
        }

        changeNavBarColor();
        setupBottomNavView();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });


    }

    private void refreshData() {

        loadMatchDays(selected_region_name);

    }


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
                ArrayList <String> userRegions = new ArrayList<>();

                //prendi tutte le regioni di interesse dello user
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    counter++;
                    String userRegion = snapshot.getValue(String.class);
                    userRegions.add(userRegion);

                }
                if (counter==int_user_regions_selected){
                    //solo quando hai downloddato tutte le user regions carichi il viewpager
                    pick_progressbar.setVisibility(View.INVISIBLE);
                    loadViewPagerRegion(userRegions);
                }



            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    public void loadViewPagerRegion(ArrayList<String> userSelectedRegions){

        adapterRegions = new Region_selection_Adapter(userSelectedRegions, PicksActivity.this);

        viewPagerRegions = findViewById(R.id.viewPager_picksActivity);
        viewPagerRegions.setAdapter(adapterRegions);
        viewPagerRegions.setPadding(410, 0, 400, 0);

        viewPagerRegions.setCurrentItem(0);

        selected_region_name=userSelectedRegions.get(0);
        loadMatchDays(selected_region_name);


        viewPagerRegions.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                String local_image =userSelectedRegions.get(position).replace(" ", "")+".png";
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

                selected_region_name = userSelectedRegions.get(position);

                loadMatchDays(selected_region_name);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state ==1 ){
                    selected_region_name="";
                }

            }
        });

    }

    public void loadMatchDays(String selected_region){

        ArrayList<String> matchDays = new ArrayList<>();
        matchDays = databaseHelper.getMatchDays(year, selected_region);


                if (matchDays.isEmpty()){
                    no_match_found.setVisibility(View.VISIBLE);
                    pick_progressbar_matches.setVisibility(View.GONE);
                    pick_progressbar.setVisibility(View.GONE);
                    viewPager_match_day.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }else {
                    no_match_found.setVisibility(View.INVISIBLE);
                    viewPager_match_day.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                    loadViewPagerMatchDays( matchDays, selected_region );
                }
    }

    private void loadViewPagerMatchDays(ArrayList<String> matchDays, String selected_region_name) {

        selectedPage = selectMatchDay(matchDays);

        viewPager_match_day = findViewById(R.id.viewPager_match_day);
        adapterDays = new Day_selection_Adapter((matchDays), PicksActivity.this);

        viewPager_match_day.setAdapter(adapterDays);
        viewPager_match_day.setPadding(300, 0, 300, 0);


        //Log.d(TAG, "loadViewPagerMatchDays: selectedPage: "+selectedPage);

        viewPager_match_day.setCurrentItem(selectedPage);
        day_selected = matchDays.get(selectedPage);

        downloadMatches( day_selected, selected_region_name);

        viewPager_match_day. setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position!=selectedPage){
                    //Log.d(TAG, "onPageScrolled: day: "+matchDays.get(position)+ " region: "+ selected_region_name + " position: "+position);
                    day_selected = matchDays.get(position);
                    downloadMatches( day_selected, selected_region_name);
                    selectedPage = position;
                }


            }

            @Override
            public void onPageSelected(int position) {


                //Log.d(TAG, "onPageSelected: day: "+matchDays.get(position)+ " region: "+ selected_region_name);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void downloadMatches(String loadMatchesForThisDate, String selected_region_name){
        //Log.d(TAG, "downloadMatches: loading matchs for this day: "+loadMatchesForThisDate +" - region: " +selected_region_name);

        matchListSplit = new ArrayList<>();
        matchesForThisDate = new ArrayList<>();

        // query sqlite per avere gli ID dei match di quel giorno di quella regione,
        matchesForThisDate = databaseHelper.getMatchIdsForThisDate(year, selected_region_name, loadMatchesForThisDate);

        // query firebase per avere gli oggetti MatchDetails
        for (int i = 0; i < matchesForThisDate.size(); i++){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Matches))
                    .child(selected_region_name)
                    .child(selected_region_name + year)
                    .child(matchesForThisDate.get(i));

            //Log.d(TAG, "loadRecyclerView: "+selected_region_name+"/"+selected_region_name + year+"/"+selected_region_name + year +"/"+matchesForThisDate.get(i));

            readData(reference, new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    //Log.d(TAG, "onSuccess: ");
                    MatchDetails matchDetails = dataSnapshot.getValue(MatchDetails.class);
                    //Log.d(TAG, "onSuccess: "+matchDetails.getDatetime());
                    if (matchDetails!=null){
                        //vedi se hai gia inserito i dettagli per questo match in locale

                        if (!databaseHelper.teamsInsertedForRegionDay(selected_region_name, getLocalDateFromDateTime(matchDetails.getDatetime()))){
                            databaseHelper.insertMatchDetails(selected_region_name,  matchDetails.getDatetime() , matchDetails.getTeam1(), matchDetails.getTeam2() );
                        }
                        matchListSplit.add(matchDetails);
                        //Log.d(TAG, "onSuccess: "+matchDetails.getDatetime() + " team1: "+ matchDetails.getTeam1()+ " - team2: " +matchDetails.getTeam2()) ;
                        //Log.d(TAG, "onSuccess: matchListSplit.size(): "+matchListSplit.size());
                        if (matchListSplit.size()== matchesForThisDate.size()){
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

    public String getDisplayDate(String dateString) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat coolDateFormatter = new SimpleDateFormat("EEE, d MMM  ");
        coolDateFormatter.setTimeZone(TimeZone.getDefault());
        String coolLocalDatetime = coolDateFormatter.format(value);

        return coolLocalDatetime;
    }

    public Integer selectMatchDay(ArrayList<String> matchDays) {
        //in base agli ID dell'array list, trova la data sucessiva o coincidente a quella attuale
        Integer itemPosition=0;

        for (int i=0; i< matchDays.size();i++){
            // Log.d(TAG, "loadMatchDays: "+matchDays.get(i));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date strDate = null;
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Date todays_date= null;

            try {
                strDate = sdf.parse(matchDays.get(i));
                todays_date = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long matchTimeMillis = strDate.getTime();

            long todays_date_millis = todays_date.getTime();


            if (todays_date_millis <= matchTimeMillis) {
                itemPosition = i;
                //Log.d(TAG, "selectMatchDay: itemPosition"+itemPosition);
                if (itemPosition <0){return 0;}else{return itemPosition;}
            }

        }

        return 0;
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

/*    private void createUserPicksForThisRegionIfNotExist(ArrayList<FullDate> allFullDates, String selected_region) {

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
    }*/

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
/*        for(int i=0; i<matchListForThisDay.size(); i++){
            Log.d(TAG, "fromMatchDaysToDisplayMatch:" +matchListForThisDay.get(i).getDatetime());
        }*/

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

    private String getLocalDateFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTime: datetime: "+datetime);
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
        Log.d(TAG, "getLocalDateFromDateTime: localDatetime: "+localDatetime);

        return localDatetime;
    }

    private void startBackgorundTasks() {
        Log.d(TAG, "startBackgorundTasks: ");

        ComponentName componentName = new ComponentName(this, BackgroundTasks.class);
        JobInfo info1 = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic( 60 * 60 * 1000) //una volta all'ora controlla se ci sono notifiche da settare
                .build();
        JobScheduler scheduler1 = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode1 =  scheduler1.schedule(info1);
        if (resultCode1 == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "onCreate: SUCCESS JOB SCHEDULER");
        }else {
            Log.d(TAG, "onCreate: DIO PORCO");
        }


        JobInfo info2 = new JobInfo.Builder(2, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(7* 24* 60* 60* 1000) // una volta a settimana controlla che le immagini in locale siano sync con le immagini sullo firestorage
                .build();
        JobScheduler scheduler2 = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode2 = scheduler2.schedule(info2);
        if (resultCode2 == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "onCreate: SUCCESS JOB SCHEDULER2");
        }else {
            Log.d(TAG, "onCreate: DIO PORCO2");
        }

        JobInfo info3 = new JobInfo.Builder(3, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(24* 60* 60* 1000) // una volta a settimana controlla che le immagini in locale siano sync con le immagini sullo firestorage
                .build();
        JobScheduler scheduler3 = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode3 = scheduler3.schedule(info3);
        if (resultCode3 == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "onCreate: SUCCESS JOB SCHEDULER3");
        }else {
            Log.d(TAG, "onCreate: DIO PORCO3");
        }

        JobInfo info4 = new JobInfo.Builder(4, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(24* 60* 60* 1000) // una volta a settimana controlla che le immagini in locale siano sync con le immagini sullo firestorage
                .build();
        JobScheduler scheduler4 = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode4 = scheduler4.schedule(info4);
        if (resultCode4 == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "onCreate: SUCCESS JOB SCHEDULER4");
        }else {
            Log.d(TAG, "onCreate: DIO PORCO4");
        }


    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }



}
