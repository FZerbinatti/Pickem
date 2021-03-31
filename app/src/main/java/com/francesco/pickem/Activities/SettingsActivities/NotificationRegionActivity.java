package com.francesco.pickem.Activities.SettingsActivities;

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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Activities.MainActivities.SettingsActivity;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
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
    String imageRegionPath;
    DatabaseHelper databaseHelper;

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
        imageRegionPath = context.getFilesDir().getAbsolutePath() + (getString(R.string.folder_regions_images));
        Bundle extras = getIntent().getExtras();
        regionSelectedExtra = extras.getString(REGION_SELECTED);
        Log.d(TAG, "onCreate: REGION_SELECTED: " +regionSelectedExtra);
        databaseHelper = new DatabaseHelper(context);
        region_name.setText(regionSelectedExtra);
        notification_regions_progressbar.setVisibility(View.VISIBLE);
        getAllTeams(regionSelectedExtra);

        loadBackground(regionSelectedExtra);
        loadSettingsForThisRegion(regionSelectedExtra);
        saveButton();






    }

    private void loadBackground(String regionSelected) {

        //non serve fare la query se hai il region name e applichi i parametri di cambio lettere/spazi
        String local_image =imageRegionPath+regionSelected.replace(" ", "")+".png";

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_load);



        Glide.with(context)
                .load(new File(local_image)) // Uri of the picture
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(imageview_notification_region);
        Log.d(TAG, "loadBackground: IMMAGINE CARICATA DA LOCALE: "+imageRegionPath+local_image);




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

                DatabaseReference notificationRegionReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.firebase_user_notification))
                        .child(getString(R.string.firebase_user_notification_region))
                        .child(regionSelectedExtra);

                    // per ogni checkbox  setta a 0 o 1
                if (!checkbox_not_picked.isChecked() && !checkbox_morning_reminder.isChecked() && !checkbox_at_match_start.isChecked()){
                    //se sono tutti a 0 elimina la regione dal db
                    notificationRegionReference.removeValue();
                }else {
                    if (checkbox_not_picked.isChecked()){
                        notificationRegionReference.child(getString(R.string.notification_no_choice_made)).setValue(1);
                        databaseHelper.insert_REGIONS_NO_CHOICE_MADE(regionSelectedExtra);
                    }else {
                        notificationRegionReference.child(getString(R.string.notification_no_choice_made)).setValue(0);
                        databaseHelper.remove_REGIONS_NO_CHOICE_MADE(regionSelectedExtra);
                    }
                    if (checkbox_morning_reminder.isChecked()){
                        notificationRegionReference.child(getString(R.string.notification_morning_reminder)).setValue(1);
                        databaseHelper.insert_REGIONS_MORNING_REMINDER(regionSelectedExtra);
                    }else {
                        notificationRegionReference.child(getString(R.string.notification_morning_reminder)).setValue(0);
                        databaseHelper.remove_REGIONS_MORNING_REMINDER(regionSelectedExtra);
                    }
                    if (checkbox_at_match_start.isChecked()){
                        notificationRegionReference.child(getString(R.string.notification_first_match_otd)).setValue(1);
                        databaseHelper.insert_REGIONS_FIRST_MATCH_ODT(regionSelectedExtra);
                    }else {
                        notificationRegionReference.child(getString(R.string.notification_first_match_otd)).setValue(0);
                        databaseHelper.remove_REGIONS_FIRST_MATCH_ODT(regionSelectedExtra);
                    }

                }



                Toast.makeText(NotificationRegionActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(NotificationRegionActivity.this, SettingsActivity.class);
                startActivity( intent);*/

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
                Log.d(TAG, "onItemClick: stringTeamSelected: "+stringTeamSelected);
                Intent intent = new Intent(NotificationRegionActivity.this, NotificationTeamActivity.class);
                intent.putExtra(TEAM_SELECTED, stringTeamSelected);
                intent.putExtra(REGION_SELECTED, regionSelectedExtra);
                startActivity(intent);
                finish();

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