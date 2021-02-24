package com.francesco.pickem.Activities.EloTracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Activities.MainActivities.CalendarActivity;
import com.francesco.pickem.Activities.MainActivities.PicksActivity;
import com.francesco.pickem.Activities.MainActivities.SettingsActivity;
import com.francesco.pickem.Activities.MainActivities.StatsActivity;
import com.francesco.pickem.Adapters.EloTrackerRecyclerViewAdapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.RecyclerItemClickListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class EloTrackerActivity extends AppCompatActivity  implements OnChartGestureListener, OnChartValueSelectedListener {

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
    private BarChart chart;
    private SeekBar seekBarX, seekBarY;
    LineChart mChart;
    ArrayList<EloTracker> elotracker_list;
    EloActivity eloActivity;
    String year;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elo_tracker);
        context = this;
        addElo();
        recycler_eloTracker = findViewById(R.id.recycler_eloTracker);
        elotrack_progressbar = findViewById(R.id.elotrack_progressbar);
        //elotracker_graph = (XYPlot) findViewById(R.id.elotracker_graph);
        elotrack_progressbar.setVisibility(View.VISIBLE);
        mChart = findViewById(R.id.chart1);
        eloActivity = new EloActivity();
        calendar = Calendar.getInstance();
        year = String.valueOf(calendar.get(Calendar.YEAR));

        setupBottomNavView();
        loadFirebaseDataEloTracker();


        loadGraph(elotracker_list);





    }


    private void loadFirebaseDataEloTracker() {




        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        Log.d(TAG, "onClick: year:"+year);

        elotracker_list = new ArrayList<>();

        if (elotracker_list.size()>0){
            elotracker_list.clear();
        }

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
                loadGraph(elotracker_list);



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
                        Intent intent= new Intent(EloTrackerActivity.this, NewTrackEloDay.class);

                        intent.putExtra((getResources().getString(R.string.elotracker_id)), eloTrackerArrayList.get(position).getID());
                        intent.putExtra((getResources().getString(R.string.elotracker_date)),eloTrackerArrayList.get(position).getDate() );
                        intent.putExtra((getResources().getString(R.string.elotracker_wins)),eloTrackerArrayList.get(position).getWins().toString() );
                        intent.putExtra((getResources().getString(R.string.elotracker_losses)),eloTrackerArrayList.get(position).getLosses().toString() );
                        intent.putExtra((getResources().getString(R.string.elotracker_elo)),eloTrackerArrayList.get(position).getElo() );
                        intent.putExtra((getResources().getString(R.string.elotracker_lps)),eloTrackerArrayList.get(position).getLps().toString() );

                        startActivity(intent);

                    }

                    @Override public void onLongItemClick(View view, int position) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure to delete this record?");
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(getString(R.string.firebase_users_elotracker))
                                                .child(year)
                                                .child(elotracker_list.get(position).getDate());


                                        Log.d(TAG, "onClick: ID:"+elotracker_list.get(position).getDate());
                                        reference.removeValue();
                                        //loadFirebaseDataEloTracker();
                                        Intent intent= new Intent(EloTrackerActivity.this, EloTrackerActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        final AlertDialog dialog = builder.create();
                        dialog.show();




                    }
                })
        );



       // loadGraph(eloTrackerArrayList);




    }

    private void addElo() {
        button_add_elotracker = findViewById(R.id.button_add_elotracker);
        button_add_elotracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EloTrackerActivity.this, NewTrackEloDay.class);
                if (elotracker_list.size()>0){
                    Log.d(TAG, "onClick: elotracker_list.get(elotracker_list.size()-1).getElo(): "+elotracker_list.get(elotracker_list.size()-1).getElo());
                    intent.putExtra("EX_ELO", elotracker_list.get(elotracker_list.size()-1).getElo());
                }
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

    private void loadGraph(ArrayList<EloTracker> eloTrackerArrayList) {


        String [] dates = new String[]{};
        ArrayList<String> arrayOfDates = new ArrayList<>();
        ArrayList<Entry> eloValues = new ArrayList<>();

        for (int i=0; i< elotracker_list.size(); i++){
            eloValues.add(new Entry(i,fromEloLpsToInteger(elotracker_list.get(i).getLps(), elotracker_list.get(i).getElo())));
            arrayOfDates.add(elotracker_list.get(i).getDate());
        }
/*        chart.setOnChartValueSelectedListener((OnChartValueSelectedListener) this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);*/
/*        seekBarX = findViewById(R.id.seekBar1);
        seekBarY = findViewById(R.id.seekBar2);*/


/*        seekBarY.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
        seekBarX.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);*/


        LineDataSet set1= new LineDataSet(eloValues, "Elo graph");

        set1.setFillAlpha(110);

        set1.setColor(context.getColor(R.color.menu_background));
        set1.setValueTextSize(12f);
        set1.setValueTextColor(context.getColor(R.color.transparent));

        set1.notifyDataSetChanged();
        mChart.setOnChartGestureListener(EloTrackerActivity.this);
        mChart.setOnChartValueSelectedListener(EloTrackerActivity.this);

        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(true);


        mChart.getXAxis().setTextColor(Color.WHITE);


        YAxis leftAxis = mChart.getAxisLeft();
        ArrayList <String> allElos = eloActivity.getAllElos(context);


        for (int i=0; i< allElos.size(); i++){

            LimitLine eloLimit = new LimitLine(fromEloLpsToInteger(0,allElos.get(i)) , allElos.get(i));
            eloLimit.setLineWidth(1f);
            eloLimit.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
            eloLimit.setLineColor(context.getResources().getColor(R.color.blue_light));
            eloLimit.setTextColor(context.getResources().getColor(R.color.blue_light));






            leftAxis.addLimitLine(eloLimit);

        }

        //trasformare arrayStrin in String[]


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

/*        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(arrayOfDates));
        xAxis.setGranularity(1);
        xAxis.setTextColor(Color.WHITE);*/

        mChart.setData(data);
        mChart.invalidate();

        //togliere numeri sul graph, metti barre per gli elos



    }

/*    public class MyXAxisValueFormatter extends IndexAxisValueFormatter {
        private ArrayList<String> mValues;

        public MyXAxisValueFormatter(ArrayList<String> mValues) {
            this.mValues = mValues;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues.get((int) value);
        }
    }*/

    public Integer fromEloLpsToInteger( Integer lps, String elo){
        Log.d(TAG, "fromEloLpsToInteger:elo:"+elo);
        Integer eloPoints = lps + eloActivity.getEloPoints(elo, context);
        Log.d(TAG, "fromEloLpsToInteger: eloPoints: "+eloPoints);
        
        return eloPoints;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}