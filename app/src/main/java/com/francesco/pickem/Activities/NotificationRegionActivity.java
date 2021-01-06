package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.francesco.pickem.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_region);
        teams_list = new ArrayList<>();

        listview_teams = findViewById(R.id.notification_region_listview_teams);
        region_name = findViewById(R.id.notification_region_name);
        notification_regions_progressbar = findViewById(R.id.notification_regions_progressbar);

        Bundle extras = getIntent().getExtras();
        String regionSelectedExtra = extras.getString(REGION_SELECTED);
        Log.d(TAG, "onCreate: REGION_SELECTED: " +regionSelectedExtra);

        region_name.setText(regionSelectedExtra);
        notification_regions_progressbar.setVisibility(View.VISIBLE);
        getAllTeams(regionSelectedExtra);


/*        // load da firebase i team della regione REGION_SELECTED
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_teams))
                .child(REGION_SELECTED);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String teamName = snapshot.child(getString(R.string.team_code)).getValue().toString();
                    teams_list.add(teamName);

                    Log.d(TAG, "onDataChange: adding teamName to teams_list:"+teamName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                teams_list );

        listview_teams.setAdapter(arrayAdapter);

        listview_teams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stringTeamSelected = teams_list.get(position);
                Intent intent = new Intent(NotificationRegionActivity.this, NotificationTeamActivity.class);
                intent.putExtra(TEAM_SELECTED, stringTeamSelected);
                startActivity(intent);

            }
        });

        // se nessuno dei 3 checkbox è attivo elimina la regione dalle notifiche*/



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


/*    private void updateFirebaseRegionNotifications(UserGeneralities userGeneralities) {

        ArrayList <String> newRegions = new ArrayList<>();
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
        }

    }*/

}