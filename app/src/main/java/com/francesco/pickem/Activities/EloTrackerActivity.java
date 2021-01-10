package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Adapters.EloTrackerRecyclerViewAdapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.RecyclerItemClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class EloTrackerActivity extends AppCompatActivity {

    Context context;
    ImageButton button_add_elotracker;
    RecyclerView recycler_eloTracker;
    EloTrackerRecyclerViewAdapter adapter;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String userID;
    private String TAG = "EloTRackerActivity";
    ProgressBar elotrack_progressbar;
    private XYPlot elotracker_graph;

    ArrayList<EloTracker> elotracker_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elo_tracker);
        context = this;
        addElo();
        recycler_eloTracker = findViewById(R.id.recycler_eloTracker);
        elotrack_progressbar = findViewById(R.id.elotrack_progressbar);
        elotracker_graph = (XYPlot) findViewById(R.id.elotracker_graph);
        elotrack_progressbar.setVisibility(View.VISIBLE);

        setupBottomNavView();
        loadFirebaseDataEloTracker();
        //loadGraph();


    }


    private void loadFirebaseDataEloTracker() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        Log.d(TAG, "onClick: year:"+year);

        elotracker_list = new ArrayList<>();


        // load da firebase le regioni
        FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_elotracker))
                .child(String.valueOf(year))
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){


                    EloTracker eloTracker = snapshot.getValue(EloTracker.class);
                    elotracker_list.add(eloTracker);

                }
                loadListview(elotracker_list);



            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "getAllRegions: regions.size(): "+ elotracker_list.size());



    }

    private void loadListview(ArrayList<EloTracker> eloTrackerArrayList) {


        // set up the RecyclerView


        recycler_eloTracker.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EloTrackerRecyclerViewAdapter(getApplicationContext(), eloTrackerArrayList);
        recycler_eloTracker.setAdapter(adapter);

        elotrack_progressbar.setVisibility(View.GONE);

        recycler_eloTracker.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recycler_eloTracker , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        //loadGraph();




    }

    private void addElo() {
        button_add_elotracker = findViewById(R.id.button_add_elotracker);
        button_add_elotracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EloTrackerActivity.this, NewTrackEloDay.class);
                startActivity(intent);
            }
        });


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

    private void loadGraph() {

        Log.d(TAG, "loadListview: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        // create a couple arrays of y-values to plot:
        Number[] domainLabels = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Number[] series2Numbers = {800, 842, 855, 810, 840, 855, 867, 880, 942, 988};

        Number [] domainEloNumbers = new Number[elotracker_list.size()];
        Number [] eloNumbers = new Number[elotracker_list.size()];


        for (int i=0; i< elotracker_list.size(); i++){
            eloNumbers[i] = (fromEloLpsToInteger(elotracker_list.get(i).getLps(), elotracker_list.get(i).getElo()));
            Log.d(TAG, "loadGraph: eloNumbers[i]: "+eloNumbers[i]);
            domainEloNumbers[i] = i;
            Log.d(TAG, "loadGraph: domainEloNumbers[i]:"+domainEloNumbers[i]);
        }


        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)

        XYSeries series2 = new SimpleXYSeries(
                //Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
                 Arrays.asList(eloNumbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:

        LineAndPointFormatter series2Format =
                //new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels_2);
               series2Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);

        // add an "dash" effect to the series2 line:
        series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[] {

                // always use DP when specifying pixel sizes, to keep things consistent across devices:
                PixelUtils.dpToPix(20),
                PixelUtils.dpToPix(15)}, 0));

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/


        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(100, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:

        elotracker_graph.addSeries(series2, series2Format);

        elotracker_graph.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                Log.d(TAG, "format: domainLabels[i]"+domainLabels[i]);
                //return toAppendTo.append(domainLabels[i]);
                return toAppendTo.append(domainEloNumbers[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });


    }
    
    public Integer fromEloLpsToInteger( Integer lps, String elo){
        
        if (elo.equals("Challenger")){
            return  1000+lps;
        }
        
        return 0;
    }

}