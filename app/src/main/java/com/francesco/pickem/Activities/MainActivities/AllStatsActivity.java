package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Activities.EloTracker.EloTrackerActivity;
import com.francesco.pickem.Activities.IndipendentActivities.StatsStandings;
import com.francesco.pickem.Activities.Statistics.AnalistActivity;
import com.francesco.pickem.Activities.Statistics.StatsPicksActivity;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AllStatsActivity extends AppCompatActivity {

    Context context;
    ConstraintLayout cc1, cc2, cc3, cc4, cc5, cc6, cc7, cc8;
    ImageView all_stats_image_1, all_stats_image_2, all_stats_image_3, all_stats_image_4;
    ImageView all_stats_image_5, all_stats_image_6, all_stats_image_7, all_stats_image_8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stats);
        context = this;

        cc1 = findViewById(R.id.cc1);
        cc2 = findViewById(R.id.cc2);
        cc3 = findViewById(R.id.cc3);
        cc4 = findViewById(R.id.cc4);
        cc5 = findViewById(R.id.cc5);
        cc6 = findViewById(R.id.cc6);
        cc7 = findViewById(R.id.cc7);
        cc8 = findViewById(R.id.cc8);

        all_stats_image_1 = findViewById(R.id.all_stats_image_1);
        all_stats_image_2 = findViewById(R.id.all_stats_image_2);
        all_stats_image_3 = findViewById(R.id.all_stats_image_3);
        all_stats_image_4 = findViewById(R.id.all_stats_image_4);
        all_stats_image_5 = findViewById(R.id.all_stats_image_5);
        all_stats_image_6 = findViewById(R.id.all_stats_image_6);
        all_stats_image_7 = findViewById(R.id.all_stats_image_7);
        all_stats_image_8 = findViewById(R.id.all_stats_image_8);

        loadImages();
        loadButtons();
        setupBottomNavView();
    }

    private void loadButtons() {

        all_stats_image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllStatsActivity.this, StatsPicksActivity.class);
                startActivity(intent);
            }
        });

        all_stats_image_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllStatsActivity.this, AnalistActivity.class);
                startActivity(intent);
            }
        });

        all_stats_image_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllStatsActivity.this, StatsStandings.class);
                startActivity(intent);
            }
        });

        all_stats_image_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllStatsActivity.this, EloTrackerActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadImages() {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_loading_error);

        Glide
             .with(context)
             .load(R.drawable.piltover01)
             .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
             .placeholder(R.drawable.ic_load)
             .apply(options)
             .into(all_stats_image_1);

        Glide
                .with(context)
                .load(R.drawable.piltover02)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_load)
                .apply(options)
                .into(all_stats_image_2);

        Glide
                .with(context)
                .load(R.drawable.piltover03)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_load)
                .apply(options)
                .into(all_stats_image_3);

        Glide
                .with(context)
                .load(R.drawable.demacia0)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_load)
                .apply(options)
                .into(all_stats_image_4);

        Glide
                .with(context)
                .load(R.drawable.noxus01)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_load)
                .apply(options)
                .into(all_stats_image_5);

        Glide
                .with(context)
                .load(R.drawable.demacia02)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_load)
                .apply(options)
                .into(all_stats_image_6);

        Glide
                .with(context)
                .load(R.drawable.demacia01)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_load)
                .apply(options)
                .into(all_stats_image_7);

        Glide
                .with(context)
                .load(R.drawable.noxus02)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_load)
                .apply(options)
                .into(all_stats_image_8);


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
                        finish();
                        Animatoo.animateFade(context);
                        break;
                    case R.id.button_calendar:
                        Intent intentCalendar= new Intent(context, CalendarActivity.class);
                        startActivity(intentCalendar);
                        finish();
                        Animatoo.animateFade(context);
                        break;

                    case R.id.button_statistics:
                        Intent intentStats= new Intent(context, AllStatsActivity.class);
                        startActivity(intentStats);
                        finish();
                        Animatoo.animateFade(context);
                        break;

                    case R.id.button_settings:
                        Intent intentNotif= new Intent(context, SettingsActivity.class);
                        startActivity(intentNotif);
                        finish();
                        Animatoo.animateFade(context);
                        break;
                }
                return true;
            }
        });

    }
}