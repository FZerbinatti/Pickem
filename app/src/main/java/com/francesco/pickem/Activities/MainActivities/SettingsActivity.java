package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Activities.SettingsActivities.InfoActivity;
import com.francesco.pickem.Activities.AccountActivities.LoginActivity;
import com.francesco.pickem.Activities.SettingsActivities.NotificationRegionActivity;
import com.francesco.pickem.Adapters.SimpleRegionRecyclerViewAdapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.SimpleRegion;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.PreferencesData;
import com.francesco.pickem.Services.RecyclerItemClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switch_notification, switch_user_settings;
    ConstraintLayout show_notifications_box, show_user_box;
    private String TAG ="NotificationActivity: ";
    Context context;
    ImageButton button_logout;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private DatabaseReference user_preferences_reference;

    RecyclerView settings_recycler_regioni;
    ListView settings_regions_listview;
    ScrollView parentScrollListener;
    EditText edittext_summoner_name;
    Spinner spinner_choose_server;
    Button button_save_elotracker_info;
    SwitchCompat switch_elotracker;
    ConstraintLayout show_elotracker_box;
    ArrayList <String> finalRegionChoosen;
    public static String REGION_SELECTED = "REGION_SELECTED";
    private String userID;
    Button button_save_settings_account;
    RegionNotifications setnot_object;
    ProgressBar settings_progressbar;
    EditText edittext_emailaddress;
    ArrayList <String> userChoosenRegionsfromDB;
    ArrayList<String> servers ;
    ArrayList<SimpleRegion> user_choosen_regions;
    ArrayList<SimpleRegion> full_list_withUserChoiche;
    ArrayList<String> finalRegions;
    SimpleRegionRecyclerViewAdapter adapter;
    ArrayList<String> choosen_regions;
    ArrayList<String> old_choosen_regions;
    ArrayList<String> new_choosen_regions;

    ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switch_notification = (SwitchCompat)  findViewById(R.id.switch_notification);
        switch_user_settings = (SwitchCompat)  findViewById(R.id.switch_notificationx);
        show_notifications_box = findViewById(R.id.show_notifications_box);
        show_user_box = findViewById(R.id.show_notifications_boxx);
        button_logout = findViewById(R.id.button_logout);
        edittext_emailaddress = findViewById(R.id.edittext_emailaddress);
        button_save_settings_account = findViewById(R.id.button_save_settings_account);
        firebaseAuth = FirebaseAuth.getInstance();
        settings_progressbar = findViewById(R.id.settings_progressbar);
        settings_regions_listview = findViewById(R.id.settings_regions_listview);
        settings_recycler_regioni = findViewById(R.id.settings_recyclerview_regioni);
        parentScrollListener = findViewById(R.id.parentScrollListener);
        info = findViewById(R.id.info_button);

        userChoosenRegionsfromDB = new ArrayList<>();

        edittext_emailaddress.setFocusable(false);
        edittext_emailaddress.setTextColor(Color.GRAY);

        edittext_summoner_name = findViewById(R.id.edittext_summoner_name);
        spinner_choose_server= findViewById(R.id.spinner_choose_server);
        button_save_elotracker_info = findViewById(R.id.button_save_elotracker_info);
        switch_elotracker = findViewById(R.id.switch_elotracker);
        show_elotracker_box = findViewById(R.id.show_elotracker_box);
        ArrayList<String> servers = new ArrayList<>();



        parentScrollListener.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                findViewById(R.id.settings_regions_listview).getParent().requestDisallowInterceptTouchEvent(false);
                findViewById(R.id.settings_recyclerview_regioni).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        settings_regions_listview.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {

                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        settings_recycler_regioni.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {

                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        context = this;
        infoButton();
        setupBottomNavView();
        buttonActions();
        changeNavBarColor();
        saveGeneralSettings();
        saveEloTracker();


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();
        Log.d(TAG, "onCreate: userID: "+userID);


    }

    private void infoButton() {

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });


    }

    private void loadListview(ArrayList<String> allRegions) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                allRegions );



        settings_regions_listview.setAdapter(arrayAdapter);

        settings_regions_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stringRegionSelected = allRegions.get(position);
                Intent intent = new Intent(SettingsActivity.this, NotificationRegionActivity.class);
                intent.putExtra(REGION_SELECTED, stringRegionSelected);
                startActivity(intent);

            }
        });

    }

    private void loadListviewChooseRegions(ArrayList<SimpleRegion> all_regions, ArrayList <String> onlyUserChoosenRegions) {

        Log.d(TAG, "loadListviewChooseRegions: "+ all_regions.size());
        settings_progressbar.setVisibility(View.GONE);

        finalRegions = new ArrayList<>();
        for (int i=0; i<onlyUserChoosenRegions.size();i++){
            finalRegions.add(onlyUserChoosenRegions.get(i));
        }

        // set up the RecyclerView

        settings_recycler_regioni.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SimpleRegionRecyclerViewAdapter(getApplicationContext(), all_regions);
        settings_recycler_regioni.setAdapter(adapter);



        settings_recycler_regioni.addOnItemTouchListener(
                new RecyclerItemClickListener(context, settings_recycler_regioni , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Log.d(TAG, "onLongItemClick: ");
                        finalRegions= new ArrayList<>();

                        if (all_regions.get(position).getChecked()){
                            all_regions.get(position).setChecked(false);
                            adapter.notifyDataSetChanged();
                            //Log.d(TAG, "onLongItemClick: false");
/*                            if (finalRegions.contains(all_regions.get(position).getName())){
                                finalRegions.remove(all_regions.get(position).getName());
                                Log.d(TAG, "onLongItemClick: rimuovo: "+all_regions.get(position).getName());
                            }*/
                        } else {
                            all_regions.get(position).setChecked(true);
                            //Log.d(TAG, "onLongItemClick: true");
                            adapter.notifyDataSetChanged();
/*
                            if (!finalRegions.contains(all_regions.get(position).getName())){
                                finalRegions.add(all_regions.get(position).getName());
                                Log.d(TAG, "onLongItemClick: aggiungo: "+all_regions.get(position).getName());
                            }
*/

                        }

                        //Log.d(TAG, "onLongItemClick: "+all_regions.get(position).getName()+":"+all_regions.get(position).getChecked());
                        for(int i=0;i<all_regions.size();i++){
                            if(all_regions.get(i).getChecked()){
                                finalRegions.add(all_regions.get(i).getName());
                                Log.d(TAG, "onLongItemClick: final regions: "+all_regions.get(i).getName());
                            }
                        }


                    }
                })
        );





    }

    private void updateChoosenRegion(String stringRegionSelected, Boolean booleanRegionSelected){


        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getResources().getString(R.string.firebase_users_generealities))
                .child(getResources().getString(R.string.firebase_user_choosen_regions))
                .setValue(stringRegionSelected);
    }

    private void loadFirebaseData() {
        old_choosen_regions = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();

        userID = user.getUid();
        user_choosen_regions = new ArrayList<>();
        full_list_withUserChoiche = new ArrayList<>();
        ArrayList<String> all_databaseRegions = new ArrayList<>();

        // load da firebase le regioni
        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_regions));
        datareference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String regionName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+regionName);
                    all_databaseRegions.add(regionName );
                }

                reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
                reference.child(userID).child(getString(R.string.firebase_users_generealities)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                        UserGeneralities userProfile = dataSnapshot.getValue(UserGeneralities.class);
                        if (userProfile !=null){

                            String database_email = userProfile.getEmail();

                            edittext_emailaddress.setText(database_email);

                            userChoosenRegionsfromDB = userProfile.getChoosen_regions();
                            old_choosen_regions = userChoosenRegionsfromDB;
                            //per ogni regione esistente
                            for (int i=0; i<all_databaseRegions.size(); i++){
                                //se tutte le regioni contiene la regione scelta dall'utente
                                if (userChoosenRegionsfromDB.contains(all_databaseRegions.get(i))){
                                    full_list_withUserChoiche.add(new SimpleRegion(all_databaseRegions.get(i), true));
                                }else {
                                    full_list_withUserChoiche.add(new SimpleRegion(all_databaseRegions.get(i), false));
                                }


                            }
                            loadListviewChooseRegions(full_list_withUserChoiche, userChoosenRegionsfromDB);

                        }


                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                    }
                });

                settings_progressbar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });




    }

    private void saveGeneralSettings() {

        button_save_settings_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settings_progressbar.setVisibility(View.VISIBLE);
                new_choosen_regions = new ArrayList<>();
                choosen_regions = new ArrayList<>();
                Log.d(TAG, "onClick: 1+++++++++++++++++++++++++++++++"+finalRegions.size());
                for (int i=0; i< finalRegions.size(); i++){
                    Log.d(TAG, "/////////////////////////////////////////////////////////////: "+finalRegions.get(i));
                }


                choosen_regions = finalRegions;
                new_choosen_regions = choosen_regions;






                if (choosen_regions.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "You must chose at least one Region to follow", Toast.LENGTH_SHORT).show();
                    settings_progressbar.setVisibility(View.INVISIBLE);
                }else {
                    UserGeneralities userGeneralities = new UserGeneralities( edittext_emailaddress.getText().toString(),"", "", finalRegions, "");
                    updateUserGeneralities(userGeneralities);


                    for (int i=0; i< old_choosen_regions.size(); i++){
                        if (choosen_regions.contains(old_choosen_regions.get(i))){
                            old_choosen_regions.remove(old_choosen_regions.get(i));
                        }
                    }removeNotificationNoChoosenMatchForThisRegion(old_choosen_regions);

                    //LEC LCK LCS
                    //LEC LPL
                    for (int i=0; i< new_choosen_regions.size(); i++){
                        if (old_choosen_regions.contains(new_choosen_regions.get(i))){
                            new_choosen_regions.remove(new_choosen_regions.get(i));
                        }
                    }
                    addNotificationNoChoosenMatchForThisRegion(new_choosen_regions);


                    Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                    settings_progressbar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }







            }
        });

    }

    private void removeNotificationNoChoosenMatchForThisRegion(ArrayList<String> old_choosen_regions) {

        for (int i=0; i<old_choosen_regions.size();i++){

            FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getString(R.string.firebase_user_notification))
                    .child(getString(R.string.firebase_user_notification_region))
                    .child(old_choosen_regions.get(i))
                    .child(getString(R.string.notification_no_choice_made))
                    .setValue(0);
        }



    }

    private void addNotificationNoChoosenMatchForThisRegion(ArrayList<String> new_choosen_regions) {

        for (int i=0; i<new_choosen_regions.size();i++){

            RegionNotifications regionNotifications = new RegionNotifications();
            regionNotifications.setNo_choice_made(1);
            regionNotifications.setNotification_first_match_otd(0);
            regionNotifications.setNotification_morning_reminder(0);
            regionNotifications.setRegion_name(new_choosen_regions.get(i));

            FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getString(R.string.firebase_user_notification))
                    .child(getString(R.string.firebase_user_notification_region))
                    .child(new_choosen_regions.get(i))
                    .setValue(regionNotifications);
        }



    }

    public void updateUserGeneralities (UserGeneralities userGeneralities){


        //remove all old regions
                FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getString(R.string.firebase_users_generealities))
                    .child(getString(R.string.firebase_user_notification_region))
                    .removeValue();

        //add all selected regions

        FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_generealities))
                .child(getString(R.string.firebase_user_choosen_regions))
                .setValue(userGeneralities.getChoosen_regions()).addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()){
                Log.d(TAG, "added: Notification Region ");

            }else{
                Log.d(TAG, "ERROR: registered: Notification Region ");
            }
        });



}

    private void buttonActions() {

        switch_elotracker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (switch_elotracker.isChecked()){
                    show_elotracker_box.setVisibility(View.VISIBLE);
                    getAllServers();
                    loadFirebaseEloTrackingData();
                }else {
                    Log.d(TAG, "onCreate: OFF");
                    show_elotracker_box.setVisibility(View.GONE);
                }
            }
        });

        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (switch_notification.isChecked()){
                    Log.d(TAG, "onCreate: ON");
                    show_notifications_box.setVisibility(View.VISIBLE);
                    settings_progressbar.setVisibility(View.VISIBLE);
                    getAllRegions();
                }else {
                    Log.d(TAG, "onCreate: OFF");
                    show_notifications_box.setVisibility(View.GONE);
                }
            }
        });

        switch_user_settings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (switch_user_settings.isChecked()){
                    Log.d(TAG, "onCreate: ON");
                    show_user_box.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Long Press to select / deselect ", Toast.LENGTH_SHORT).show();
                    loadFirebaseData();
                }else {
                    Log.d(TAG, "onCreate: OFF");
                    show_user_box.setVisibility(View.GONE);
                }
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Do you want to Logout?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                PreferencesData.setUserLoggedInStatus(getApplicationContext(),false);
                                firebaseAuth.signOut();
                                startActivity(intent);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();


            }
        });
    }

    private void setupBottomNavView() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.button_settings);
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
                        /*Intent intentNotif= new Intent(context, SettingsActivity.class);
                        startActivity(intentNotif);
                        Animatoo.animateFade(context);*/
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

    public void  getAllRegions(){
        Log.d(TAG, "getAllRegions: ");

        ArrayList<String> regions = new ArrayList<>();

        // load da firebase le regioni
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_regions));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String regionName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+regionName);
                    regions.add(regionName);
                }
                loadListview(regions);
                settings_progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "getAllRegions: regions.size(): "+regions.size());

    }

    public void  getAllServers(){
        Log.d(TAG, "getAllServers: ");
        servers= new ArrayList<>();

        // load da firebase i servers
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_servers));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String serverName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+serverName);
                    servers.add(serverName);
                }
                settings_progressbar.setVisibility(View.GONE);
                Log.d(TAG, "onDataChange: "+servers.size());
                loadSpinner(servers);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadSpinner(ArrayList<String> allServers) {
        Log.d(TAG, "loadSpinner: "+allServers.size());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.spinner_item, allServers);
        spinner_choose_server.setAdapter(adapter);
        spinner_choose_server.setSelection(2);

    }

    private void saveEloTracker(){

        button_save_elotracker_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");

        String summonerName = edittext_summoner_name.getText().toString();
        String summoner_server = spinner_choose_server.getSelectedItem().toString();

                Log.d(TAG, "onClick: summonerName: "+summonerName);
                Log.d(TAG, "onClick: summoner_server"+summoner_server);

        if (summonerName.isEmpty()){edittext_summoner_name.setError("Please Insert your Summoner Name");}else
            if (summoner_server.isEmpty()){
                Toast.makeText(context, "Select Your Region Server!", Toast.LENGTH_SHORT).show();
                }else {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.firebase_users_generealities));

                            reference.child(getString(R.string.summoner_name)).setValue(summonerName);
                            reference.child(getString(R.string.summoner_server)).setValue(summoner_server);

                Log.d(TAG, "onClick: DONE ");

                show_elotracker_box.setVisibility(View.GONE);
                switch_elotracker.setChecked(false);


                }
            }
        });
    }

    private void loadFirebaseEloTrackingData() {


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();


        Log.d(TAG, "onCreate: userID: "+userID);
        reference.child(userID).child(getString(R.string.firebase_users_generealities)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                UserGeneralities userProfile = dataSnapshot.getValue(UserGeneralities.class);
                if (userProfile !=null){

                    String summoner_name = userProfile.getSummoner_name();
                    edittext_summoner_name.setText(summoner_name);

                    String server_name = userProfile.getSummoner_server();

                    for (int i=0; i<servers.size(); i++){
                        Log.d(TAG, "onDataChange: server_name:"+server_name);
                        Log.d(TAG, "onDataChange: servers.get(i)"+servers.get(i));
                        if (servers.get(i).equals(server_name)){
                            spinner_choose_server.setSelection(i, true);
                            break;
                        }
                    }




                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        settings_progressbar.setVisibility(View.INVISIBLE);


    }


}