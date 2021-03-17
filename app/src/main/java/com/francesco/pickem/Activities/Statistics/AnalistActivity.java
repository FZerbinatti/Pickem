package com.francesco.pickem.Activities.Statistics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.francesco.pickem.Adapters.RecyclerView_Analyst_Adapter;
import com.francesco.pickem.Adapters.Region_selection_Adapter;
import com.francesco.pickem.Models.AnalistPerson;
import com.francesco.pickem.Models.ItemAnalistRecyclerVIew;
import com.francesco.pickem.NotificationsService.BackgroundTasks;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class AnalistActivity extends AppCompatActivity {

    ImageButton button_add_analyst, analists_back_arrow;
    RecyclerView analysts_recyclerview;
    ViewPager viewPager_AnalystsActivity;
    public static final String TAG ="AnalystsActivity";
    Context context;
    String imageRegionPath;
    Region_selection_Adapter adapterRegions;
    String selected_region_name;
    DatabaseHelper databaseHelper;
    Calendar myCalendar;
    String year;
    String today;
    BackgroundTasks backgroundTasks;
    TextView item_analists_date, item_analists_textview;
    ArrayList <ItemAnalistRecyclerVIew> itemsAnalistRecyclerVIew;
    RecyclerView_Analyst_Adapter adapterRecycler;
    SwipeRefreshLayout pullToRefreshanalysts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analist);

        context = this;
        analists_back_arrow = findViewById(R.id.analists_back_arrow);
        analysts_recyclerview = findViewById(R.id.analysts_recyclerview);
        button_add_analyst = findViewById(R.id.button_add_analyst);
        viewPager_AnalystsActivity = findViewById(R.id.viewPager_AnalystsActivity);
        imageRegionPath = context.getFilesDir().getAbsolutePath() + (getString(R.string.folder_regions_images));
        selected_region_name ="";
        databaseHelper = new DatabaseHelper(this);
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));
        backgroundTasks = new BackgroundTasks();
        item_analists_date=findViewById(R.id.item_analists_date);
        item_analists_textview= findViewById(R.id.item_analists_textview);
        pullToRefreshanalysts = findViewById(R.id.pullToRefreshanalysts);

        item_analists_date.setText(getLocalDateFromDateTime(backgroundTasks.getTodayDate()));

        itemsAnalistRecyclerVIew = new ArrayList<>();

        analists_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_add_analyst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalistActivity.this, AddAnalystActivity.class);
                startActivity(intent);
            }
        });

        downloadUserRegions();

        pullToRefreshanalysts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(); // your code
                pullToRefreshanalysts.setRefreshing(false);
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

                    loadViewPagerRegion(userRegions);
                }



            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    public void loadViewPagerRegion(ArrayList<String> userSelectedRegions){

        adapterRegions = new Region_selection_Adapter(userSelectedRegions, AnalistActivity.this);

        viewPager_AnalystsActivity = findViewById(R.id.viewPager_AnalystsActivity);
        viewPager_AnalystsActivity.setAdapter(adapterRegions);
        viewPager_AnalystsActivity.setPadding(410, 0, 400, 0);

        viewPager_AnalystsActivity.setCurrentItem(0);

        selected_region_name=userSelectedRegions.get(0);
        loadMatchDays(selected_region_name);


        viewPager_AnalystsActivity.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {




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

    private void loadMatchDays(String selected_region_name) {

        itemsAnalistRecyclerVIew = new ArrayList<>();
        itemsAnalistRecyclerVIew.addAll(databaseHelper.getItemsAnalystRecyclerVIewForRegionMatchDay(selected_region_name, backgroundTasks.getTodayDate()));
        if (!itemsAnalistRecyclerVIew.isEmpty()){
            item_analists_textview.setVisibility(View.INVISIBLE);
            analysts_recyclerview.setVisibility(View.VISIBLE);
            for(int i=0; i<itemsAnalistRecyclerVIew.size(); i++){
                Log.d(TAG, "loadMatchDays: lowkey cycle: " +itemsAnalistRecyclerVIew.get(i).getDatetime() + " t1: "+ itemsAnalistRecyclerVIew.get(i).getTeam1()+ " t2: "+ itemsAnalistRecyclerVIew.get(i).getTeam2());
            }

            //prendi gli analist seguiti dall'utente,
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getString(R.string.firebase_users_analists));

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int int_user_regions_selected = (int) snapshot.getChildrenCount();
                    if (int_user_regions_selected>0){
                        int counter = 0;
                        ArrayList <String> userAnalysts = new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            counter++;
                            String userAnalyst = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "onDataChange: Analyst found: "+userAnalyst);
                            userAnalysts.add(userAnalyst);
                        }
                        if (counter==int_user_regions_selected){
                            //solo quando hai downloddato tutti gli analysts
                            getIDforThieseAnalysts(userAnalysts,selected_region_name);
                        }
                    }else {
                        item_analists_date.setText("Add an analist to follow!");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }else {
            item_analists_textview.setVisibility(View.VISIBLE);
            analysts_recyclerview.setVisibility(View.INVISIBLE);
        }




    }

    private void getIDforThieseAnalysts(ArrayList<String> userAnalysts,String selected_region_name) {
        // prenmdi da analyst il loro user_id,
        for(int i=0; i<userAnalysts.size(); i++){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_analysts))
                    .child(userAnalysts.get(i));

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AnalistPerson userAnalyst = snapshot.getValue(AnalistPerson.class);
                    if (userAnalyst!=null){
                        Log.d(TAG, "onDataChange: Analyst: "+userAnalyst.getUsername() +" region: " +userAnalyst.getRegion()+ " id: "+userAnalyst.getUserId());


                        loadPredictionForThisAnalystForTHisRegion(userAnalyst.getUserId() , selected_region_name, userAnalyst.getUsername());
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }



    }

    private void loadPredictionForThisAnalystForTHisRegion(String userId, String selected_region_name, String analystName) {
        Log.d(TAG, "loadPredictionForThisAnalystForTHisRegion: "+ userId + " " + selected_region_name);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(userId)
                .child(getString(R.string.firebase_users_picks))
                .child(selected_region_name)
                .child(selected_region_name+year);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    String analystPredictionDate = dataSnapshot.getKey().toString();

                    String[] datetime = analystPredictionDate.split("T");

                    String data ="";

                    if (datetime.length ==2){
                        data = datetime[0];

                    }

                    if (data.equals(backgroundTasks.getTodayDate())){
                        Log.d(TAG, "onDataChange: " + data);
                        String analystPrediction = dataSnapshot.getValue(String.class);
                        ItemAnalistRecyclerVIew itemAnalistRecyclerVIew = new ItemAnalistRecyclerVIew(analystPredictionDate, "" , analystName, analystPrediction);
                        itemsAnalistRecyclerVIew.add(itemAnalistRecyclerVIew);
                        Log.d(TAG, "onDataChange: "+itemAnalistRecyclerVIew.getPrediction());

                        loadTest(itemsAnalistRecyclerVIew);

                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadTest(ArrayList<ItemAnalistRecyclerVIew> itemsAnalistRecyclerVIew) {

        Collections.sort(itemsAnalistRecyclerVIew, new ItemAnalistRecyclerVIew.ByDatetime());

        for(int i=0; i<itemsAnalistRecyclerVIew.size(); i++){
            Log.d(TAG, "loadTest: datetime: "+ itemsAnalistRecyclerVIew.get(i).getDatetime()  + " team 1: "+itemsAnalistRecyclerVIew.get(i).getTeam1()+ " team 2: "+itemsAnalistRecyclerVIew.get(i).getTeam2() + " prediction: "+itemsAnalistRecyclerVIew.get(i).getPrediction());
        }

        adapterRecycler = new RecyclerView_Analyst_Adapter(this, itemsAnalistRecyclerVIew);
        adapterRecycler.notifyDataSetChanged();
        analysts_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        analysts_recyclerview.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemAnimator animator = analysts_recyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        analysts_recyclerview.setAdapter(adapterRecycler);

    }

    private String getLocalDateFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTimeeee: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM  ");
        //SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);
        Log.d(TAG, "getLocalDateFromDateTimeeeee: localDatetime: "+localDatetime);

        return localDatetime;
    }

/*    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
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

    }*/


}