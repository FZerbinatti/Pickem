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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.Models.User;
import com.francesco.pickem.R;
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

    SwitchCompat switch_notification, switch_notificationx;
    ConstraintLayout show_notifications_box, show_notifications_boxx;
    private String TAG ="NotificationActivity: ";
    Context context;
    ImageButton button_logout;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    EditText edittext_username;
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
        userID = user.getUid();


        Log.d(TAG, "onCreate: userID: "+userID);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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
        });

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
}