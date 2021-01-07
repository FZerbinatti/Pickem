package com.francesco.pickem.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.francesco.pickem.Models.TeamDetails;
import com.francesco.pickem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationTeamActivity extends AppCompatActivity {

    private static String TEAM_SELECTED = "TEAM_SELECTED";
    private static String TAG = "Notification Teams: ";
    TextView team_name;
    TeamDetails teamDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_team);

        Bundle extras = getIntent().getExtras();
        String teamSelectedExtra = extras.getString(TEAM_SELECTED);
        Log.d(TAG, "onCreate: REGION_SELECTED: " +TEAM_SELECTED);
        teamDetails = new TeamDetails();
        team_name = findViewById(R.id.notification_team_name);


        team_name.setText(teamSelectedExtra);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_teams))
                .child(teamSelectedExtra);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                teamDetails.setCode(dataSnapshot.child(getString(R.string.team_code)).getValue().toString());
                teamDetails.setImage(dataSnapshot.child(getString(R.string.team_image)).getValue().toString());
                teamDetails.setId(Integer.parseInt(dataSnapshot.child(getString(R.string.team_id)).getValue().toString()));
                teamDetails.setName(dataSnapshot.child(getString(R.string.team_name)).getValue().toString());

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}