package com.francesco.pickem.Activities.SettingsActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Models.TeamDetails;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class NotificationTeamActivity extends AppCompatActivity {

    private String TEAM_SELECTED ="TEAM_SELECTED";
    public  String REGION_SELECTED= "REGION_SELECTED" ;
    private static String TAG = "Notification Teams: ";
    TextView team_name;
    TeamDetails teamDetails;
    Context context;
    ImageView background_team;
    ProgressBar notifications_teams_progressbar;
    Button save_button;
    String teamSelectedExtra ;
    String regionSelectedExtra;
    CheckBox checkbox_as_team_plays, checkbox_team_morning_reminder;
    String imageTeamPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_team);

        Bundle extras = getIntent().getExtras();
        teamSelectedExtra = extras.getString(TEAM_SELECTED);
        regionSelectedExtra = extras.getString(REGION_SELECTED);

        Log.d(TAG, "onCreate: TEAM_SELECTED: " +teamSelectedExtra);
        teamDetails = new TeamDetails();
        team_name = findViewById(R.id.notification_team_name);
        background_team = findViewById(R.id.imageview_notification_team);
        notifications_teams_progressbar = findViewById(R.id.notifications_teams_progressbar);
        save_button = findViewById(R.id.button_save_notification_settings_teams);
        checkbox_as_team_plays = findViewById(R.id.checkbox_as_team_plays);
        checkbox_team_morning_reminder = findViewById(R.id.checkbox_team_morning_reminder);
        context = this;
        imageTeamPath = context.getFilesDir().getAbsolutePath() + (getString(R.string.folder_teams_images));

        notifications_teams_progressbar.setVisibility(View.VISIBLE);
        team_name.setText(teamSelectedExtra);

        saveButton();
        setInfoTeamDetailsForRegionForTeam(regionSelectedExtra, teamSelectedExtra);
        loadSettingsForThisTeam(regionSelectedExtra, teamSelectedExtra);

    }

    public void setInfoTeamDetailsForRegionForTeam (String region , String team){

        String local_image =imageTeamPath +team+".png";


        RequestOptions options = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_load);

        Glide.with(context)
                .load(new File(local_image)) // Uri of the picture
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(background_team);



    }

    private void saveButton() {

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference notificationTeamReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.firebase_user_notification))
                        .child(getString(R.string.firebase_teams))
                        .child(regionSelectedExtra)
                        .child(teamSelectedExtra);


                // per ogni checkbox  setta a 0 o 1
                if (!checkbox_as_team_plays.isChecked() && !checkbox_team_morning_reminder.isChecked()){
                    //se sono tutti a 0 elimina la regione dal db
                    notificationTeamReference.removeValue();
                }else {

                    notificationTeamReference.child(getString(R.string.regions_region)).setValue(regionSelectedExtra);
                    notificationTeamReference.child(getString(R.string.team)).setValue(teamSelectedExtra);

                    if (checkbox_as_team_plays.isChecked()){
                        notificationTeamReference.child(getString(R.string.notification_team_as_team_plays)).setValue(1);
                    }else {
                        notificationTeamReference.child(getString(R.string.notification_team_as_team_plays)).setValue(0);
                    }
                    if (checkbox_team_morning_reminder.isChecked()){
                        notificationTeamReference.child(getString(R.string.notification_team_morning_reminder)).setValue(1);
                    }else {
                        notificationTeamReference.child(getString(R.string.notification_team_morning_reminder)).setValue(0);
                    }
                }



                Toast.makeText(NotificationTeamActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NotificationTeamActivity.this, NotificationRegionActivity.class);
                intent.putExtra(REGION_SELECTED, regionSelectedExtra);
                startActivity( intent);
                finish();


            }
        });

    }

    private void loadSettingsForThisTeam(String regionSelected, String teamSelected ) {


        DatabaseReference notificationTeamReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_notification))
                .child(getString(R.string.firebase_teams))
                .child(regionSelected)
                .child(teamSelected);


        Log.d(TAG, "loadSettingsForThisTeam: path: "+ getString(R.string.firebase_users) +"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()  +"/"+  getString(R.string.firebase_user_notification)    +"/"+   getString(R.string.firebase_teams)   +"/"+ regionSelected +"/"+ teamSelected);
        Log.d(TAG, "loadSettingsForThisRegion: regionSelected: "+regionSelected);

        notificationTeamReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                TeamNotification teamsNotifications = dataSnapshot.getValue(TeamNotification.class);
                    String team_name = dataSnapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: team_name: "+team_name);

                    if (teamsNotifications !=null ){
                        Log.d(TAG, "onDataChange: "+teamsNotifications.getRegion() +" " +team_name);
                        if (teamsNotifications.getRegion().equals(regionSelectedExtra) && team_name.equals(teamSelectedExtra)){
                            //Log.d(TAG, "onDataChange: "+regionNotifications.getRegion_name());
                            Integer atp = teamsNotifications.getNotification_team_as_team_plays();
                            Integer mr = teamsNotifications.getNotification_team_morning_reminder();

                            checkbox_as_team_plays.setChecked(atp > 0);
                            checkbox_team_morning_reminder.setChecked(mr > 0);
                        }

                    }






            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        notifications_teams_progressbar.setVisibility(View.INVISIBLE);

    }

}