package com.francesco.pickem.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationRegionActivity extends AppCompatActivity {

    ListView listview_teams;
    TextView region_name;
    ArrayList<String> teams_list;
    private String TEAM_SELECTED ="TEAM_SELECTED";
    public  String REGION_SELECTED= "REGION_SELECTED" ;
    public static String TAG = "NotificationRegionActivity: ";
    ProgressBar notification_regions_progressbar;
    Button save_button;
    CheckBox checkbox_at_match_start, checkbox_morning_reminder, checkbox_not_picked;
    String regionSelectedExtra;
    ImageView imageview_notification_region;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_region);
        teams_list = new ArrayList<>();

        listview_teams = findViewById(R.id.notification_region_listview_teams);
        region_name = findViewById(R.id.notification_region_name);
        notification_regions_progressbar = findViewById(R.id.notification_regions_progressbar);
        save_button = findViewById(R.id.button_save_notification_settings);
        checkbox_not_picked = findViewById(R.id.checkbox_not_picked);
        checkbox_morning_reminder = findViewById(R.id.checkbox_morning_reminder);
        checkbox_at_match_start = findViewById(R.id.checkbox_at_match_start);
        imageview_notification_region = findViewById(R.id.imageview_notification_region);

        context = this;

        Bundle extras = getIntent().getExtras();
        regionSelectedExtra = extras.getString(REGION_SELECTED);
        Log.d(TAG, "onCreate: REGION_SELECTED: " +regionSelectedExtra);

        region_name.setText(regionSelectedExtra);
        notification_regions_progressbar.setVisibility(View.VISIBLE);
        getAllTeams(regionSelectedExtra);

        loadBackground(regionSelectedExtra);
        loadSettingsForThisRegion(regionSelectedExtra);
        saveButton();




    }

    private void loadBackground(String regionSelected) {

        DatabaseReference regionImageReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_regions))
                .child(regionSelected)
                .child(getString(R.string.regions_image));

        regionImageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url_image = dataSnapshot.getValue(String.class);

                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .error(R.drawable.ic_load);
                Glide.with(context).load(url_image).apply(options).transition(DrawableTransitionOptions.withCrossFade(500)).into(imageview_notification_region);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void loadSettingsForThisRegion(String regionSelected ) {

        DatabaseReference notificationRegionReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_notification))
                .child(getString(R.string.firebase_user_notification_region))
                .child(regionSelected);

        Log.d(TAG, "loadSettingsForThisRegion: regionSelected: "+regionSelected);

        notificationRegionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                RegionNotifications regionNotifications = dataSnapshot.getValue(RegionNotifications.class);
                if (regionNotifications !=null){
                    Integer ncm = regionNotifications.getNo_choice_made();
                    Integer fmotd = regionNotifications.getNotification_first_match_otd();
                    Integer mr = regionNotifications.getNotification_morning_reminder();

                    checkbox_not_picked.setChecked(ncm > 0);
                    checkbox_at_match_start.setChecked(fmotd > 0);
                    checkbox_morning_reminder.setChecked(mr > 0);


                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        notification_regions_progressbar.setVisibility(View.INVISIBLE);

    }

    private void saveButton() {

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*                // se nessuno dei 3 checkbox è attivo elimina la regione dalle notifiche
                if (!checkbox_at_match_start.isChecked() && !checkbox_morning_reminder.isChecked() && !checkbox_not_picked.isChecked() ){
                    FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.firebase_user_notification))
                            .child(getString(R.string.firebase_user_notification_region))
                            .child(regionSelectedExtra)
                            .removeValue();
                }else */

                DatabaseReference notificationRegionReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.firebase_user_notification))
                        .child(getString(R.string.firebase_user_notification_region))
                        .child(regionSelectedExtra);

                    // per ogni checkbox  setta a 0 o 1
                if (checkbox_not_picked.isChecked()){
                    notificationRegionReference.child(getString(R.string.notification_no_choice_made)).setValue(1);
                }else {
                    notificationRegionReference.child(getString(R.string.notification_no_choice_made)).setValue(0);
                }
                if (checkbox_morning_reminder.isChecked()){
                    notificationRegionReference.child(getString(R.string.notification_morning_reminder)).setValue(1);
                }else {
                    notificationRegionReference.child(getString(R.string.notification_morning_reminder)).setValue(0);
                }
                if (checkbox_at_match_start.isChecked()){
                    notificationRegionReference.child(getString(R.string.notification_first_match_otd)).setValue(1);
                }else {
                    notificationRegionReference.child(getString(R.string.notification_first_match_otd)).setValue(0);
                }

                Toast.makeText(NotificationRegionActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NotificationRegionActivity.this, SettingsActivity.class);
                startActivity( intent);

/*                ArrayList <String> newRegions = new ArrayList<>();
                ArrayList <String> oldRegions = new ArrayList<>();
                ArrayList <String> unchangedRegions = new ArrayList<>();

                newRegions = userGeneralities.getChoosen_regions();
                oldRegions = choosenRegionsfromDB;

                Log.d(TAG, "updateFirebaseRegionNotifications: newRegions.seize():"+newRegions.size());
                Log.d(TAG, "updateFirebaseRegionNotifications: oldRegions.seize():"+oldRegions.size());


                // lista delle regioni non modificate
                for  (int i=0; i<allRegions.size(); i++){
                    if (newRegions.contains(allRegions.get(i)) && oldRegions.contains(allRegions.get(i))){
                        unchangedRegions.add(allRegions.get(i));
                    }
                }

                //lista delle nuove regioni da aggiungere
                for  (int i=0; i<unchangedRegions.size(); i++){
                    if (newRegions.contains(unchangedRegions.get(i))){
                        newRegions.remove(i);
                    }
                }

                //lista delle regioni da eliminare
                for  (int i=0; i<unchangedRegions.size(); i++){
                    if (oldRegions.contains(unchangedRegions.get(i))){
                        oldRegions.remove(i);
                    }
                }



                //regions aggiunte : aggiungi regione e inizializzala
                for (int i=0; i<newRegions.size(); i++){

                    RegionNotifications regionNotifications = new RegionNotifications(newRegions.get(i),0,0,1);
                    FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.firebase_user_notification))
                            .child(getString(R.string.firebase_user_notification_region))
                            .child(newRegions.get(i))
                            .setValue(regionNotifications).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()){
                            Log.d(TAG, "ADDED: Notification Region ");
                        }else{
                            Log.d(TAG, "ERROR: registered: Notification Region ");
                        }
                    });
                }

                //regioni eliminate: eliminale dal db
                for (int i=0; i<oldRegions.size(); i++){

                    RegionNotifications regionNotifications = new RegionNotifications(oldRegions.get(i),0,0,1);
                    FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.firebase_user_notification))
                            .child(getString(R.string.firebase_user_notification_region))
                            .child(oldRegions.get(i))
                            .removeValue();
                }*/

            }
        });

    }

    private void loadListview(ArrayList<String> allTeamsForThiRegion) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                allTeamsForThiRegion);

        listview_teams.setAdapter(arrayAdapter);

        listview_teams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stringTeamSelected = allTeamsForThiRegion.get(position);
                Intent intent = new Intent(NotificationRegionActivity.this, NotificationTeamActivity.class);
                intent.putExtra(TEAM_SELECTED, stringTeamSelected);
                startActivity(intent);

            }
        });

    }

    public void  getAllTeams(String regionSelected){
        Log.d(TAG, "getAllRegions: ");

        ArrayList<String> teams = new ArrayList<>();

        // load da firebase le regioni
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_teams))
                .child(regionSelected);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String teamName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+ teamName);
                    teams.add(teamName);


                }
                loadListview(teams);
                notification_regions_progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "getAllRegions: regions.size(): "+ teams.size());



    }



}