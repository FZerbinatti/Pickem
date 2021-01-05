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
import android.widget.ImageButton;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
import java.util.List;

public class PicksActivity extends AppCompatActivity  {

    ViewPager viewPager;
    Region_selection_Adapter adapter;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picks);
        recyclerView = findViewById(R.id.picksactivity_recyclerview);

        selectedRegions = new ArrayList<String>();
        displayRegions = new ArrayList<RegionDetails>();

        context = this;
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
/*        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));
        selectedRegions.add(new RegionDetails(1111111,R.drawable.logo_lck, "LCK", 3, "KOREA", "lck"));*/

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
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                        }

                    });


/*                    referenceRegions.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: snapshot.child(getString(R.string.regions_name)).getValue(String.class):"+snapshot.child(getString(R.string.regions_name)).getValue(String.class));

                                RegionDetails regionDetails = new RegionDetails();
                                regionDetails.setId(snapshot.child(getString(R.string.regions_id)).getValue(String.class));
                                regionDetails.setImage(snapshot.child(getString(R.string.regions_image)).getValue(String.class));
                                regionDetails.setName(snapshot.child(getString(R.string.regions_name)).getValue(String.class));

                                regionDetails.setPriority(snapshot.child(getString(R.string.regions_priority)).getValue(Integer.class));

                                regionDetails.setRegion(snapshot.child(getString(R.string.regions_region)).getValue(String.class));
                                regionDetails.setSlug(snapshot.child(getString(R.string.regions_slug)).getValue(String.class));


                                displayRegions.add(regionDetails);
                                Log.d(TAG, "onDataChange: adding region to regionDetail:"+regionDetails.getName());


                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                        }
                    });*/
                }

                adapter = new Region_selection_Adapter(displayRegions, PicksActivity.this);

                Log.d(TAG, "initializeLeagueSelection: "+selectedRegions.size());

                viewPager = findViewById(R.id.viewPager_picksActivity);
                viewPager.setAdapter(adapter);
                viewPager.setPadding(400, 0, 400, 0);

                Integer[] colors_temp = {
                        getResources().getColor(R.color.sfum1),
                        getResources().getColor(R.color.sfum2),
                        getResources().getColor(R.color.sfum3),
                        getResources().getColor(R.color.sfum4),
                        getResources().getColor(R.color.sfum5),
                        getResources().getColor(R.color.sfum6),
                        getResources().getColor(R.color.sfum7),
                        getResources().getColor(R.color.sfum8),
                        getResources().getColor(R.color.sfum9),
                        getResources().getColor(R.color.sfum10),
                        getResources().getColor(R.color.sfum11),
                        getResources().getColor(R.color.sfum12)
                };

                colors_backgroundlistview = colors_temp;

                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        if (position < (adapter.getCount() -1) && position < (colors_backgroundlistview.length - 1)) {

                            pick_background.setBackgroundColor(

                                    (Integer) argbEvaluator.evaluate(
                                            positionOffset,
                                            colors_backgroundlistview[position],
                                            colors_backgroundlistview[position + 1]
                                    )
                            );
                        }
                        else {
                            pick_background.setBackgroundColor(colors_backgroundlistview[colors_backgroundlistview.length - 1]);
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {

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

    public void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }


}