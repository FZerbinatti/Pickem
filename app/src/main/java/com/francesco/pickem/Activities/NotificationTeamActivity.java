package com.francesco.pickem.Activities;

import androidx.annotation.NonNull;
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.TeamDetails;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        notifications_teams_progressbar.setVisibility(View.VISIBLE);

        saveButton();
        setInfoTeamDetailsForRegionForTeam(regionSelectedExtra, teamSelectedExtra);
        loadSettingsForThisTeam(regionSelectedExtra, teamSelectedExtra);


/*        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_teams))
                .child(regionSelectedExtra)
                .child(teamSelectedExtra);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeamDetails databaseTeamDetails = dataSnapshot.getValue(TeamDetails.class);
                if (databaseTeamDetails!= null){
                    NotificationTeamActivity.this.team_name.setText(databaseTeamDetails.getName());

                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .error(R.drawable.ic_load);
                    Glide.with(context).load(databaseTeamDetails.getImage()).apply(options).transition(DrawableTransitionOptions.withCrossFade(500)).into(background_team);

                    notifications_teams_progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

/*        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                teamDetails.setCode(dataSnapshot.child(getString(R.string.team_code)).getValue().toString());
                String team_image = dataSnapshot.child(getString(R.string.team_image)).getValue().toString();
                teamDetails.setImage(team_image);
                Log.d(TAG, "onDataChange: dataSnapshot.child(getString(R.string.team_id)).getValue().toString()"+dataSnapshot.child(getString(R.string.team_id)).getValue().toString());
                teamDetails.setId(dataSnapshot.child(getString(R.string.team_id)).getValue().toString());
                String team_name = dataSnapshot.child(getString(R.string.team_name)).getValue().toString();
                teamDetails.setName(team_name);
                NotificationTeamActivity.this.team_name.setText(team_name);

                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .error(R.drawable.ic_load);
                Glide.with(context).load(team_image).apply(options).transition(DrawableTransitionOptions.withCrossFade(500)).into(background_team);

                notifications_teams_progressbar.setVisibility(View.GONE);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    public void setInfoTeamDetailsForRegionForTeam (String region , String team){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_teams))
                .child(region)
                .child(team);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TeamDetails databaseTeamDetails = dataSnapshot.getValue(TeamDetails.class);
                if (databaseTeamDetails!= null){
                    NotificationTeamActivity.this.team_name.setText(databaseTeamDetails.getName());

                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .error(R.drawable.ic_load);
                    Glide.with(context).load(databaseTeamDetails.getImage()).apply(options).transition(DrawableTransitionOptions.withCrossFade(500)).into(background_team);

                    notifications_teams_progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

/*        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                teamDetails.setCode(dataSnapshot.child(getString(R.string.team_code)).getValue().toString());
                String team_image = dataSnapshot.child(getString(R.string.team_image)).getValue().toString();
                teamDetails.setImage(team_image);
                Log.d(TAG, "onDataChange: dataSnapshot.child(getString(R.string.team_id)).getValue().toString()"+dataSnapshot.child(getString(R.string.team_id)).getValue().toString());
                teamDetails.setId(dataSnapshot.child(getString(R.string.team_id)).getValue().toString());
                String team_name = dataSnapshot.child(getString(R.string.team_name)).getValue().toString();
                teamDetails.setName(team_name);
                NotificationTeamActivity.this.team_name.setText(team_name);

                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .error(R.drawable.ic_load);
                Glide.with(context).load(team_image).apply(options).transition(DrawableTransitionOptions.withCrossFade(500)).into(background_team);

                notifications_teams_progressbar.setVisibility(View.GONE);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }







    private void saveButton() {

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference notificationTeamReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.firebase_user_notification))
                        .child(getString(R.string.firebase_user_notification_region))
                        .child(regionSelectedExtra)
                        .child(teamSelectedExtra);

                // per ogni checkbox  setta a 0 o 1
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

                Toast.makeText(NotificationTeamActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NotificationTeamActivity.this, NotificationRegionActivity.class);
                intent.putExtra(REGION_SELECTED, regionSelectedExtra);
                startActivity( intent);


            }
        });

    }

    private void loadSettingsForThisTeam(String regionSelected, String teamSelected ) {

        DatabaseReference notificationTeamReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_notification))
                .child(getString(R.string.firebase_user_notification_region))
                .child(regionSelected)
                .child(teamSelected);

        Log.d(TAG, "loadSettingsForThisRegion: regionSelected: "+regionSelected);

        notificationTeamReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                TeamNotification teamNotification = dataSnapshot.getValue(TeamNotification.class);
                if (teamNotification !=null){
                    Integer atp = teamNotification.getNotification_team_as_team_plays();
                    Integer mr = teamNotification.getNotification_team_morning_reminder();

                    checkbox_as_team_plays.setChecked(atp > 0);
                    checkbox_team_morning_reminder.setChecked(mr > 0);


                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        notifications_teams_progressbar.setVisibility(View.INVISIBLE);

    }

}