package com.dreamsphere.pickem.Activities.EloTracker;

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
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.dreamsphere.pickem.Activities.MainActivities.CalendarActivity;
import com.dreamsphere.pickem.Activities.MainActivities.PicksActivity;
import com.dreamsphere.pickem.Activities.MainActivities.SettingsActivity;
import com.dreamsphere.pickem.Activities.Statistics.StatsPicksActivity;
import com.dreamsphere.pickem.Adapters.EloTrackerRecyclerViewAdapter;
import com.dreamsphere.pickem.Annotation.NonNull;
import com.dreamsphere.pickem.Models.EloTracker;
import com.dreamsphere.pickem.Models.RegionServers;
import com.dreamsphere.pickem.Models.UserGeneralities;
import com.dreamsphere.pickem.R;
import com.dreamsphere.pickem.Services.JsonPlaceHolderAPI_Elo;
import com.dreamsphere.pickem.Services.JsonPlaceHolderAPI_Summoner;
import com.dreamsphere.pickem.Services.Post_Elo;
import com.dreamsphere.pickem.Services.Post_Summoner;
import com.dreamsphere.pickem.Services.RecyclerItemClickListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EloTrackerActivity extends AppCompatActivity  implements OnChartGestureListener, OnChartValueSelectedListener {

    Context context;
    ImageButton button_add_elotracker, button_update_elotracker;
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
        //loadFirebaseDataEloTracker();


        //loadGraph(elotracker_list);

        getLastEloTrackerUpdateIfPresent();





    }

    private void getLastEloTrackerUpdateIfPresent(){

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();
        getUserSummonerNameAndSoloqData();
    }


    private void loadFirebaseDataEloTracker() {






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
                        intent.putExtra((getResources().getString(R.string.elotracker_elo)),eloTrackerArrayList.get(position).getElo() );
                        intent.putExtra((getResources().getString(R.string.elotracker_lps)),eloTrackerArrayList.get(position).getLps().toString() );

                        startActivity(intent);
                        finish();

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
                finish();
            }
        });

/*        button_update_elotracker = findViewById(R.id.button_update_elotracker);
        button_update_elotracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //getUserSummonerNameAndSoloqData();

            }
        });*/


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
                        Intent intentStats= new Intent(context, StatsPicksActivity.class);
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

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setValueFormatter(new MyXAxisValueFormatter(arrayOfDates));
        xAxis.setGranularity(1);
        xAxis.setTextColor(Color.WHITE);

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
        //Log.d(TAG, "fromEloLpsToInteger:elo:"+elo);
        Integer eloPoints = lps + eloActivity.getEloPoints(elo, context);
       // Log.d(TAG, "fromEloLpsToInteger: eloPoints: "+eloPoints);
        
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

    public native String getKeys();
    static {
        System.loadLibrary("api-keys");
    }

    public String getTodayDate(){

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(c.getTime());
        return selectedDate;
    }

    private void getUserSummonerNameAndSoloqData(){

        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        reference.child(userID).child(getString(R.string.firebase_users_generealities)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                UserGeneralities userProfile = dataSnapshot.getValue(UserGeneralities.class);
                if (userProfile !=null){
                    String summonerName = userProfile.getSummoner_name();
                    String summonerServer = userProfile.getSummoner_server();

                    if (!summonerName.isEmpty() && !summonerServer.isEmpty()){
                        saveFirstElotracker(summonerName, summonerServer);
                    }else {}
                    loadFirebaseDataEloTracker();
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveFirstElotracker(String summonerName, String summoner_server) {


        // get summoner ID from summoner name + server
        // https://     euw1    .api.riotgames.com      /lol/summoner/v4/summoners/by-name/         DEMACIA%20REICH         ?api_key=       RGAPI-632893d3-8938-4031-a32e-4aa92062d229

        String address = getString(R.string.HTTP) + summoner_server +getString(R.string.riot_api_address);
        /// https://euw1.api.riotgames.com

        //summoner name + api_key_path + API key
        String end_path = summonerName + getString(R.string.key_request)+ getKeys();


        Log.d(TAG, "saveFirstElotracker: address: " +address + "/lol/summoner/v4/summoners/by-name/" +end_path);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/DEMACIA%20REICH?api_key=RGAPI-3c834326-87d8-479f-acb9-bf94f64212e0
        //https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/DEMACIA%20REICH?api_key=RGAPI-3c834326-87d8-479f-acb9-bf94f64212e0

        JsonPlaceHolderAPI_Summoner jsonPlaceHolderAPI_summoner = retrofit.create(JsonPlaceHolderAPI_Summoner.class);
        Log.d(TAG, "saveFirstElotracker: passando a getPost: "+end_path);
        // devi passare a GetPost:    DEMACIA%20REICH?api_key=RGAPI-3c834326-87d8-479f-acb9-bf94f64212e0
        Log.d(TAG, "saveFirstElotracker: deve essere = "+"DEMACIA REICH?api_key=RGAPI-3c834326-87d8-479f-acb9-bf94f64212e0");
        Call<Post_Summoner> callSummoner =  jsonPlaceHolderAPI_summoner.getPost(summonerName, getKeys()) ;

        callSummoner.enqueue(new Callback<Post_Summoner>() {
            @Override
            public void onResponse(Call<Post_Summoner> call, Response<Post_Summoner> response) {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: "+response.code());
                    Toast.makeText(context, "Something went wrong, check Summoner Name and Region selected!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse: "+ response.body().getId() +" summonerLevel:" + response.body().getSummonerLevel() + " summoner Name: "+ response.body().getName());
                //ora che hai lo il summonerID puoi fare l'altra call all'API

                JsonPlaceHolderAPI_Elo jsonPlaceHolderAPIElo = retrofit.create(JsonPlaceHolderAPI_Elo.class);

                Call<List<Post_Elo>> callElo = jsonPlaceHolderAPIElo.getPost(response.body().getId(), getKeys() );
                callElo.enqueue(new Callback<List<Post_Elo>>() {
                    @Override
                    public void onResponse(Call<List<Post_Elo>> call, Response<List<Post_Elo>> response) {
                        if (!response.isSuccessful()){
                            Log.d(TAG, "onResponse: "+response.code());
                            return;
                        }

                        List<Post_Elo> postElos = response.body();

                        for (Post_Elo postElo : postElos){
                            if (postElo.getQueueType().equals("RANKED_SOLO_5x5")){
                                Log.d(TAG, "onResponse: "+ postElo.getSummonerName() +" elo: " + postElo.getTier() + " " + postElo.getRank() +" " + postElo.getLeaguePoints()+ "LP");

                                String elo = postElo.getTier().substring(0, 1).toUpperCase() + postElo.getTier().substring(1).toLowerCase();

                                EloTracker eloTracker = new EloTracker(
                                        getTodayDate(),
                                        getTodayDate(),
                                        elo+" "+(postElo.getRank()),
                                        postElo.getLeaguePoints()
                                );

                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                Log.d(TAG, "onClick: year:"+year);


                                FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(getString(R.string.firebase_users_elotracker))
                                        .child(String.valueOf(year))
                                        .child(getTodayDate() )
                                        .setValue(eloTracker).addOnCompleteListener(task2 -> {

                                    loadFirebaseDataEloTracker();

                                });





                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Post_Elo>> call, Throwable t) {
                        Log.d(TAG, "onFailure: ERROR COMINCATING WITH API: "+t.getMessage());
                    }
                });

            }

            @Override
            public void onFailure(Call<Post_Summoner> call, Throwable t) {
                Log.d(TAG, "onFailure: ERROR COMINCATING WITH API: "+t.getMessage());
                Toast.makeText(context, "Something went wrong server-side, contact helpdesk", Toast.LENGTH_SHORT).show();
            }
        });



    }




}