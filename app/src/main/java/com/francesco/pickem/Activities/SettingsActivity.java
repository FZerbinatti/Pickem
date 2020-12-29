package com.francesco.pickem.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.R;
import com.francesco.pickem.SQLite.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switch_notification, switch_notificationx;
    ConstraintLayout show_notifications_box, show_notifications_boxx;
    private String TAG ="NotificationActivity: ";
    Context context;
    ImageButton button_logout;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private DatabaseReference user_preferences_reference;

    public static String REGION_SELECTED = "REGION_SELECTED";

    private String userID;
    Button button_save_settings_account;
    RegionNotifications setnot_object;
    ProgressBar settings_progressbar;

    DatabaseHelper databaseHelper;

    EditText edittext_username;
    ListView settings_regions_listview;
    //CheckBox checkbox_at_match_start, five_mins_before, checkbox_morning_reminder, not_picked;
    CheckBox settings_checkbox_lec,settings_checkbox_lck,settings_checkbox_lpl,settings_checkbox_lcs,settings_checkbox_tcl,settings_checkbox_cblol;
    CheckBox settings_checkbox_opl,settings_checkbox_ljl,settings_checkbox_pcs,settings_checkbox_eum,settings_checkbox_lcsa,settings_checkbox_lla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switch_notification = (SwitchCompat)  findViewById(R.id.switch_notification);
        switch_notificationx = (SwitchCompat)  findViewById(R.id.switch_notificationx);
        show_notifications_box = findViewById(R.id.show_notifications_box);
        show_notifications_boxx = findViewById(R.id.show_notifications_boxx);
        button_logout = findViewById(R.id.button_logout);
        edittext_username = findViewById(R.id.edittext_username);
        button_save_settings_account = findViewById(R.id.button_save_settings_account);
        databaseHelper = new DatabaseHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();
        settings_progressbar = findViewById(R.id.settings_progressbar);
        settings_regions_listview = findViewById(R.id.settings_regions_listview);

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
        loadListview();

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();
        Log.d(TAG, "onCreate: userID: "+userID);

        settings_progressbar.setVisibility(View.VISIBLE);

        loadSqliteData();

/*        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                User userProfile = dataSnapshot.getValue(User.class);
                if (userProfile !=null){
                    String database_username = userProfile.username;
                    edittext_username.setText(database_username);
                    ArrayList <String> choosenRegions = new ArrayList<>();
                    choosenRegions = userProfile.choosen_regions;
                    if (choosenRegions.contains(getString(R.string.lec))){settings_checkbox_lec.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.lcs))){settings_checkbox_lcs.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.lpl))){settings_checkbox_lpl.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.lck))){settings_checkbox_lck.setChecked(true);}

                    if (choosenRegions.contains(getString(R.string.tcl))){settings_checkbox_tcl.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.cblol))){settings_checkbox_cblol.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.opl))){settings_checkbox_opl.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.ljl))){settings_checkbox_ljl.setChecked(true);}

                    if (choosenRegions.contains(getString(R.string.pcs))){settings_checkbox_pcs.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.eum))){settings_checkbox_eum.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.lcs_academy))){settings_checkbox_lcsa.setChecked(true);}
                    if (choosenRegions.contains(getString(R.string.lla))){settings_checkbox_lla.setChecked(true);}

                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });*/

    }

    private void loadListview() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                getAllRegions() );

        settings_regions_listview.setAdapter(arrayAdapter);

        settings_regions_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stringRegionSelected = getAllRegions().get(position);
                Intent intent = new Intent(SettingsActivity.this, NotificationRegionActivity.class);
                intent.putExtra(REGION_SELECTED, stringRegionSelected);
                startActivity(intent);

            }
        });

    }

    private void loadSqliteData() {

        ArrayList <String> choosenRegions = new ArrayList<>();
        UserGeneralities sgo = databaseHelper.getgeneralSettings();
        choosenRegions = sgo.getChoosen_regions();
        Log.d(TAG, "loadSqliteData: choosenRegions.size: "+choosenRegions.size());

        if (choosenRegions.contains(getString(R.string.lec))){settings_checkbox_lec.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.lcs))){settings_checkbox_lcs.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.lpl))){settings_checkbox_lpl.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.lck))){settings_checkbox_lck.setChecked(true);}

        if (choosenRegions.contains(getString(R.string.tcl))){settings_checkbox_tcl.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.cblol))){settings_checkbox_cblol.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.opl))){settings_checkbox_opl.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.ljl))){settings_checkbox_ljl.setChecked(true);}

        if (choosenRegions.contains(getString(R.string.pcs))){settings_checkbox_pcs.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.eum))){settings_checkbox_eum.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.lcs_academy))){settings_checkbox_lcsa.setChecked(true);}
        if (choosenRegions.contains(getString(R.string.lla))){settings_checkbox_lla.setChecked(true);}

        String username = sgo.getUsername();
        edittext_username.setText(username);

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
                if(settings_checkbox_eum.isChecked())  {choosen_regions .add(getString(R.string.eum)) ;}
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


                UserGeneralities userGeneralities = new UserGeneralities("", edittext_username.getText().toString(), choosen_regions);

                databaseHelper.updateGeneralSettings(userGeneralities);
                updateFirebaseGeneralSettings(userGeneralities);


                show_notifications_box.setVisibility(View.GONE);
                settings_progressbar.setVisibility(View.INVISIBLE);




            }
        });

    }

    private void updateFirebaseGeneralSettings(UserGeneralities setnot_object) {

/*        reference = FirebaseDatabase.getInstance().getReference();
        user_preferences_reference = reference.child(getString(R.string.firebase_users_generealities));
        user_preferences_reference.push().set*/


    }


    private void buttonActions() {

        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (switch_notification.isChecked()){
                    Log.d(TAG, "onCreate: ON");
                    show_notifications_box.setVisibility(View.VISIBLE);
                }else {
                    Log.d(TAG, "onCreate: OFF");
                    show_notifications_box.setVisibility(View.GONE);
                }
            }
        });

        switch_notificationx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (switch_notificationx.isChecked()){
                    Log.d(TAG, "onCreate: ON");
                    show_notifications_boxx.setVisibility(View.VISIBLE);
                }else {
                    Log.d(TAG, "onCreate: OFF");
                    show_notifications_boxx.setVisibility(View.GONE);
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
                        Intent intentCalendar= new Intent(context, Calendar.class);
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

    public ArrayList <String> getAllRegions(){

        ArrayList<String> regions = new ArrayList<>();

        regions.add(getString(R.string.lec));
        regions.add(getString(R.string.lcs));
        regions.add(getString(R.string.lpl));
        regions.add(getString(R.string.lck));
        regions.add(getString(R.string.tcl));
        regions.add(getString(R.string.cblol));
        regions.add(getString(R.string.opl));
        regions.add(getString(R.string.ljl));
        regions.add(getString(R.string.pcs));
        regions.add(getString(R.string.eum));
        regions.add(getString(R.string.lcs_academy));
        regions.add(getString(R.string.lla));

        return  regions;
    }


}