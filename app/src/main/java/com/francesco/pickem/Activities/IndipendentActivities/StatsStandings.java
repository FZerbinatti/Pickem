package com.francesco.pickem.Activities.IndipendentActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Activities.MainActivities.SettingsActivity;
import com.francesco.pickem.Activities.SettingsActivities.NotificationRegionActivity;
import com.francesco.pickem.Adapters.EloTrackerRecyclerViewAdapter;
import com.francesco.pickem.Adapters.RecyclerView_Statistics_Adapter;
import com.francesco.pickem.Adapters.StandingsRecyclerViewAdapter;
import com.francesco.pickem.Models.StandingTeams;
import com.francesco.pickem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class StatsStandings extends AppCompatActivity {

    ListView standings_regions_list, standings_listview;
    TextView standings_title;
    ImageView standings_region_logo;
    String imageRegionPath;
    Context context;
    public static final String TAG ="StatsStanding";
    ProgressBar standings_progressbar;
    Calendar myCalendar;
    String year;
    StandingsRecyclerViewAdapter adapter;
    RecyclerView standings_recyclerview;
    ImageButton back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_standings);
        context = this;
        standings_regions_list = findViewById(R.id.standings_regions_list);
        standings_recyclerview = findViewById(R.id.standings_recyclerview);
        standings_title = findViewById(R.id.standings_title);
        standings_region_logo = findViewById(R.id.standings_region_logo);
        standings_progressbar = findViewById(R.id.standings_progressbar);
        back_arrow = findViewById(R.id.back_arrow);


        imageRegionPath = context.getFilesDir().getAbsolutePath() + (getString(R.string.folder_regions_images));
        standings_progressbar.setVisibility(View.VISIBLE);
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));


        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getAllRegions();


    }

    public void  getAllRegions(){
        Log.d(TAG, "getAllRegions: ");

        ArrayList<String> regions = new ArrayList<>();

        // load da firebase le regioni
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_servers));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String regionName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+regionName);
                    regions.add(regionName);
                }
                loadListview(regions);
                standings_progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "getAllRegions: regions.size(): "+regions.size());

    }


    private void loadStanding(String regionSelected) {
        //load from firebase
        ArrayList <StandingTeams> standings = new ArrayList<>();

        String current_chart = "chart_"+year;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_statistics))
                .child(regionSelected)
                .child(regionSelected+year)
                .child(current_chart);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {


                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    StandingTeams standing = snapshot.getValue(StandingTeams.class);
                    standings.add(standing);
                }

                loadListviewStandings(standings);


            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadListviewStandings(ArrayList<StandingTeams> standings) {

        standings_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StandingsRecyclerViewAdapter(getApplicationContext(), standings);
        standings_recyclerview.setAdapter(adapter);

        standings_progressbar.setVisibility(View.GONE);
    }

    private void loadListview(ArrayList<String> allRegions) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                allRegions );



        standings_regions_list.setAdapter(arrayAdapter);

        standings_regions_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stringRegionSelected = allRegions.get(position);

                loadStanding(stringRegionSelected);
                loadBackground(stringRegionSelected);
                standings_title.setText(context.getString(R.string.current_standing_for)+" "+stringRegionSelected);


            }
        });

    }

    private void loadBackground(String regionSelected) {

        //non serve fare la query se hai il region name e applichi i parametri di cambio lettere/spazi
        String local_image =imageRegionPath+regionSelected.replace(" ", "")+".png";

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_load);



        Glide.with(context)
                .load(new File(local_image)) // Uri of the picture
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(standings_region_logo);





    }




}