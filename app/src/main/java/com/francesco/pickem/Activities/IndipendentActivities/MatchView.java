package com.francesco.pickem.Activities.IndipendentActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Models.GlobalMatchStats;
import com.francesco.pickem.Models.Player;
import com.francesco.pickem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MatchView extends AppCompatActivity {

    public static final String TAG ="MatchVIew";

    TextView team_1_win, team_2_win, team_1_loss, team_2_loss;
    ImageView team_1_logo, team_2_logo;
    TextView team_1_score, team_2_score;
    ImageView imageview_team_1_top , imageview_team_2_top, imageview_team_1_jungler, imageview_team_2_jungler,
            imageview_team_1_midlaner, imageview_team_2_midlaner, imageview_team_1_adc, imageview_team_2_adc,
            imageview_team_1_supp, imageview_team_2_supp;
    TextView team_1_top_name, team_1_top_champion, team_1_top_score,
            team_1_jungler_name,team_1_jungler_champion,team_1_jungler_score,
             team_1_midlaner_name,team_1_midlaner_champion,team_1_midlaner_score,
             team_1_adc_name,team_1_adc_champion,team_1_adc_score,
             team_1_supp_name,team_1_supp_champion,team_1_supp_score;
    TextView team_2_top_name, team_2_top_champion, team_2_top_score,
            team_2_jungler_name,team_2_jungler_champion,team_2_jungler_score,
            team_2_midlaner_name,team_2_midlaner_champion,team_2_midlaner_score,
            team_2_adc_name,team_2_adc_champion,team_2_adc_score,
            team_2_supp_name,team_2_supp_champion,team_2_supp_score;
    TextView tv_matchtime, team_1_total_gold, team_2_total_gold, team_1_barons, team_2_barons, team_1_elders, team_2_elders, team_1_inibitors, team_2_inibitors;
    ImageView team1_drake_1, team1_drake_2, team1_drake_3, team1_drake_4;
    ImageView team2_drake_1, team2_drake_2, team2_drake_3, team2_drake_4;
    Calendar myCalendar;
    String year;
    Context context;
    String imageTeamsPath;
    CardView cv11, cv12 , cv13, cv14, cv21, cv22, cv23, cv24;

    ArrayList <Player> team1_players;
    ArrayList <Player> team2_players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);
        findViews();
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));
        context = this;
        imageTeamsPath = context.getFilesDir().getAbsolutePath() + (getString(R.string.folder_teams_images));



        Intent intent = getIntent();

        String match_ID = intent.getStringExtra       ("MATCH_ID" );
        String match_region = intent.getStringExtra     ("REGION" );
        String match_winner = intent.getStringExtra     ("WINNER" );
        String team1 = intent.getStringExtra     ("TEAM1" );
        String team2 = intent.getStringExtra     ("TEAM2" );

        Log.d(TAG, "onCreate: " + match_ID + " region: " + match_region+ " winner: " + match_winner);

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_load);

        Glide.with(context)
                .load(new File(imageTeamsPath +team1)) // Uri of the picture
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(team_1_logo);

        Glide.with(context)
                .load(new File(imageTeamsPath +team2)) // Uri of the picture
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(team_2_logo);

        if (match_winner.equals(team1)){
            team_1_win.setVisibility(View.VISIBLE);
            team_2_loss.setVisibility(View.VISIBLE);
        }else {
            team_2_win.setVisibility(View.VISIBLE);
            team_1_loss.setVisibility(View.VISIBLE);
        }

        downloadMatchStats(match_region, match_ID, team1, team2);

    }

    private void downloadMatchStats(String region,  String match_ID, String team1, String team2) {

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_load);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_statistics))
                .child(region)
                .child(region+year)
                .child(getString(R.string.firebase_matches_stats));
                //.child(match_ID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {

                GlobalMatchStats globalMatchStats = dataSnapshot.getValue(GlobalMatchStats.class);

                tv_matchtime.setText(globalMatchStats.getEnded().toString());

                //      T E A M    1
                ArrayList <String> playersTeam1 = new ArrayList<>();
                //players names
                String top1 = globalMatchStats.getTeam1().getPlayers().get(0).getSummonerName();
                team_1_top_name.setText(top1);  playersTeam1.add(top1);
                String jng1 = globalMatchStats.getTeam1().getPlayers().get(1).getSummonerName();
                team_1_jungler_name.setText(jng1); playersTeam1.add(jng1);
                String mid1 = globalMatchStats.getTeam1().getPlayers().get(2).getSummonerName();
                team_1_midlaner_name.setText(mid1); playersTeam1.add(mid1);
                String adc1 = globalMatchStats.getTeam1().getPlayers().get(3).getSummonerName();
                team_1_adc_name.setText(adc1); playersTeam1.add(adc1);
                String supp1 = globalMatchStats.getTeam1().getPlayers().get(4).getSummonerName();
                team_1_supp_name.setText(supp1); playersTeam1.add(supp1);
                //players scores
                team_1_top_score.setText(globalMatchStats.getTeam1().getPlayers().get(0).getKills()+"/"+ globalMatchStats.getTeam1().getPlayers().get(0).getDeaths()+"/"+ globalMatchStats.getTeam1().getPlayers().get(0).getAssists());
                team_1_jungler_score.setText(globalMatchStats.getTeam1().getPlayers().get(1).getKills()+"/"+ globalMatchStats.getTeam1().getPlayers().get(1).getDeaths()+"/"+ globalMatchStats.getTeam1().getPlayers().get(1).getAssists());
                team_1_midlaner_score.setText(globalMatchStats.getTeam1().getPlayers().get(2).getKills()+"/"+ globalMatchStats.getTeam1().getPlayers().get(2).getDeaths()+"/"+ globalMatchStats.getTeam1().getPlayers().get(2).getAssists());
                team_1_adc_score.setText(globalMatchStats.getTeam1().getPlayers().get(3).getKills()+"/"+ globalMatchStats.getTeam1().getPlayers().get(3).getDeaths()+"/"+ globalMatchStats.getTeam1().getPlayers().get(3).getAssists());
                team_1_supp_score.setText(globalMatchStats.getTeam1().getPlayers().get(4).getKills()+"/"+ globalMatchStats.getTeam1().getPlayers().get(4).getDeaths()+"/"+ globalMatchStats.getTeam1().getPlayers().get(4).getAssists());
                //champions names
                team_1_top_champion.setText(globalMatchStats.getTeam1().getPlayers().get(0).getChampionName());
                team_1_jungler_champion.setText(globalMatchStats.getTeam1().getPlayers().get(1).getChampionName());
                team_1_midlaner_champion.setText(globalMatchStats.getTeam1().getPlayers().get(2).getChampionName());
                team_1_adc_champion.setText(globalMatchStats.getTeam1().getPlayers().get(3).getChampionName());
                team_1_supp_champion.setText(globalMatchStats.getTeam1().getPlayers().get(4).getChampionName());
                //bottom stats
                team_1_total_gold.setText(globalMatchStats.getTeam1().getTotalGold());
                team_1_barons.setText(globalMatchStats.getTeam1().getBarons());
                team_1_inibitors.setText(globalMatchStats.getTeam1().getInhibitors());
                team_1_score.setText(globalMatchStats.getTeam1().getTotalKills());

                for(int i=0; i<globalMatchStats.getTeam1().getDragons().size(); i++){
                    String drakeType = globalMatchStats.getTeam1().getDragons().get(i);
                    if (i==0){
                        if (drakeType.equals("cloud")){
                            Glide.with(context)
                                    .load(R.drawable.drake_cloud) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_1);
                        }else if (drakeType.equals("mountain")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_1);
                        }else if (drakeType.equals("ocean")){
                            Glide.with(context)
                                    .load(R.drawable.drake_ocean) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_1);
                        }else if (drakeType.equals("infernal")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_1);
                        }
                    }else if (i==1){
                        if (drakeType.equals("cloud")){
                            Glide.with(context)
                                    .load(R.drawable.drake_cloud) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_2);
                        }else if (drakeType.equals("mountain")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_2);
                        }else if (drakeType.equals("ocean")){
                            Glide.with(context)
                                    .load(R.drawable.drake_ocean) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_2);
                        }else if (drakeType.equals("infernal")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_2);
                        }
                    }else if (i==2){
                        if (drakeType.equals("cloud")){
                            Glide.with(context)
                                    .load(R.drawable.drake_cloud) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_3);
                        }else if (drakeType.equals("mountain")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_3);
                        }else if (drakeType.equals("ocean")){
                            Glide.with(context)
                                    .load(R.drawable.drake_ocean) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_3);
                        }else if (drakeType.equals("infernal")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_3);
                        }
                    }else if (i==3){
                        if (drakeType.equals("cloud")){
                            Glide.with(context)
                                    .load(R.drawable.drake_cloud) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_4);
                        }else if (drakeType.equals("mountain")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_4);
                        }else if (drakeType.equals("ocean")){
                            Glide.with(context)
                                    .load(R.drawable.drake_ocean) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_4);
                        }else if (drakeType.equals("infernal")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team1_drake_4);
                        }
                    }


                }

                loadPlayersImageTeam1(playersTeam1, region, team1);


                //      T E A M    2
                ArrayList <String> playersTeam2 = new ArrayList<>();
                //players names
                String top2 = globalMatchStats.getTeam2().getPlayers().get(0).getSummonerName();
                team_2_top_name.setText(top2);  playersTeam2.add(top2);
                String jng2 = globalMatchStats.getTeam2().getPlayers().get(1).getSummonerName();
                team_2_jungler_name.setText(jng2); playersTeam2.add(jng2);
                String mid2 = globalMatchStats.getTeam2().getPlayers().get(2).getSummonerName();
                team_2_midlaner_name.setText(mid2); playersTeam2.add(mid2);
                String adc2 = globalMatchStats.getTeam2().getPlayers().get(3).getSummonerName();
                team_2_adc_name.setText(adc2); playersTeam2.add(adc2);
                String supp2 = globalMatchStats.getTeam2().getPlayers().get(4).getSummonerName();
                team_2_supp_name.setText(supp2); playersTeam2.add(supp2);
                //players scores
                team_2_top_score.setText(globalMatchStats.getTeam2().getPlayers().get(0).getKills()+"/"+ globalMatchStats.getTeam2().getPlayers().get(0).getDeaths()+"/"+ globalMatchStats.getTeam2().getPlayers().get(0).getAssists());
                team_2_jungler_score.setText(globalMatchStats.getTeam2().getPlayers().get(1).getKills()+"/"+ globalMatchStats.getTeam2().getPlayers().get(1).getDeaths()+"/"+ globalMatchStats.getTeam2().getPlayers().get(1).getAssists());
                team_2_midlaner_score.setText(globalMatchStats.getTeam2().getPlayers().get(2).getKills()+"/"+ globalMatchStats.getTeam2().getPlayers().get(2).getDeaths()+"/"+ globalMatchStats.getTeam2().getPlayers().get(2).getAssists());
                team_2_adc_score.setText(globalMatchStats.getTeam2().getPlayers().get(3).getKills()+"/"+ globalMatchStats.getTeam2().getPlayers().get(3).getDeaths()+"/"+ globalMatchStats.getTeam2().getPlayers().get(3).getAssists());
                team_2_supp_score.setText(globalMatchStats.getTeam2().getPlayers().get(4).getKills()+"/"+ globalMatchStats.getTeam2().getPlayers().get(4).getDeaths()+"/"+ globalMatchStats.getTeam2().getPlayers().get(4).getAssists());
                //champions names
                team_2_top_champion.setText(globalMatchStats.getTeam2().getPlayers().get(0).getChampionName());
                team_2_jungler_champion.setText(globalMatchStats.getTeam2().getPlayers().get(1).getChampionName());
                team_2_midlaner_champion.setText(globalMatchStats.getTeam2().getPlayers().get(2).getChampionName());
                team_2_adc_champion.setText(globalMatchStats.getTeam2().getPlayers().get(3).getChampionName());
                team_2_supp_champion.setText(globalMatchStats.getTeam2().getPlayers().get(4).getChampionName());
                //bottom stats
                team_2_total_gold.setText(globalMatchStats.getTeam2().getTotalGold());
                team_2_barons.setText(globalMatchStats.getTeam2().getBarons());
                team_2_inibitors.setText(globalMatchStats.getTeam2().getInhibitors());
                team_2_score.setText(globalMatchStats.getTeam2().getTotalKills());


                for(int i=0; i<globalMatchStats.getTeam2().getDragons().size(); i++){
                    String drakeType = globalMatchStats.getTeam2().getDragons().get(i);
                    if (i==0){
                        if (drakeType.equals("cloud")){
                            Glide.with(context)
                                    .load(R.drawable.drake_cloud) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_1);
                        }else if (drakeType.equals("mountain")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_1);
                        }else if (drakeType.equals("ocean")){
                            Glide.with(context)
                                    .load(R.drawable.drake_ocean) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_1);
                        }else if (drakeType.equals("infernal")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_1);
                        }
                    }else if (i==1){
                        if (drakeType.equals("cloud")){
                            Glide.with(context)
                                    .load(R.drawable.drake_cloud) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_2);
                        }else if (drakeType.equals("mountain")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_2);
                        }else if (drakeType.equals("ocean")){
                            Glide.with(context)
                                    .load(R.drawable.drake_ocean) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_2);
                        }else if (drakeType.equals("infernal")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_2);
                        }
                    }else if (i==2){
                        if (drakeType.equals("cloud")){
                            Glide.with(context)
                                    .load(R.drawable.drake_cloud) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_3);
                        }else if (drakeType.equals("mountain")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_3);
                        }else if (drakeType.equals("ocean")){
                            Glide.with(context)
                                    .load(R.drawable.drake_ocean) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_3);
                        }else if (drakeType.equals("infernal")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_3);
                        }
                    }else if (i==3){
                        if (drakeType.equals("cloud")){
                            Glide.with(context)
                                    .load(R.drawable.drake_cloud) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_4);
                        }else if (drakeType.equals("mountain")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_4);
                        }else if (drakeType.equals("ocean")){
                            Glide.with(context)
                                    .load(R.drawable.drake_ocean) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_4);
                        }else if (drakeType.equals("infernal")){
                            Glide.with(context)
                                    .load(R.drawable.drake_montain) // Uri of the picture
                                    .apply(options)
                                    .transition(DrawableTransitionOptions.withCrossFade(500))
                                    .into(team2_drake_4);
                        }
                    }


                }

                loadPlayersImageTeam2(playersTeam2, region, team2);










            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadPlayersImageTeam1(ArrayList<String> players, String region, String team) {

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_load);


        team1_players = new ArrayList<>();

        //get all Players from firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_teams))
                .child(region)
                .child(team)
                .child(getString(R.string.firebase_players));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                int count = (int) (dataSnapshot.getChildrenCount());
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Player player = snapshot.getValue(Player.class);
                    team1_players.add(player);
                }
                if (team1_players.size()==count){

                    for(int i=0; i<players.size(); i++){
                        String[] array = players.get(i).toString().split(" ");
                        String playerName = array[1];

                        for(int j=0; j<team1_players.size(); j++){
                            if (team1_players.get(j).getSummonerName().equals(playerName)){
                                if (i==0){
                                    Glide.with(context)
                                            .load(team1_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_1_top);
                                }else if (i==1){
                                    Glide.with(context)
                                            .load(team1_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_1_jungler);
                                }else if (i==2){
                                    Glide.with(context)
                                            .load(team1_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_1_midlaner);
                                }else if (i==3){
                                    Glide.with(context)
                                            .load(team1_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_1_adc);
                                }else if (i==4){
                                    Glide.with(context)
                                            .load(team1_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_1_supp);
                                }


                            }
                        }



                    }

                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });


    }

    private void loadPlayersImageTeam2(ArrayList<String> players, String region, String team) {

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_load);


        team2_players = new ArrayList<>();

        //get all Players from firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_teams))
                .child(region)
                .child(team)
                .child(getString(R.string.firebase_players));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                int count = (int) (dataSnapshot.getChildrenCount());
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Player player = snapshot.getValue(Player.class);
                    team2_players.add(player);
                }
                if (team2_players.size()==count){

                    for(int i=0; i<players.size(); i++){
                        String[] array = players.get(i).toString().split(" ");
                        String playerName = array[1];

                        for(int j=0; j<team2_players.size(); j++){
                            if (team2_players.get(j).getSummonerName().equals(playerName)){
                                if (i==0){
                                    Glide.with(context)
                                            .load(team2_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_2_top);
                                }else if (i==1){
                                    Glide.with(context)
                                            .load(team2_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_2_jungler);
                                }else if (i==2){
                                    Glide.with(context)
                                            .load(team2_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_2_midlaner);
                                }else if (i==3){
                                    Glide.with(context)
                                            .load(team2_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_2_adc);
                                }else if (i==4){
                                    Glide.with(context)
                                            .load(team2_players.get(j).getImage()) // Uri of the picture
                                            .apply(options)
                                            .transition(DrawableTransitionOptions.withCrossFade(500))
                                            .into(imageview_team_2_supp);
                                }


                            }
                        }



                    }

                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });


    }

    private void findViews() {

        team1_drake_1 = findViewById(R.id.team1_drake_1);
        team1_drake_2 = findViewById(R.id.team1_drake_2);
        team1_drake_3 = findViewById(R.id.team1_drake_3);
        team1_drake_4 = findViewById(R.id.team1_drake_4);
        team2_drake_1 = findViewById(R.id.team2_drake_1);
        team2_drake_2 = findViewById(R.id.team2_drake_2);
        team2_drake_3 = findViewById(R.id.team2_drake_3);
        team2_drake_4 = findViewById(R.id.team2_drake_4);

        tv_matchtime = findViewById(R.id.tv_matchtime);
        team_1_total_gold = findViewById(R.id.team_1_total_gold);
        team_2_total_gold = findViewById(R.id.team_2_total_gold);
        team_1_barons = findViewById(R.id.team_1_barons);
        team_2_barons = findViewById(R.id.team_2_barons);
        //team_1_elders = findViewById(R.id.team_1_elders);
        //team_2_elders = findViewById(R.id.team_2_elders);

        team_1_top_name = findViewById(R.id.team_1_top_name);
        team_1_top_champion = findViewById(R.id.team_1_top_champion);
        team_1_top_score = findViewById(R.id.team_1_top_score);
        team_1_jungler_name = findViewById(R.id.team_1_jungler_name1);
        team_1_jungler_champion = findViewById(R.id.team_1_jungler_champion);
        team_1_jungler_score = findViewById(R.id.team_1_jungler_score);
        team_1_midlaner_name = findViewById(R.id.team_1_midlaner_name);
        team_1_midlaner_champion = findViewById(R.id.team_1_midlaner_champion);
        team_1_midlaner_score = findViewById(R.id.team_1_midlaner_score);
        team_1_adc_name = findViewById(R.id.team_1_adc_name);
        team_1_adc_champion = findViewById(R.id.team_1_adc_champion);
        team_1_adc_score = findViewById(R.id.team_1_adc_score);
        team_1_supp_name = findViewById(R.id.team_1_supp_name);
        team_1_supp_champion = findViewById(R.id.team_1_supp_champion);
        team_1_supp_score = findViewById(R.id.team_1_supp_score);

        team_2_top_name = findViewById(R.id.team_2_top_name);
        team_2_top_champion = findViewById(R.id.team_2_top_champion);
        team_2_top_score = findViewById(R.id.team_2_top_score);
        team_2_jungler_name = findViewById(R.id.team_2_jungler_name);
        team_2_jungler_champion = findViewById(R.id.team_2_jungler_champion);
        team_2_jungler_score = findViewById(R.id.team_2_jungler_score);
        team_2_midlaner_name = findViewById(R.id.team_2_midlaner_name);
        team_2_midlaner_champion = findViewById(R.id.team_2_midlaner_champion);
        team_2_midlaner_score = findViewById(R.id.team_2_midlaner_score);
        team_2_adc_name = findViewById(R.id.team_2_adc_name);
        team_2_adc_champion = findViewById(R.id.team_2_adc_champion);
        team_2_adc_score = findViewById(R.id.team_2_adc_score);
        team_2_supp_name = findViewById(R.id.team_2_supp_name);
        team_2_supp_champion = findViewById(R.id.team_2_supp_champion);
        team_2_supp_score = findViewById(R.id.team_2_supp_score);

        imageview_team_1_top = findViewById(R.id.imageview_team_1_top);
        imageview_team_2_top = findViewById(R.id.imageview_team_2_top);
        imageview_team_1_jungler = findViewById(R.id.imageview_team_1_jungler);
        imageview_team_2_jungler = findViewById(R.id.imageview_team_2_jungler);
        imageview_team_1_midlaner = findViewById(R.id.imageview_team_1_midlaner);
        imageview_team_2_midlaner = findViewById(R.id.imageview_team_2_midlaner);
        imageview_team_1_adc = findViewById(R.id.imageview_team_1_adc);
        imageview_team_2_adc = findViewById(R.id.imageview_team_2_adc);
        imageview_team_1_supp = findViewById(R.id.imageview_team_1_supp);
        imageview_team_2_supp = findViewById(R.id.imageview_team_2_supp);

        team_1_win = findViewById(R.id.team_1_win);
        team_2_win = findViewById(R.id.team_2_win);
        team_1_loss = findViewById(R.id.team_1_loss);
        team_2_loss = findViewById(R.id.team_2_loss);

        team_1_logo = findViewById(R.id.team_1_logo);
        team_2_logo = findViewById(R.id.team_2_logo);
        team_1_score = findViewById(R.id.team_1_score);
        team_2_score = findViewById(R.id.team_2_score);

        team_1_inibitors = findViewById(R.id.team_1_inibitors);
        team_2_inibitors = findViewById(R.id.team_2_inibitors);

        cv11 = findViewById(R.id.cv11);
        cv12 = findViewById(R.id.cv12);
        cv13 = findViewById(R.id.cv13);
        cv14 = findViewById(R.id.cv14);

        cv21 = findViewById(R.id.cv21);
        cv22 = findViewById(R.id.cv22);
        cv23 = findViewById(R.id.cv23);
        cv24 = findViewById(R.id.cv24);






    }
}