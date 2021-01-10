package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.text.DateFormat;
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
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.RegionDetails;
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

public class PicksActivity extends AppCompatActivity  {

    ViewPager viewPager, viewPager_match_day;
    Region_selection_Adapter adapterRegions;
    Day_selection_Adapter adapterDays;

    RecyclerView_Picks_Adapter adapterRecycler;
    List<String> selectedRegions;
    ArrayList <RegionDetails> displayRegions;
    Integer[] colors_backgroundlistview = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    ConstraintLayout pick_background;
    ImageButton notification, picks, calendar, stats;
    RecyclerView recyclerView;
    ArrayList<MatchDetails> matchDetailsList;
    private String TAG ="PicksActivity";
    Context context;
    ImageView pick_backgroundimage;
    RequestOptions options;
    ProgressBar pick_progressbar;
    String selected_region_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picks);
        recyclerView = findViewById(R.id.picksactivity_recyclerview);

        pick_backgroundimage = findViewById(R.id.pick_backgroundimage);
        pick_progressbar = findViewById(R.id.pick_progressbar);

        selectedRegions = new ArrayList<String>();
        displayRegions = new ArrayList<RegionDetails>();
        selected_region_name ="";

        context = this;
        pick_progressbar.setVisibility(View.VISIBLE);
        changeNavBarColor();
        setupBottomNavView();
        initializeLeagueSelection();
        initRecyclerView();




    }


    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: ");

        matchDetailsList = new ArrayList<>();

        matchDetailsList.add(new MatchDetails(0000001, "24/01/2020", "17.00" ,"FNC", "G2",
                  "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/f/fc/Fnaticlogo_square.png/revision/latest?cb=20200124163013",
                  "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/7/77/G2_Esportslogo_square.png/revision/latest?cb=20190201222017",1,1));

        matchDetailsList.add(new MatchDetails(0000002, "24/01/2020", "18.00" ,"RGE", "SK",
                 "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/a/a4/Rogue_%28European_Team%29logo_square.png/revision/latest?cb=20190415174442",
                 "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/4/4f/SK_Gaminglogo_square.png/revision/latest?cb=20180706022016",1,2));

        matchDetailsList.add(new MatchDetails(0000003, "24/01/2020", "19.30" ,"FNC", "G2",
                "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/f/fc/Fnaticlogo_square.png/revision/latest?cb=20200124163013",
                "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/7/77/G2_Esportslogo_square.png/revision/latest?cb=20190201222017",2,2));

        matchDetailsList.add(new MatchDetails(0000004, "24/01/2020", "20.00" ,"RGE", "SK",
                "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/a/a4/Rogue_%28European_Team%29logo_square.png/revision/latest?cb=20190415174442",
                "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/4/4f/SK_Gaminglogo_square.png/revision/latest?cb=20180706022016",-1,1));

        matchDetailsList.add(new MatchDetails(0000005, "24/01/2020", "21.10" ,"RGE", "SK",
                "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/a/a4/Rogue_%28European_Team%29logo_square.png/revision/latest?cb=20190415174442",
                "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/4/4f/SK_Gaminglogo_square.png/revision/latest?cb=20180706022016",-1,-1));



/*        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                ArrayList<String> images_paths = null;
                // open the Full screen image display
                Intent intent = new Intent(MainActivity.this, MovieDetails.class);
                // passa solamente una lista dei path delle immagini
                for (int i=0; i<movieList.size(); i++){
                    images_paths.add(movieList.get(i).getMovie_poster());
                }

                intent.putExtra("MOVIE_PHOTO", "") ;
                startActivity(intent);


            }
        };*/


        adapterRecycler = new RecyclerView_Picks_Adapter(this, matchDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterRecycler);
        //adapter.notifyDataSetChanged();

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

    private void initializeLeagueSelection() {
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


                //getAllMatchDaysForThisRegion(selectedRegions.get(0));


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

                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        RequestOptions options = new RequestOptions()
                                .fitCenter()
                                .error(R.drawable.ic_load);

                        Glide.with(context).load(displayRegions.get(position).getImage()).apply(options).transition(DrawableTransitionOptions.withCrossFade(500)).into(pick_backgroundimage);
                        selected_region_name = displayRegions.get(position).getName();
                        getAllMatchDaysForThisRegion(selected_region_name);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        selected_region_name = displayRegions.get(position).getName();


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

        pick_progressbar.setVisibility(View.GONE);




    }




    public void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }

    public String formNumberToDayweek(Integer dayofWeek){

        String lunedi    = getString(R.string.lunedi);
        String martedi   = getString(R.string.martedi  );
        String mercoledi    = getString(R.string.mercoledi);
        String giovedi  = getString(R.string.giovedi  );
        String venerdi  = getString(R.string.venerdi  );
        String sabato     = getString(R.string.sabato   );
        String domenica     = getString(R.string.domenica   );


        if (dayofWeek == 1){return lunedi;
        }else if (dayofWeek == 2){return martedi;
        }else if (dayofWeek == 3){return mercoledi;
        }else if (dayofWeek == 4){return giovedi;
        }else if (dayofWeek == 5){return venerdi;
        }else if (dayofWeek == 6){return sabato;
        }else {return  domenica; }



/*        if (dayofWeek == 1){return  getApplicationContext().getResources().getString(R.string.lunedi);
        }else if (dayofWeek == 2){return  getResources().getString(R.string.martedi);
        }else if (dayofWeek == 3){return  getResources().getString(R.string.mercoledi);
        }else if (dayofWeek == 4){return  getResources().getString(R.string.giovedi);
        }else if (dayofWeek == 5){return  getResources().getString(R.string.venerdi);
        }else if (dayofWeek == 6){return  getResources().getString(R.string.sabato);
        }else {return  this.getString(R.string.domenica); }*/

    }

    public String formNumberToMonth(String month_number){

        if (month_number.equals("1")){return        getResources().getString(R.string.gennaio);
        }else if (month_number.equals("2")){return  getResources().getString(R.string.febbraio);
        }else if (month_number.equals("3")){return  getResources().getString(R.string.marzo);
        }else if (month_number.equals("4")){return  getResources().getString(R.string.aprile);
        }else if (month_number.equals("5")){return  getResources().getString(R.string.maggio);
        }else if (month_number.equals("6")){return  getResources().getString(R.string.giugno);
        }else if (month_number.equals("7")){return  getResources().getString(R.string.luglio);
        }else if (month_number.equals("8")){return  getResources().getString(R.string.agosto);
        }else if (month_number.equals("9")){return  getResources().getString(R.string.settembre);
        }else if (month_number.equals("10")){return  getResources().getString(R.string.ottobre);
        }else if (month_number.equals("11")){return  getResources().getString(R.string.novembre);
        }else if (month_number.equals("12")){return this.getString(R.string.dicembre); }
        else  return "error";

    }





    private void loadMatchDays(ArrayList<String> matchDays, Integer selectedPage) {
        Log.d(TAG, "loadMatchDays: matchDays: " +matchDays.size());
        for (int i=0; i< matchDays.size();i++){
            Log.d(TAG, "loadMatchDays: "+matchDays.get(i));
        }

        viewPager_match_day = findViewById(R.id.viewPager_match_day);
//interpol
                adapterDays = new Day_selection_Adapter((matchDays), PicksActivity.this);

                Log.d(TAG, "initializeLeagueSelection: "+selectedRegions.size());

                viewPager_match_day.setAdapter(adapterDays);
                viewPager_match_day.setPadding(200, 0, 200, 0);

                viewPager_match_day.setCurrentItem(selectedPage);

                viewPager_match_day.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        String day_selected = matchDays.get(position);
                        Log.d(TAG, "onPageScrolled: id day selected: "+day_selected);

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

        pick_progressbar.setVisibility(View.GONE);

    }

    public Integer selectMatchDay(ArrayList<String> matchDays) throws ParseException {
        //in base agli ID dell'array list, trova la data sucessiva o coincidente a quella attuale
        Integer itemPosition=0;

        for (int i=0; i< matchDays.size();i++){
            Log.d(TAG, "loadMatchDays: "+matchDays.get(i));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date strDate = sdf.parse(matchDays.get(i));


            Log.d(TAG, "selectMatchDay: System.currentTimeMillis(): "+System.currentTimeMillis());;

            long matchTimeMillis = strDate.getTime();

            Log.d(TAG, "selectMatchDay: matchTimeMillis:"+matchTimeMillis);
            if (System.currentTimeMillis() <= matchTimeMillis) {
                itemPosition = i-1;
                return itemPosition;
            }

        }

       return 0;
    }

    //1610203262423 now
    //1610060400000 08 gen

    //1610203262423 now
    //1612047600000 31gen



    public void getAllMatchDaysForThisRegion(String selected_region){
        Log.d(TAG, "getAllMatchDays: selected_region: "+selected_region);
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        ArrayList<String> matchDays = new ArrayList<>();
        ArrayList<String> matchDaysID = new ArrayList<>();

        // load da firebase le regioni
        Log.d(TAG, "getAllMatchDays: path: "+getString(R.string.match)+"/"+ selected_region +"/"+ selected_region + year);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.match))
                .child(selected_region)
                .child(selected_region + year);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // prendi tutti i giorni disponibili per quell'anno per quelal regione

                    String matchDayDetails = (snapshot.getKey().toString());

                    Log.d(TAG, "onDataChange: regionName: "+matchDayDetails);
                    matchDays.add(fromIDtoStringDate(matchDayDetails));
                    matchDaysID.add(matchDayDetails);


                }
                Log.d(TAG, "onDataChange: matchDays.size()"+matchDays.size());
                try {
                    loadMatchDays(matchDays,selectMatchDay(matchDaysID) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                pick_progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    public void  testDays(){
        Log.d(TAG, "testDays: ");

        ArrayList<String> days = new ArrayList<>();

        // load da firebase le regioni

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TestMatch")
                .child("LEC")
                .child("LEC2021");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String dayName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+dayName);
                    days.add(dayName);
                }

                Log.d(TAG, "onDataChange: days gathered: "+days.size());

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onDataChange: days gathered: "+days.size());

    }

    public String fromIDtoStringDate(String id){
        Log.d(TAG, "fromIDtoStringDate: id:"+id);

        String[] dateStrings = id.split("-");

        int year = 0;
        int month = 0;
        int day = 0;

        Date date = new Date();

        if (dateStrings.length == 3) {

            year = Integer.parseInt(dateStrings[0]);
            month = Integer.parseInt(dateStrings[1]);
            day = Integer.parseInt(dateStrings[2]);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = dateFormat.parse(id);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        String stringDay = formNumberToDayweek(dayOfWeek);

        Log.d(TAG, "fromIDtoStringDate: String.valueOf(month): "+String.valueOf(month));
        String stringMonth = formNumberToMonth(String.valueOf(month));

        Log.d(TAG, "fromIDtoStringDate: "+stringDay);
        Log.d(TAG, "fromIDtoStringDate: "+ stringMonth );
        Log.d(TAG, "fromIDtoStringDate: "+ day);

        return stringDay + " " + stringMonth + " " + day;
    }

}