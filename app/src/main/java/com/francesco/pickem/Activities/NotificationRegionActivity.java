package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.francesco.pickem.R;

import java.util.ArrayList;

public class NotificationRegionActivity extends AppCompatActivity {

    ListView listview_teams;
    TextView region_name;
    private static String TEAM_SELECTED = "TEAM_SELECTED";
    public static String REGION_SELECTED = "REGION_SELECTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_region);

        listview_teams = findViewById(R.id.notification_region_listview_teams);
        region_name = findViewById(R.id.notification_region_name);

        Bundle extras = getIntent().getExtras();
        String regionSelectedExtra = extras.getString(REGION_SELECTED);

        region_name.setText(regionSelectedExtra);

        ArrayList <String> placeHolderTeams = new ArrayList<>();
        placeHolderTeams.add("FNC");
        placeHolderTeams.add("G2");
        placeHolderTeams.add("VIT");
        placeHolderTeams.add("RGE");
        placeHolderTeams.add("OG");
        placeHolderTeams.add("MSF");
        placeHolderTeams.add("XL");
        placeHolderTeams.add("S04");
        placeHolderTeams.add("SK");
        placeHolderTeams.add("MAD");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                placeHolderTeams );

        listview_teams.setAdapter(arrayAdapter);

        listview_teams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stringTeamSelected = placeHolderTeams.get(position);
                Intent intent = new Intent(NotificationRegionActivity.this, NotificationTeamActivity.class);
                intent.putExtra(TEAM_SELECTED, stringTeamSelected);
                startActivity(intent);

            }
        });


    }
}