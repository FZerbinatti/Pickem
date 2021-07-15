package com.dreamsphere.pickem.Activities.IndipendentActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dreamsphere.pickem.Adapters.RecyclerView_ChooseGame_Adapter;
import com.dreamsphere.pickem.Models.GlobalMatchStats;
import com.dreamsphere.pickem.Models.GlobalMatchStatsSimplified;
import com.dreamsphere.pickem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityChooseGame extends AppCompatActivity {

    Context context;
    RecyclerView_ChooseGame_Adapter adapterRecycler;
    RecyclerView recyclerView;
    ImageView back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game);
        recyclerView= findViewById(R.id.choose_game_recyclerview);
        back_arrow = findViewById(R.id.back_arrow);
        context=this;

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Intent intent = getIntent();
        String match_ID = intent.getStringExtra       ("MATCH_ID" );
        String region = intent.getStringExtra       ("REGION" );
        String year = intent.getStringExtra       ("YEAR" );


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_statistics))
                .child(region)
                .child(region+year)
                .child(context.getString(R.string.firebase_matches_stats))
                .child(match_ID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer children = (int) snapshot.getChildrenCount();
                String match_id = snapshot.getKey();

                Integer counter =0;
                ArrayList<GlobalMatchStatsSimplified> allMatches = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    counter++;

                    GlobalMatchStats singleMatch = dataSnapshot.getValue(GlobalMatchStats.class);

                    GlobalMatchStatsSimplified globalMatchStatsSimplified = new GlobalMatchStatsSimplified();
                    globalMatchStatsSimplified.setTeam1(singleMatch.getTeam1());
                    globalMatchStatsSimplified.setTeam2(singleMatch.getTeam2());
                    globalMatchStatsSimplified.setDatetime(match_ID);
                    globalMatchStatsSimplified.setEnded(singleMatch.getEnded());
                    globalMatchStatsSimplified.setRegion(region);
                    globalMatchStatsSimplified.setYear(year);

                    Log.d("TAG", "onDataChange: dioca "+singleMatch.getWinner());
                    globalMatchStatsSimplified.setWinner(singleMatch.getWinner());

                    allMatches.add(globalMatchStatsSimplified);
                }
                if (counter == children){

                    adapterRecycler = new RecyclerView_ChooseGame_Adapter(context, allMatches);
                    adapterRecycler.notifyDataSetChanged();
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));

                    recyclerView.setAdapter(adapterRecycler);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}