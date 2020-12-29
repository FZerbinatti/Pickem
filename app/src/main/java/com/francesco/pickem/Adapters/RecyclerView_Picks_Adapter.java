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
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.R;

import java.util.List;

public class RecyclerView_Picks_Adapter extends RecyclerView.Adapter <RecyclerView_Picks_Adapter.ViewHolder> {

    private Context context;
    private List<MatchDetails> matchDetailsList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;



    public RecyclerView_Picks_Adapter(Context context, List<MatchDetails> matchDetailsList) {
        this.context = context;
        this.matchDetailsList = matchDetailsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.pick_recyciclervirew_item, viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String team1LogoURL = matchDetailsList.get(i).getUrlLogoTeam1();
        String team2LogoURL = matchDetailsList.get(i).getUrlLogoTeam2();
        String match_time = matchDetailsList.get(i).getMatch_time();
        String match_date = matchDetailsList.get(i).getMatch_date();
        Integer match_ID = matchDetailsList.get(i).getMatch_id();
        Integer match_prediction = matchDetailsList.get(i).getMatch_prediction();
        Integer match_winner = matchDetailsList.get(i).getMatch_winner();


        Log.d(TAG, "onBindViewHolder: team1LogoURL: "+team1LogoURL);
        Log.d(TAG, "onBindViewHolder: team2LogoURL: "+team2LogoURL);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.logo_lec)
                .error(R.drawable.logo_lck);

        Glide.with(context).load(team1LogoURL).placeholder(R.drawable.ic_load).apply(options).into(viewHolder.image_team_1);
        Glide.with(context).load(team2LogoURL).placeholder(R.drawable.ic_load).apply(options).into(viewHolder.image_team_2);
        viewHolder.textview_match_timer.setText(match_time);

        // se non ha scelto e il match è concluso

        if(match_winner == -1)  {
            viewHolder.image_team_1.setEnabled(true);
            viewHolder.image_team_2.setEnabled(true);
                if (match_prediction==1){
                    viewHolder.opacity_team_1.setVisibility(View.GONE);
                    viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                    viewHolder.opacity_team_2.setVisibility(View.GONE);
                }
        }else {
            viewHolder.image_team_1.setEnabled(false);
            viewHolder.image_team_2.setEnabled(false);

                if (match_winner ==1 && match_prediction ==1){
                    viewHolder.icon_prediction_correct_team1.setVisibility(View.VISIBLE);
                    viewHolder.opacity_team_1.setVisibility(View.GONE);
                    viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                }else if (match_winner==1 &&  match_prediction ==2){
                    viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                    viewHolder.opacity_team_2.setVisibility(View.GONE);
                    viewHolder.icon_prediction_wrong_team2.setVisibility(View.VISIBLE);
                }else if (match_winner ==2 && match_prediction ==2){
                    viewHolder.icon_prediction_correct_team2.setVisibility(View.VISIBLE);
                    viewHolder.opacity_team_2.setVisibility(View.GONE);
                    viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                }else if (match_winner==2 &&  match_prediction ==1){
                    viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                    viewHolder.opacity_team_1.setVisibility(View.GONE);
                    viewHolder.icon_prediction_wrong_team1.setVisibility(View.VISIBLE);
                }else if (match_prediction==-1){
                    viewHolder.opacity_team_2.setVisibility(View.VISIBLE);
                    viewHolder.opacity_team_1.setVisibility(View.VISIBLE);
                    if (match_winner==1){
                        viewHolder.icon_prediction_wrong_team2.setVisibility(View.VISIBLE);
                    }else if(match_winner ==2){
                        viewHolder.icon_prediction_wrong_team1.setVisibility(View.VISIBLE);

                    }
                }
            }



    }

    @Override
    public int getItemCount() {
        return matchDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView image_team_1;
        private ImageView opacity_team_1;
        private ImageView image_team_2;
        private ImageView opacity_team_2;
        private TextView textview_match_timer;

        private ImageView icon_prediction_correct_team1, icon_prediction_wrong_team1, icon_waiting_for_save_team1;
        private ImageView icon_prediction_correct_team2, icon_prediction_wrong_team2, icon_waiting_for_save_team2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_team_1 = (ImageView) itemView.findViewById(R.id.logo_team_1);
            image_team_2 = (ImageView) itemView.findViewById(R.id.logo_team_2);
            opacity_team_1 = (ImageView) itemView.findViewById(R.id.opacity_team_1);
            opacity_team_2 = (ImageView) itemView.findViewById(R.id.opacity_team_2);
            textview_match_timer = (TextView) itemView.findViewById(R.id.textview_match_timer);

            icon_prediction_correct_team1 = (ImageView) itemView.findViewById(R.id.icon_prediction_correct_team1);
            icon_prediction_wrong_team1 = (ImageView) itemView.findViewById(R.id.icon_prediction_wrong_team1);
            icon_waiting_for_save_team1 = (ImageView) itemView.findViewById(R.id.icon_waiting_for_save_team1);
            icon_prediction_correct_team2 = (ImageView) itemView.findViewById(R.id.icon_prediction_correct_team2);
            icon_prediction_wrong_team2 = (ImageView) itemView.findViewById(R.id.icon_prediction_wrong_team2);
            icon_waiting_for_save_team2 = (ImageView) itemView.findViewById(R.id.icon_waiting_for_save_team2);


            image_team_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opacity_team_2.setVisibility(View.VISIBLE);
                    opacity_team_1.setVisibility(View.INVISIBLE);
                    icon_waiting_for_save_team1.setVisibility(View.VISIBLE);
                    icon_waiting_for_save_team2.setVisibility(View.INVISIBLE);
                }
            });

            image_team_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opacity_team_1.setVisibility(View.VISIBLE);
                    opacity_team_2.setVisibility(View.INVISIBLE);
                    icon_waiting_for_save_team1.setVisibility(View.INVISIBLE);
                    icon_waiting_for_save_team2.setVisibility(View.VISIBLE);
                }
            });


        }



    }
}

