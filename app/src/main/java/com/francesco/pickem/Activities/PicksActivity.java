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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Adapters.League_selection_Adapter;
import com.francesco.pickem.Adapters.RecyclerView_Picks_Adapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.SelectionLeague;
import com.francesco.pickem.Models.SingleMatch;
import com.francesco.pickem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class PicksActivity extends AppCompatActivity {

    ViewPager viewPager;
    League_selection_Adapter adapter;
    RecyclerView_Picks_Adapter adapterRecycler;
    List<SelectionLeague> selectedLeagues;
    Integer[] colors_backgroundlistview = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    ConstraintLayout pick_background;
    ImageButton notification, picks, calendar, stats;
    RecyclerView recyclerView;
    ArrayList<SingleMatch> singleMatchList;
    private String TAG ="PicksActivity";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picks);
        recyclerView = findViewById(R.id.picksactivity_recyclerview);
        context = this;
        changeNavBarColor();
        setupBottomNavView();
        initializeLeagueSelection();
        initRecyclerView();

    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: ");

        singleMatchList = new ArrayList<>();
        singleMatchList.add(new SingleMatch("FNC", "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/f/fc/Fnaticlogo_square.png/revision/latest?cb=20200124163013",
                                           "G2", "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/7/77/G2_Esportslogo_square.png/revision/latest?cb=20190201222017"));
        singleMatchList.add(new SingleMatch("RGE", "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/a/a4/Rogue_%28European_Team%29logo_square.png/revision/latest?cb=20190415174442",
                                           "SK", "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/4/4f/SK_Gaminglogo_square.png/revision/latest?cb=20180706022016"));


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


        adapterRecycler = new RecyclerView_Picks_Adapter(this, singleMatchList);
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

                    case R.id.button_notification:
                        Intent intentNotif= new Intent(context, NotificationActivity.class);
                        startActivity(intentNotif);
                        Animatoo.animateFade(context);
                        break;
                }
                return true;
            }
        });

    }

    private void initializeLeagueSelection() {

        viewPager = findViewById(R.id.viewPager_picksActivity);
        pick_background = findViewById(R.id.pick_background);


        selectedLeagues = new ArrayList<>();
        selectedLeagues.add(new SelectionLeague(R.drawable.logo_lck, "LCK"));
        selectedLeagues.add(new SelectionLeague(R.drawable.logo_lec, "LEC"));
        selectedLeagues.add(new SelectionLeague(R.drawable.logo_lpl, "LPL"));
        selectedLeagues.add(new SelectionLeague(R.drawable.logo_lcs, "LCS"));

        adapter = new League_selection_Adapter(selectedLeagues, this);

        viewPager = findViewById(R.id.viewPager_picksActivity);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(400, 0, 400, 0);


        Integer[] colors_temp = {
                getResources().getColor(R.color.sfum1),
                getResources().getColor(R.color.sfum2),
                getResources().getColor(R.color.sfum3),
                getResources().getColor(R.color.sfum4)
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

    public void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }
}