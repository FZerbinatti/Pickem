package com.francesco.pickem.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Models.DisplayMatch;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RecyclerView_Picks_Adapter extends RecyclerView.Adapter <RecyclerView_Picks_Adapter.ViewHolder> {

    private Context context;
    private List<DisplayMatch> displayMatchDetailsList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    DisplayMatch thisMatch;

    public RecyclerView_Picks_Adapter() {


    }

    public RecyclerView_Picks_Adapter(Context context, List<DisplayMatch> displayMatchDetailsList) {
        this.context = context;
        this.displayMatchDetailsList = displayMatchDetailsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.pick_recyciclervirew_item, viewGroup,false);
        thisMatch = new DisplayMatch();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {



        String team1 = displayMatchDetailsList.get(i).getTeam1();
        String team2 = displayMatchDetailsList.get(i).getTeam2();
        String match_time = displayMatchDetailsList.get(i).getTime();
        String match_date = displayMatchDetailsList.get(i).getDate();
        String match_ID = displayMatchDetailsList.get(i).getId();
        String match_prediction = displayMatchDetailsList.get(i).getPrediction();
        String match_winner = displayMatchDetailsList.get(i).getWinner();
        String region = displayMatchDetailsList.get(i).getRegion();
        String split = displayMatchDetailsList.get(i).getSplit();
        String year = displayMatchDetailsList.get(i).getYear();

        Log.d(TAG, "onBindViewHolder: match_ID:"+match_ID);

        thisMatch.setSplit(split);
        thisMatch.setYear(year);
        thisMatch.setRegion(region);
        thisMatch.setId(match_ID);
        thisMatch.setWinner(match_winner);
        thisMatch.setTeam1(team1);
        thisMatch.setTeam2(team2);
        thisMatch.setDate(match_date);
        thisMatch.setTime(match_time);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_loading_error);

        Log.d(TAG, "onBindViewHolder: region:"+region);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teams")
                .child(region);

                reference.child(team1).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                String teamImage = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: teamImage1:"+teamImage);
                if (teamImage!= null){
                    Glide.with(context).load(teamImage).placeholder(R.drawable.ic_load).apply(options).into(viewHolder.image_team_1);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }

        });

        reference.child(team2).child("image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                String teamImage = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: teamImage2:"+teamImage);
                if (teamImage!= null){
                    Glide.with(context).load(teamImage).placeholder(R.drawable.ic_load).apply(options).into(viewHolder.image_team_2);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }

        });


        viewHolder.textview_match_timer.setText(match_time);

        // se non ha scelto e il match è concluso
        getUserPick(thisMatch, viewHolder);

    }

    @Override
    public int getItemCount() {
        return displayMatchDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView image_team_1;
        private ImageView opacity_team_1;
        private ImageView image_team_2;
        private ImageView opacity_team_2;
        private TextView textview_match_timer;

        private ImageView icon_prediction_correct_team1, icon_prediction_wrong_team1;
        private ImageView icon_prediction_correct_team2, icon_prediction_wrong_team2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_team_1 = (ImageView) itemView.findViewById(R.id.logo_team_1);
            image_team_2 = (ImageView) itemView.findViewById(R.id.logo_team_2);
            opacity_team_1 = (ImageView) itemView.findViewById(R.id.opacity_team_1);
            opacity_team_2 = (ImageView) itemView.findViewById(R.id.opacity_team_2);
            textview_match_timer = (TextView) itemView.findViewById(R.id.textview_match_timer);

            icon_prediction_correct_team1 = (ImageView) itemView.findViewById(R.id.icon_prediction_correct_team1);
            icon_prediction_wrong_team1 = (ImageView) itemView.findViewById(R.id.icon_prediction_wrong_team1);
            icon_prediction_correct_team2 = (ImageView) itemView.findViewById(R.id.icon_prediction_correct_team2);
            icon_prediction_wrong_team2 = (ImageView) itemView.findViewById(R.id.icon_prediction_wrong_team2);



            image_team_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DisplayMatch displayMatchClicked = displayMatchDetailsList.get(getAdapterPosition());
                    displayMatchClicked.setPrediction(displayMatchDetailsList.get(getAdapterPosition()).getTeam1());

                    updateUserPick(displayMatchClicked);
                    opacity_team_2.setVisibility(View.VISIBLE);
                    opacity_team_1.setVisibility(View.INVISIBLE);

                }
            });

            image_team_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DisplayMatch displayMatchClicked = displayMatchDetailsList.get(getAdapterPosition());
                    displayMatchClicked.setPrediction(displayMatchDetailsList.get(getAdapterPosition()).getTeam2());

                    updateUserPick(displayMatchClicked);
                    opacity_team_1.setVisibility(View.VISIBLE);
                    opacity_team_2.setVisibility(View.INVISIBLE);

                }
            });


        }



    }

    public void updateUserPick (DisplayMatch displayMatch){

        Log.d(TAG, "updateUserPick: "+thisMatch.getId());

        Log.d(TAG, "updateUserPick: "+displayMatch.getId());
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("UserPicks")
                .child(displayMatch.getRegion())
                .child(displayMatch.getYear())
                .child(displayMatch.getSplit())
                .child(displayMatch.getId())
                .setValue(displayMatch.getPrediction());

    }

    public String getUserPick (DisplayMatch displayMatch, ViewHolder viewHolder){

        String match_winner = displayMatch.getWinner();
        String team1 = displayMatch.getTeam1();
        String team2 = displayMatch.getTeam2();
        //remove all old regions
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("UserPicks")
                .child(displayMatch.getRegion())
                .child(displayMatch.getYear())
                .child(displayMatch.getSplit())
                .child(displayMatch.getId());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                String match_prediction = dataSnapshot.getValue(String.class);
                if (match_prediction != null){

                    if (match_prediction.equals("")||match_prediction.equals("null")){
                        viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                        viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                    }else {

                        if(match_winner.equals("null"))  {
                            viewHolder.image_team_1.setEnabled(true);
                            viewHolder.image_team_2.setEnabled(true);
                            if (match_prediction.equals(team1)){
                                viewHolder.opacity_team_1.setVisibility(View.GONE);
                                viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                            }else {
                                viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                                viewHolder.opacity_team_2.setVisibility(View.GONE);
                            }
                        }else {
                            viewHolder.image_team_1.setEnabled(false);
                            viewHolder.image_team_2.setEnabled(false);

                            if (match_winner.equals(team1) && match_prediction.equals(team1)){
                                viewHolder.icon_prediction_correct_team1.setVisibility(View.VISIBLE);
                                viewHolder.opacity_team_1.setVisibility(View.GONE);
                                viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                            }else if (match_winner.equals(team1) &&  match_prediction.equals(team2)){
                                viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                                viewHolder.opacity_team_2.setVisibility(View.GONE);
                                viewHolder.icon_prediction_wrong_team2.setVisibility(View.VISIBLE);
                            }else if (match_winner.equals(team2) && match_prediction.equals(team2)){
                                viewHolder.icon_prediction_correct_team2.setVisibility(View.VISIBLE);
                                viewHolder.opacity_team_2.setVisibility(View.GONE);
                                viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                            }else if (match_winner.equals(team2) &&  match_prediction.equals(team1)){
                                viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                                viewHolder.opacity_team_1.setVisibility(View.GONE);
                                viewHolder.icon_prediction_wrong_team1.setVisibility(View.VISIBLE);
                            }else if (match_prediction.equals("null")){
                                viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                                viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                                if (match_winner.equals(team1)){
                                    viewHolder.icon_prediction_wrong_team2.setVisibility(View.VISIBLE);
                                }else if(match_winner.equals(team2)){
                                    viewHolder.icon_prediction_wrong_team1.setVisibility(View.VISIBLE);
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

        return "";

    }


}

