package com.francesco.pickem.Activities.MainActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Activities.AccountActivities.RegisterActivity;
import com.francesco.pickem.Activities.SettingsActivities.InfoActivity;
import com.francesco.pickem.Activities.AccountActivities.LoginActivity;
import com.francesco.pickem.Activities.SettingsActivities.NotificationRegionActivity;
import com.francesco.pickem.Activities.Statistics.StatsPicksActivity;
import com.francesco.pickem.Adapters.SimpleRegionRecyclerViewAdapter;
import com.francesco.pickem.Adapters.SpinnerAdapter;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.BuildConfig;
import com.francesco.pickem.Models.CurrentRegion;
import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.Models.RegionServers;
import com.francesco.pickem.Models.SimpleRegion;
import com.francesco.pickem.Models.Sqlite_Match;
import com.francesco.pickem.Models.Sqlite_MatchDay;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.NotificationsService.AlarmReceiver;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.DatabaseHelper;
import com.francesco.pickem.Services.JsonPlaceHolderAPI_Elo;
import com.francesco.pickem.Services.JsonPlaceHolderAPI_Summoner;
import com.francesco.pickem.Services.Post_Elo;
import com.francesco.pickem.Services.Post_Summoner;
import com.francesco.pickem.Services.PreferencesData;
import com.francesco.pickem.Services.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switch_notification, switch_user_settings;
    ConstraintLayout show_notifications_box, show_user_box;
    private String TAG ="SettingsActivity: ";
    private static final String NOTIFICATION_ID = "1";
    Context context;
    ImageButton button_logout, button_delete_account;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private DatabaseReference user_preferences_reference;

    RecyclerView settings_recycler_regioni;
    ListView settings_regions_listview;
    ScrollView parentScrollListener;
    EditText edittext_summoner_name, edittext_username;
    Spinner spinner_choose_server;
    Button button_save_elotracker_info;
    SwitchCompat switch_elotracker, switch_automatic_elotracker;
    ConstraintLayout show_elotracker_box;
    ArrayList <String> finalRegionChoosen;
    public static String REGION_SELECTED = "REGION_SELECTED";
    private String userID;
    Button button_save_settings_account;
    RegionNotifications setnot_object;
    ProgressBar settings_progressbar;
    EditText edittext_emailaddress;
    ArrayList <String> userChoosenRegionsfromDB;
    ArrayList<RegionServers> servers ;
    ArrayList<SimpleRegion> user_choosen_regions;
    ArrayList<SimpleRegion> full_list_withUserChoiche;
    ArrayList<String> finalRegions;
    SimpleRegionRecyclerViewAdapter adapter;
    ArrayList<String> removed_regionss;
    ArrayList<String> old_choosen_regions;
    ArrayList<String> added_regionss;
    DatabaseHelper databaseHelper;
    Calendar myCalendar;
    String year;
    private SpinnerAdapter spinnerAdapter;
    TextView hour_elo_update, elotracker_timer_text;
    Integer hour, minute;

    ImageView info, info_button_elotracker;

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
        info_button_elotracker = findViewById(R.id.info_button_elotracker);
        switch_automatic_elotracker = findViewById(R.id.switch_automatic_elotracker);
        hour_elo_update = findViewById(R.id.hour_elo_update);
        elotracker_timer_text = findViewById(R.id.elotracker_timer_text);
        button_delete_account = findViewById(R.id.button_delete_account);
        edittext_username = findViewById(R.id.edittext_username);







        userChoosenRegionsfromDB = new ArrayList<>();

        edittext_emailaddress.setFocusable(false);
        edittext_username.setFocusable(false);
        edittext_emailaddress.setTextColor(Color.GRAY);

        edittext_summoner_name = findViewById(R.id.edittext_summoner_name);
        spinner_choose_server= findViewById(R.id.spinner_choose_server);
        button_save_elotracker_info = findViewById(R.id.button_save_elotracker_info);
        switch_elotracker = findViewById(R.id.switch_elotracker);
        show_elotracker_box = findViewById(R.id.show_elotracker_box);
        ArrayList<String> servers = new ArrayList<>();

        clickListeners();





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

    private void clickListeners() {

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

        info_button_elotracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Automatic Elotracker");
                builder.setMessage(R.string.elotracker_info);

                // add a button
                builder.setPositiveButton("Ok bro", null);

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        hour_elo_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                Calendar mcurrentTime = Calendar.getInstance();
                hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                hour_elo_update.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        switch_automatic_elotracker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    elotracker_timer_text.setVisibility(View.VISIBLE);
                    hour_elo_update.setVisibility(View.VISIBLE);

                    if (edittext_summoner_name.getText().toString().isEmpty() || edittext_summoner_name.getText().toString().equals("null")){
                        Toast.makeText(context, "Please Insert your Summoner Name", Toast.LENGTH_SHORT).show();
                        switch_automatic_elotracker.setChecked(false);
                    }

                }else {
                    elotracker_timer_text.setVisibility(View.GONE);
                    hour_elo_update.setVisibility(View.GONE);
                }
            }
        });

        button_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                alertDialog.setTitle("Delete Account");
                alertDialog.setMessage("Are you sure you want to delete your account? This action is permanent.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String currentUSer = FirebaseAuth.getInstance().getCurrentUser().getUid();



                                Intent intent1 = new Intent(SettingsActivity.this, RegisterActivity.class);
                                //PreferencesData.setUserLoggedInStatus(getApplicationContext(),false);
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                                .child(currentUSer)
                                                .removeValue();

                                        Toast.makeText(context, "Account Deleted.", Toast.LENGTH_SHORT).show();
                                        startActivity(intent1);
                                    }
                                });


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

/*    private void updateChoosenRegion(String stringRegionSelected, Boolean booleanRegionSelected){


        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getResources().getString(R.string.firebase_users_generealities))
                .child(getResources().getString(R.string.firebase_user_choosen_regions))
                .setValue(stringRegionSelected);
    }*/

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
                            edittext_username.setText(userProfile.getUsername());

                            userChoosenRegionsfromDB = userProfile.getChoosen_regions();
                            old_choosen_regions.addAll(userChoosenRegionsfromDB);
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
                ArrayList <String> added_regions = new ArrayList<>();
                added_regions.addAll(finalRegions);

                ArrayList <String> removed_regions = new ArrayList<>();
                removed_regions.addAll(old_choosen_regions);

                if (finalRegions.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "You must chose at least one Region to follow", Toast.LENGTH_SHORT).show();
                    settings_progressbar.setVisibility(View.INVISIBLE);
                }else {

                    UserGeneralities userGeneralities = new UserGeneralities( edittext_emailaddress.getText().toString(),"", edittext_username.getText().toString(), "", -1,finalRegions, "", "");
                    updateUserGeneralities(userGeneralities);
                    databaseHelper = new DatabaseHelper(context);



                    // LE REGIONI CHE CI SONO IN old_choosen_regions E NON IN FINAL_REGIONS SONO LE REGIONI ELIMINATE
                    //removed_regions = old_choosen_regions.REMOVE(FINAL_REGIONS)

                    removed_regions.removeAll(finalRegions);

                    removeNotificationNoChoosenMatchForThisRegion(removed_regions);

                    for(int i = 0; i< removed_regions.size(); i++){
                        Log.d(TAG, "REMOVING SQL MATCHES FOR THIS REGION: "+ removed_regions.get(i));
                        databaseHelper.removeFromDatabase(removed_regions.get(i));
                    }


                    //LE REGIONI CHE CI SONO IN FINAL_REGIONS E NON IN old_choosen_regions SONO LE REGIONI NUOVE AGGIUNTE
                    //added_regions = FINAL_REGIONS.REMOVE(old_choosen_regions)

                    added_regions.removeAll(old_choosen_regions);

                    addNotificationNoChoosenMatchForThisRegion(added_regions);

                    for(int i = 0; i< added_regions.size(); i++){
                        Log.d(TAG, "ADDING SQL MATCHES FOR THIS REGION: "+ added_regions.get(i));
                        downloadMatchDays(added_regions.get(i));
                    }

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
            Log.d(TAG, "removeNotificationNoChoosenMatchForThisRegion: REMOVING NOTIFICATION FOR: "+old_choosen_regions.get(i));

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
            Log.d(TAG, "addNotificationNoChoosenMatchForThisRegion: ADDING NOTIFICATION FOR: "+new_choosen_regions.get(i));

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
                                finish();
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
                    RegionServers regionServer = snapshot.getValue(RegionServers.class);

                    servers.add(regionServer);
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

    private void loadSpinner(ArrayList<RegionServers> allServers) {
        Log.d(TAG, "loadSpinner: "+allServers.size());
        spinnerAdapter = new SpinnerAdapter(SettingsActivity.this,
                android.R.layout.simple_spinner_item,
                allServers);

        spinner_choose_server.setAdapter(spinnerAdapter);
        spinner_choose_server.setSelection(2);

    }

    private void saveEloTracker(){

        button_save_elotracker_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");

        String summonerName = edittext_summoner_name.getText().toString();
        RegionServers summoner_server = (RegionServers) spinner_choose_server.getSelectedItem();

                Log.d(TAG, "onClick: summonerName: "+summonerName);
                Log.d(TAG, "onClick: summoner_server"+summoner_server.getCode());

        if (summonerName.isEmpty()){edittext_summoner_name.setError("Please Insert your Summoner Name");}else
            if (summoner_server.getCode().isEmpty()){
                Toast.makeText(context, "Select Your Region Server!", Toast.LENGTH_SHORT).show();
                }else {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.firebase_users_generealities));

                            reference.child(getString(R.string.summoner_name)).setValue(summonerName);
                            reference.child(getString(R.string.summoner_server)).setValue(summoner_server.getId().toLowerCase());
                            if (switch_automatic_elotracker.isChecked()){
                                reference.child(getString(R.string.summoner_elotracker)).setValue(1);
                                saveFirstElotracker(summonerName, summoner_server);
                            }else {
                                reference.child(getString(R.string.summoner_elotracker)).setValue(0);
                            }
                            reference.child("time_elotracker").setValue(hour_elo_update.getText().toString());

                Log.d(TAG, "onClick: DONE ");

                show_elotracker_box.setVisibility(View.GONE);
                switch_elotracker.setChecked(false);


                }
            }
        });
    }

    private void saveFirstElotracker(String summonerName, RegionServers summoner_server) {


        // get summoner ID from summoner name + server
        // https://     euw1    .api.riotgames.com      /lol/summoner/v4/summoners/by-name/         DEMACIA%20REICH         ?api_key=       RGAPI-632893d3-8938-4031-a32e-4aa92062d229

        String address = getString(R.string.HTTP) + summoner_server.getId().toLowerCase() +getString(R.string.riot_api_address);
        /// https://euw1.api.riotgames.com

        //summoner name + api_key_path + API key
        String end_path = summonerName + getString(R.string.key_request)+ BuildConfig.RIOT_API_KEY;


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
        Call<Post_Summoner> callSummoner =  jsonPlaceHolderAPI_summoner.getPost(summonerName, BuildConfig.RIOT_API_KEY) ;

        callSummoner.enqueue(new Callback<Post_Summoner>() {
            @Override
            public void onResponse(Call<Post_Summoner> call, Response<Post_Summoner> response) {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: "+response.code());
                    Toast.makeText(context, "Something went wrong, check Summoner Name and Region selected!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(context, "Success! Current Elo registered, will update every 24 hours!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse: "+ response.body().getId() +" summonerLevel:" + response.body().getSummonerLevel() + " summoner Name: "+ response.body().getName());
                //ora che hai lo il summonerID puoi fare l'altra call all'API

                JsonPlaceHolderAPI_Elo jsonPlaceHolderAPIElo = retrofit.create(JsonPlaceHolderAPI_Elo.class);

                Call<List<Post_Elo>> callElo = jsonPlaceHolderAPIElo.getPost(response.body().getId(), BuildConfig.RIOT_API_KEY );
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
                                        getYesterdayDate(),
                                        getYesterdayDate(),
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
                                        .child(getYesterdayDate() )
                                        .setValue(eloTracker).addOnCompleteListener(task2 -> {

                                    if (task2.isSuccessful()){
                                        Log.d(TAG, "Success elo added");
                                        // aggiungi JobScheduler che checka ogni 24h se Elotracher è 1
                                        // prendi l'ora a cui il player vuola che la rank sia aggiornata

                                       String[] datetime = hour_elo_update.getText().toString().split(":");

                                       String ora ="";
                                       String minuto = "";

                                       if (datetime.length ==2){
                                           ora = datetime[0];
                                           minuto = datetime[1];
                                       }


                                        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                        Intent intent0 = new Intent(SettingsActivity.this, AlarmReceiver.class);
                                        intent0.putExtra("TYPE", "CHECK_ELO");
                                        intent0.putExtra("HOUR", ora);
                                        intent0.putExtra("MINUTE", minuto);
                                        PendingIntent pendingIntent0 = PendingIntent.getBroadcast(SettingsActivity.this, 1, intent0, 0);
                                        Calendar task7AM = Calendar.getInstance();


                                        task7AM.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ora));
                                        task7AM.set(Calendar.MINUTE, Integer.parseInt(minuto));

                                        //COMMENTED FOR TESTING PURPOSE
                                        task7AM.add(Calendar.DAY_OF_MONTH,0);

                                        task7AM.setTimeZone(TimeZone.getDefault());
                                        Log.d(TAG, "SettingsActivity: controllo dell'Elo a quest'ora: "+task7AM.getTimeInMillis());

                                        alarmManager.set(AlarmManager.RTC_WAKEUP, task7AM.getTimeInMillis(), pendingIntent0);




                                    }else{
                                        Log.d(TAG, "ERROR");
                                    }
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
                    String timer_elotracker = userProfile.getTime_elotracker();
                    if (!timer_elotracker.isEmpty()){
                        hour_elo_update.setText(timer_elotracker);
                    }else {
                        hour_elo_update.setText("22:00");
                    }

                    if (userProfile.getSummoner_elotracker()>0){
                        switch_automatic_elotracker.setChecked(true);
                    }else {
                        switch_automatic_elotracker.setChecked(false);
                    }

                    String server_name = userProfile.getSummoner_server();

                    for (int i=0; i<servers.size(); i++){
                       // Log.d(TAG, "onDataChange: server_name:"+server_name);
                       // Log.d(TAG, "onDataChange: servers.get(i)"+servers.get(i).getCode());
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

    private String getLocalDateFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTime: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);
        Log.d(TAG, "getLocalDateFromDateTime: localDatetime: "+localDatetime);

        return localDatetime;
    }

    public void downloadMatchDays(String selected_region){
        String firebase_section = getString(R.string.firebase_Matches);
        CurrentRegion currentRegion = new CurrentRegion();
        currentRegion.setRegion(selected_region);
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(firebase_section)
                .child(selected_region)
                .child(selected_region + year);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                String current_date = "";
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // prendi tutti i giorni disponibili per quell'anno per quella regione
                    String match_id = (snapshot.getKey());
                    String date =getLocalDateFromDateTime(match_id);
                    // pusha i match nell'SQL locale tabella Matches
                    databaseHelper.insertMatch( new Sqlite_Match(year,currentRegion.getRegion(), date, match_id));
                    //filtra tutti i match e ottieni solo i matchdays univoci

                    if (!date.equals(current_date)){
                        databaseHelper.insertMatchDay(new Sqlite_MatchDay(year, currentRegion.getRegion(), date));
                        current_date=date;
                    }

                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    public String getYesterdayDate(){

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(c.getTime());
        return selectedDate;
    }

    public Integer romanNumberToArabian(String romabValue){

        switch(romabValue) {
            case "I":
                return  1;
            case "II":
                return  2;
            case "III":
                return  3;
            case "IV":
                return  4;
            default:
                return 0;
        }

    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_pickem);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}