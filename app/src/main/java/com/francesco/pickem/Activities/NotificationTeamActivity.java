package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.francesco.pickem.R;

public class NotificationTeamActivity extends AppCompatActivity {

    private static String TEAM_SELECTED = "TEAM_SELECTED";
    TextView team_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_team);

        team_name = findViewById(R.id.notification_team_name);

        Bundle extras = getIntent().getExtras();
        String teamSelectedExtra = extras.getString(TEAM_SELECTED);

        team_name.setText(teamSelectedExtra);



    }
}