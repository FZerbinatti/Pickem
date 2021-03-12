package com.francesco.pickem.Activities.Statistics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
    SimpleRegionRecyclerViewAdapter adapter;
    ArrayList<String> finalAnalysts;
    ArrayList <AnalistPerson> analists;
    ArrayList<String>userAnalysts;
    Context context;
    TextView analyst_save;
    String serverSelected="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_analyst);
        back_arrow = findViewById(R.id.back_arrow);
        add_analysts_list = findViewById(R.id.add_analysts_list);
        analyst_request = findViewById(R.id.analyst_request);
        add_analysts_serverlist = findViewById(R.id.add_analysts_serverlist);
        analyst_save= findViewById(R.id.analyst_save);
        analists = new ArrayList<>();
        context = this;

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


        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUserAnalysts();




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
                loadStanding(stringRegionSelected);


            }
        });

    }

    private void loadStanding(String regionSelected) {

        ArrayList <SimpleRegion> analists_of_region = new ArrayList<>();
        for(int i=0; i<analists.size(); i++){

            if (analists.get(i).getRegion().equals(regionSelected)){
                if (userAnalysts.contains(analists.get(i).getUsername())){
                    analists_of_region.add(new SimpleRegion(analists.get(i).getUsername(), true));
                }else {
                    analists_of_region.add(new SimpleRegion(analists.get(i).getUsername(), false));
                }

            }

        }
        loadListviewStandings(analists_of_region);

    }

    private void loadListviewStandings(ArrayList<SimpleRegion> all_analysts) {

        Log.d(TAG, "loadListviewChooseRegions: "+ all_analysts.size());


        // set up the RecyclerView

        add_analysts_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SimpleRegionRecyclerViewAdapter(getApplicationContext(), all_analysts);
        add_analysts_list.setAdapter(adapter);


        add_analysts_list.addOnItemTouchListener(
                new RecyclerItemClickListener(context, add_analysts_list , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Log.d(TAG, "onLongItemClick: ");
                        // do whatever
                        if (all_analysts.get(position).getChecked()){
                            all_analysts.get(position).setChecked(false);
                            adapter.notifyDataSetChanged();
                            //Log.d(TAG, "onLongItemClick: false");
                            if (finalAnalysts.contains(all_analysts.get(position).getName())){
                                finalAnalysts.remove(all_analysts.get(position).getName());
                            }
                        }else {
                            all_analysts.get(position).setChecked(true);
                            //Log.d(TAG, "onLongItemClick: true");
                            adapter.notifyDataSetChanged();
                            finalAnalysts.add(all_analysts.get(position).getName());



                        }

                        Log.d(TAG, "onLongItemClick: "+ all_analysts.get(position).getName()+":"+ all_analysts.get(position).getChecked());


                    }
                })
        );






    }






}