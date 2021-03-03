package com.francesco.pickem.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.francesco.pickem.Activities.EloTracker.EloActivity;
import com.francesco.pickem.Activities.EloTracker.NewTrackEloDay;
import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.Models.StandingTeams;
import com.francesco.pickem.R;

import java.util.ArrayList;

public class StandingsRecyclerViewAdapter extends RecyclerView.Adapter<StandingsRecyclerViewAdapter.ViewHolder> {

     ArrayList<StandingTeams> teamsStandings;
     LayoutInflater mInflater;
     Context context;
     StandingTeams standingTeams;
     static  String TAG = "StandingsRecyclerViewAdapter: ";

    // data is passed into the constructor
    public StandingsRecyclerViewAdapter(Context context, ArrayList<StandingTeams> teamsStandings) {
        this.mInflater = LayoutInflater.from(context);
        this.teamsStandings = teamsStandings;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_standings, parent, false);
        return new ViewHolder(view);
    }



    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        StandingTeams standing =  new StandingTeams(
                teamsStandings.get(position).getPlace(),
                teamsStandings.get(position).getTeam(),
                teamsStandings.get(position).getWins(),
                teamsStandings.get(position).getLosses()
                );

        holder.standings_place.setText(standing.getPlace().toString());
        holder.standings_team.setText(standing.getTeam().toString());

        holder.standings_wins.setText(standing.getWins().toString());
        holder.standings_loss.setText(standing.getLosses().toString());

/*        if(teamsStandings.get(position).getPlace() <= 4){
            ColorStateList colorStateListGreen = ContextCompat.getColorStateList(holder.background_standings.getContext(), R.color.material_green);
            holder.background_standings.setBackgroundTintList(colorStateListGreen);
        } else if (teamsStandings.get(position).getPlace() > 4 && teamsStandings.get(position).getPlace() <= 6){
            ColorStateList colorStateListRed = ContextCompat.getColorStateList(holder.background_standings.getContext(), R.color.material_yellow);
            holder.background_standings.setBackgroundTintList(colorStateListRed);
        }else {
            ColorStateList colorStateListYellow = ContextCompat.getColorStateList(holder.background_standings.getContext(), R.color.material_red);
            holder.background_standings.setBackgroundTintList(colorStateListYellow);
        }*/


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return teamsStandings.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView standings_wins, standings_loss,  standings_place , standings_team ;
        ConstraintLayout background_standings;


        ViewHolder(View itemView) {
            super(itemView);
            standings_wins = itemView.findViewById(R.id.standings_wins);
            standings_loss = itemView.findViewById(R.id.standings_loss);
            standings_place = itemView.findViewById(R.id.standings_place);
            standings_team = itemView.findViewById(R.id.standings_team);
            background_standings = itemView.findViewById(R.id.background_standings);

        }

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return teamsStandings.get(id).getPlace().toString();
    }

}
