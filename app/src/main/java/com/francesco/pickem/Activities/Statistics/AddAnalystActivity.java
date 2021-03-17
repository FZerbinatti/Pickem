package com.francesco.pickem.Activities.Statistics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.francesco.pickem.Activities.SettingsActivities.InfoActivity2;
import com.francesco.pickem.Adapters.SimpleRegionRecyclerViewAdapter;
import com.francesco.pickem.Models.AnalistPerson;
import com.francesco.pickem.Models.SimpleRegion;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddAnalystActivity extends AppCompatActivity {

    public static final String TAG ="AddAnalystsActivity ";
    RecyclerView add_analysts_list;
    ListView add_analysts_serverlist;
    TextView analyst_request;
    ImageButton back_arrow;

    ArrayList<String> finalAnalysts;
    ArrayList <AnalistPerson> analists;
    ArrayList<String>userAnalysts;
    Context context;
    TextView analyst_save;
    String serverSelected="";
    ArrayList <SimpleRegion> analists_of_region;
    ArrayList<SimpleRegion> all_analysts_for_this_region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_analyst);
        back_arrow = findViewById(R.id.back_arrow);

        analyst_request = findViewById(R.id.analyst_request);
        add_analysts_serverlist = findViewById(R.id.add_analysts_serverlist);
        analyst_save= findViewById(R.id.analyst_save);
        analists = new ArrayList<>();
        context = this;



        actionButton();



        getUserAnalysts();


    }

    private void actionButton() {

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        analyst_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: saved: list size: "+ finalAnalysts.size());
                for(int i=0; i<finalAnalysts.size(); i++){
                    Log.d(TAG, "onClick: "+finalAnalysts.get(i).toString());
                }

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getResources().getString(R.string.firebase_users_analists))
                        .setValue(finalAnalysts).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: ");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.toString());
                    }
                });


            }
        });

        analyst_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AddAnalystActivity.this, InfoActivity2.class);
                startActivity(intent);
            }
        });

    }

    private void getUserAnalysts() {

        userAnalysts = new ArrayList<>();
        finalAnalysts = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getResources().getString(R.string.firebase_users_analists));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer interazione =0;
                int count = (int) (snapshot.getChildrenCount());

                for(DataSnapshot dataSnapshot : snapshot .getChildren()){
                    String analist = dataSnapshot.getValue(String.class);
                    finalAnalysts.add(analist);
                    userAnalysts.add(analist);
                    interazione++;
                }
                if (interazione == count){
                    Log.d(TAG, "onDataChange: number of analysts: " +analists.size());
                    getAllAnalysts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllAnalysts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_analysts));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer interazione =0;
                int count = (int) (snapshot.getChildrenCount());

                for(DataSnapshot dataSnapshot : snapshot .getChildren()){
                    AnalistPerson analist = dataSnapshot.getValue(AnalistPerson.class);
                    analists.add(analist);
                    interazione++;
                }
                if (interazione == count){
                    Log.d(TAG, "onDataChange: number of analysts: " +analists.size());
                    getAllServers();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void  getAllServers(){
        Log.d(TAG, "getAllRegions: ");

        ArrayList<String> regions = new ArrayList<>();

        // load da firebase le regioni
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_servers));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String regionName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+regionName);
                    regions.add(regionName);
                }
                loadListview(regions);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "getAllRegions: regions.size(): "+regions.size());

    }

    private void loadListview(ArrayList<String> allRegions) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                allRegions );



        add_analysts_serverlist.setAdapter(arrayAdapter);

        add_analysts_serverlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String stringRegionSelected = allRegions.get(position);
                serverSelected=stringRegionSelected;
                all_analysts_for_this_region = new ArrayList<>();
                loadAnalystsForThisRegion(stringRegionSelected);


            }
        });

    }

    private void loadAnalystsForThisRegion(String regionSelected) {

        analists_of_region = new ArrayList<>();
        for(int i=0; i<analists.size(); i++){

            if (analists.get(i).getRegion().equals(regionSelected)){
                if (userAnalysts.contains(analists.get(i).getUsername())){
                    analists_of_region.add(new SimpleRegion(analists.get(i).getUsername(), true));
                }else {
                    analists_of_region.add(new SimpleRegion(analists.get(i).getUsername(), false));
                }

            }

        }
        Log.d(TAG, "loadAnalystsForThisRegion: analists_of_region.size()= " +analists_of_region.size());
        loadListviewAnalistThiRegion(analists_of_region);

    }

    private void loadListviewAnalistThiRegion(ArrayList<SimpleRegion> all_analysts_for_this_region) {

        add_analysts_list = findViewById(R.id.add_analysts_list);
        Log.d(TAG, "all_analysts_for_this_region: "+ all_analysts_for_this_region.size());
        add_analysts_list.setLayoutManager(new LinearLayoutManager(this));
        SimpleRegionRecyclerViewAdapter adapter = new SimpleRegionRecyclerViewAdapter(getApplicationContext(), all_analysts_for_this_region);
        adapter.notifyDataSetChanged();
        add_analysts_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        add_analysts_list.addOnItemTouchListener(

                new RecyclerItemClickListener(context, add_analysts_list , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d(TAG, "onItemClick: "+position);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Log.d(TAG, "onLongItemClick: "+position );
                        Log.d(TAG, "onLongItemClick: "+all_analysts_for_this_region.get(position).getName());
                        Log.d(TAG, "onLongItemClick: "+all_analysts_for_this_region.get(position).getChecked());
                        if (all_analysts_for_this_region.get(position).getChecked()){
                            Log.d(TAG, "onLongItemClick: is checked");
                        }else {
                            Log.d(TAG, "onLongItemClick: is not checked");
                        }

/*
                        if (all_analysts_for_this_region.get(position).getChecked()){
                            all_analysts_for_this_region.get(position).setChecked(false);
                            adapter.notifyDataSetChanged();
                            //Log.d(TAG, "onLongItemClick: false");
                            if (finalAnalysts.contains(all_analysts_for_this_region.get(position).getName())){
                                finalAnalysts.remove(all_analysts_for_this_region.get(position).getName());
                            }
                        }else {
                            all_analysts_for_this_region.get(position).setChecked(true);
                            //Log.d(TAG, "onLongItemClick: true");
                            adapter.notifyDataSetChanged();
                            finalAnalysts.add(all_analysts_for_this_region.get(position).getName());



                        }


*/


                    }
                })
        );

        // set up the RecyclerView






    }






}