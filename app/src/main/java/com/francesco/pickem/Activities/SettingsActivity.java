package com.francesco.pickem.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.PreferencesData;
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

    EditText edittext_summoner_name;
    Spinner spinner_choose_server;
    Button button_save_elotracker_info;
    SwitchCompat switch_elotracker;
    ConstraintLayout show_elotracker_box;


    public static String REGION_SELECTED = "REGION_SELECTED";

    private String userID;
    Button button_save_settings_account;
    RegionNotifications setnot_object;
    ProgressBar settings_progressbar;

    EditText edittext_emailaddress;
    ListView settings_regions_listview;
    //CheckBox checkbox_at_match_start, five_mins_before, checkbox_morning_reminder, not_picked;
    CheckBox settings_checkbox_lec,settings_checkbox_lck,settings_checkbox_lpl,settings_checkbox_lcs,settings_checkbox_tcl,settings_checkbox_cblol;
    CheckBox settings_checkbox_opl,settings_checkbox_ljl,settings_checkbox_pcs,settings_checkbox_eum,settings_checkbox_lcsa,settings_checkbox_lla;

    ArrayList <String> choosenRegionsfromDB;
    ArrayList<String> servers ;

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
        choosenRegionsfromDB = new ArrayList<>();

        edittext_emailaddress.setFocusable(false);
        edittext_emailaddress.setTextColor(Color.GRAY);

        edittext_summoner_name = findViewById(R.id.edittext_summoner_name);
        spinner_choose_server= findViewById(R.id.spinner_choose_server);
        button_save_elotracker_info = findViewById(R.id.button_save_elotracker_info);
        switch_elotracker = findViewById(R.id.switch_elotracker);
        show_elotracker_box = findViewById(R.id.show_elotracker_box);
        ArrayList<String> servers = new ArrayList<>();

        settings_checkbox_lec = findViewById(R.id.checkbox_lec);
        settings_checkbox_lcs = findViewById(R.id.checkbox_lcs);
        settings_checkbox_lck = findViewById(R.id.checkbox_lck);
        settings_checkbox_lpl = findViewById(R.id.checkbox_lpl);

        settings_checkbox_tcl = findViewById(R.id.checkbox_tcl);
        settings_checkbox_cblol = findViewById(R.id.checkbox_cblol);
        settings_checkbox_opl = findViewById(R.id.checkbox_opl);
        settings_checkbox_ljl = findViewById(R.id.checkbox_ljl);

        settings_checkbox_pcs = findViewById(R.id.checkbox_pcs);
        settings_checkbox_eum = findViewById(R.id.checkbox_eum);
        settings_checkbox_lcsa = findViewById(R.id.checkbox_lcsa);
        settings_checkbox_lla = findViewById(R.id.checkbox_lla);


        context = this;

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

    private void loadFirebaseData() {


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();


        Log.d(TAG, "onCreate: userID: "+userID);
        reference.child(userID).child(getString(R.string.firebase_users_generealities)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                UserGeneralities userProfile = dataSnapshot.getValue(UserGeneralities.class);
                if (userProfile !=null){

                    String database_email = userProfile.getEmail();

                    edittext_emailaddress.setText(database_email);

                    choosenRegionsfromDB = userProfile.getChoosen_regions();
                    if (choosenRegionsfromDB.contains(getString(R.string.lec))){settings_checkbox_lec.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.lcs))){settings_checkbox_lcs.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.lpl))){settings_checkbox_lpl.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.lck))){settings_checkbox_lck.setChecked(true);}

                    if (choosenRegionsfromDB.contains(getString(R.string.tcl))){settings_checkbox_tcl.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.cblol))){settings_checkbox_cblol.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.opl))){settings_checkbox_opl.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.lla))){settings_checkbox_lla.setChecked(true);}

                    if (choosenRegionsfromDB.contains(getString(R.string.pcs))){settings_checkbox_pcs.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.ljl))){settings_checkbox_ljl.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.lcs_academy))){settings_checkbox_lcsa.setChecked(true);}
                    if (choosenRegionsfromDB.contains(getString(R.string.eu_masters))){settings_checkbox_eum.setChecked(true);}


                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        settings_progressbar.setVisibility(View.INVISIBLE);


    }

    private void saveGeneralSettings() {

        button_save_settings_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settings_progressbar.setVisibility(View.VISIBLE);

               ArrayList <String> choosen_regions = new ArrayList<>();

/*                Integer int_checkbox_at_match_start = 0;
                Integer int_notification_5_minutes=0;
                Integer int_switch_allgames_firstgame=0;
                Integer int_notification_morning_reminder=0;
                Integer int_no_choice_made=0;*/

                if(settings_checkbox_lec.isChecked())  {choosen_regions .add(getString(R.string.lec)) ;}
                if(settings_checkbox_lcs.isChecked())  {choosen_regions .add(getString(R.string.lcs)) ;}
                if(settings_checkbox_lck.isChecked())  {choosen_regions .add(getString(R.string.lck)) ;}
                if(settings_checkbox_lpl.isChecked())  {choosen_regions .add(getString(R.string.lpl)) ;}
                if(settings_checkbox_tcl.isChecked())  {choosen_regions .add(getString(R.string.tcl)) ;}
                if(settings_checkbox_cblol.isChecked()){choosen_regions .add(getString(R.string.cblol) );}
                if(settings_checkbox_opl.isChecked())  {choosen_regions .add(getString(R.string.opl)) ;}
                if(settings_checkbox_ljl.isChecked())  {choosen_regions .add(getString(R.string.ljl)) ;}
                if(settings_checkbox_pcs.isChecked())  {choosen_regions .add(getString(R.string.pcs)) ;}
                if(settings_checkbox_eum.isChecked())  {choosen_regions .add(getString(R.string.eu_masters)) ;}
                if(settings_checkbox_lcsa.isChecked()) {choosen_regions .add(getString(R.string.lcs_academy) );}
                if(settings_checkbox_lla.isChecked())  {choosen_regions .add(getString(R.string.lla)); }

/*                      if( checkbox_at_match_start.isChecked()){int_checkbox_at_match_start  =1;}else{int_checkbox_at_match_start  =0;}
                        if( five_mins_before.isChecked()){int_notification_5_minutes  =1;}else{int_notification_5_minutes  =0;}
                        if (switch_allgames_firstgame.isActivated()){int_switch_allgames_firstgame=1;}else {int_switch_allgames_firstgame=0;}
                        if( checkbox_morning_reminder.isChecked()){int_notification_morning_reminder =1;}else{int_notification_morning_reminder =0;}
                        if( not_picked.isChecked()){int_no_choice_made  =1;}else{int_no_choice_made  =0;}

                        setnot_object= new Settings_notification_object(

                                choosen_regions,
                                int_checkbox_at_match_start,
                                int_notification_5_minutes,
                                int_switch_allgames_firstgame,
                                int_notification_morning_reminder,
                                int_no_choice_made
                        );*/

                Log.d(TAG, "onClick: passing this size: "+choosen_regions.size());


                UserGeneralities userGeneralities = new UserGeneralities( edittext_emailaddress.getText().toString(),"", "", choosen_regions, "");
                //updateFirebaseRegionNotifications(userGeneralities);
                //updateFirebaseRegionNotifications(userGeneralities);
                updateUserGeneralities(userGeneralities);
                show_user_box.setVisibility(View.GONE);
                switch_user_settings.setChecked(false);
                Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
                settings_progressbar.setVisibility(View.INVISIBLE);




            }
        });

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
                alertDialog.setMessage("Do you want to perform the Logout");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                PreferencesData.setUserLoggedInStatus(getApplicationContext(),false);
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
                        Intent intentNotif= new Intent(context, SettingsActivity.class);
                        startActivity(intentNotif);
                        Animatoo.animateFade(context);
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