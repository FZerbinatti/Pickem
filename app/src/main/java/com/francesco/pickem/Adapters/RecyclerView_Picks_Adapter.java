package com.francesco.pickem.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Interfaces.RecyclerViewClickListener;
import com.francesco.pickem.Models.DisplayMatch;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclerView_Picks_Adapter extends RecyclerView.Adapter <RecyclerView_Picks_Adapter.ViewHolder> {

    private Context context;
    private List<DisplayMatch> displayMatchDetailsList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    DisplayMatch thisMatch;
    String match_prediction;
    String imageTeamPath;

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

        imageTeamPath = context.getFilesDir().getAbsolutePath() + "/images/teams/";

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
        String year = displayMatchDetailsList.get(i).getYear();
        String dateTime = displayMatchDetailsList.get(i).getDatetime();

        Log.d(TAG, "onBindViewHolder: match_ID:"+match_ID);

        thisMatch.setYear(year);
        thisMatch.setRegion(region);
        thisMatch.setId(match_ID);
        thisMatch.setWinner(match_winner);
        thisMatch.setTeam1(team1);
        thisMatch.setTeam2(team2);
        thisMatch.setDate(match_date);
        thisMatch.setTime(match_time);
        thisMatch.setDatetime(dateTime);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_loading_error);



            if (team1.equals(" ")){
                viewHolder.image_team_1.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                viewHolder.image_team_1.setEnabled(false);
                Glide
                        .with(context)
                        .load(R.mipmap.ic_tbd)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.ic_load)
                        .apply(options)
                        .into(viewHolder.image_team_1);

            }else {

                String local_image =imageTeamPath +team1+".png";

                Glide.with(context)
                        .load(new File(local_image)) // Uri of the picture
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(viewHolder.image_team_1);

            }

            if (team2.equals(" ")){
                viewHolder.image_team_2.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                viewHolder.image_team_2.setEnabled(false);
                Glide
                        .with(context)
                        .load(R.mipmap.ic_tbd)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .apply(options)
                        .into(viewHolder.image_team_2);

            }else {

                String local_image =imageTeamPath +team2+".png";

                Glide.with(context)
                        .load(new File(local_image)) // Uri of the picture
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(viewHolder.image_team_2);


            }










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
                .child(displayMatch.getDatetime())
                .setValue(displayMatch.getPrediction());

    }

    public void getUserPick (DisplayMatch displayMatch, ViewHolder viewHolder){

        String match_winner = displayMatch.getWinner();
        String team1 = displayMatch.getTeam1();
        String team2 = displayMatch.getTeam2();
        //remove all old regions
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("UserPicks")
                .child(displayMatch.getRegion())
                .child(displayMatch.getYear())
                .child(displayMatch.getDatetime());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                match_prediction = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: match_prediction: "+match_prediction);
                displayMatch.setPrediction(match_prediction);
                loadDataWithPrediction(displayMatch, viewHolder);

            }




            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });



    }

    public void loadDataWithPrediction (DisplayMatch displayMatch, ViewHolder viewHolder){

        String prediction= displayMatch.getPrediction();
        String team1 = displayMatch.getTeam1();
        String team2 = displayMatch.getTeam2();
        String winner = displayMatch.getWinner();

        //Log.d(TAG, "loadDataWithPrediction: ID: "+ displayMatch.getId() +" - region: "+ displayMatch.getRegion() +" teams: "+ displayMatch.getTeam1()  +" vs "+ displayMatch.getTeam2()+" prediction: "+ displayMatch.getPrediction()+" winner: "+ displayMatch.getWinner() );

        Boolean elapsed = matchAlreadyElapsedQuestionMark(displayMatch);

        if (elapsed){
            //se il match è passato, disabilita la possibilità di selezione
            viewHolder.image_team_1.setEnabled(false);
            viewHolder.image_team_2.setEnabled(false);

            //se il match è già passato, allora setta il match winner
            if (winner.equals(team1)) {
                viewHolder.icon_prediction_correct_team1.setVisibility(View.VISIBLE);
                viewHolder.icon_prediction_wrong_team2.setVisibility(View.VISIBLE);

            } else if (winner.equals(team2) ) {
                viewHolder.icon_prediction_correct_team2.setVisibility(View.VISIBLE);
                viewHolder.icon_prediction_wrong_team1.setVisibility(View.VISIBLE);
            }
        }else {
            // altrimenti i match si possono ancora scegliere/cambiare
            viewHolder.image_team_1.setEnabled(true);
            viewHolder.image_team_2.setEnabled(true);
        }
        //setta le prediction dell'utente, dove possibile
        if (prediction != null) {
            //se le prediction non sono null, vuol dire che lo user ha fatto la prediction
            if(prediction.equals(team1)){
                viewHolder.opacity_team_1.setVisibility(View.INVISIBLE);
            }else if (prediction.equals(team2)){
                viewHolder.opacity_team_2.setVisibility(View.INVISIBLE);
            }
        }else {
            //se le prediction, sono null, vuol dire che lo user non ha votato nulla, quindi both dark
            viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
            viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
        }
    }

    public boolean matchAlreadyElapsedQuestionMark(DisplayMatch matchDays) {

        //Log.d(TAG, "matchAlreadyElapsedQuestionMark: (matchDays.getDatetime(): "+(matchDays.getDatetime()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date strDate = null;
            try {
                strDate = sdf.parse(matchDays.getDatetime());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            // Log.d(TAG, "selectMatchDay: System.currentTimeMillis(): "+System.currentTimeMillis());;

            long matchTimeMillis = strDate.getTime();

            // Log.d(TAG, "selectMatchDay: matchTimeMillis:"+matchTimeMillis);
            if (System.currentTimeMillis() < matchTimeMillis) {
                return false;
            }else {
                return true;}


    }


}

