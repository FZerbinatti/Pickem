package com.dreamsphere.pickem.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dreamsphere.pickem.Activities.IndipendentActivities.MatchView;
import com.dreamsphere.pickem.Interfaces.RecyclerViewClickListener;
import com.dreamsphere.pickem.Models.GlobalMatchStatsSimplified;
import com.dreamsphere.pickem.Models.MatchSingleTeamStats;
import com.dreamsphere.pickem.R;
import com.dreamsphere.pickem.Services.DatabaseHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class RecyclerView_ChooseGame_Adapter extends RecyclerView.Adapter <RecyclerView_ChooseGame_Adapter.ViewHolder> {

    private Context context;
    private List<GlobalMatchStatsSimplified> displayMatchDetailsList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    ItemClickListener mClickListener;
    GlobalMatchStatsSimplified thisMatch;
    String match_prediction;
    String imageTeamPath;
    DatabaseHelper databaseHelper;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference teamReference = storage.getReference("team_img");

    public RecyclerView_ChooseGame_Adapter() {
    }

    public RecyclerView_ChooseGame_Adapter(Context context, List<GlobalMatchStatsSimplified> displayMatchDetailsList) {
        this.context = context;
        this.displayMatchDetailsList = displayMatchDetailsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item_choose_game, viewGroup,false);
        thisMatch = new GlobalMatchStatsSimplified();

        imageTeamPath = context.getFilesDir().getAbsolutePath() + "/images/teams/";

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        MatchSingleTeamStats team1 = displayMatchDetailsList.get(i).getTeam1();
        MatchSingleTeamStats team2 = displayMatchDetailsList.get(i).getTeam2();
        String match_time = displayMatchDetailsList.get(i).getEnded();
        String match_ID = displayMatchDetailsList.get(i).getMatch_id();
        String region = displayMatchDetailsList.get(i).getRegion();
        String year = displayMatchDetailsList.get(i).getYear();
        String dateTime = displayMatchDetailsList.get(i).getDatetime();


        Log.d(TAG, "onBindViewHolder: match_ID:"+match_ID +" datetime: "+dateTime);

        thisMatch.setYear(year);
        thisMatch.setRegion(region);
        thisMatch.setMatch_id(match_ID);
        thisMatch.setTeam1(team1);
        thisMatch.setTeam2(team2);
        thisMatch.setDatetime(dateTime);
        thisMatch.setEnded(match_time);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .error(R.drawable.ic_loading_error);

                //String local_image1 =imageTeamPath +team1.getTeamCode()+".png";




        teamReference.child(team1.getTeamCode().replace(" ", "")+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context)
                        .load(uri) // Uri of the picture
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(viewHolder.image_team_1);

            }
        });

               // String local_image2 =imageTeamPath +team2.getTeamCode()+".png";



        teamReference.child(team2.getTeamCode().replace(" ", "")+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context)
                        .load(uri) // Uri of the picture
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(viewHolder.image_team_2);

            }
        });




        viewHolder.textview_match_timer.setText(match_time);
        viewHolder.textview_team1_score.setText(team1.getTotalKDA().replace("a","/"));
        viewHolder.textview_team2_score.setText(team2.getTotalKDA().replace("a","/"));

    }

    @Override
    public int getItemCount() {
        return displayMatchDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView image_team_1;
        private ImageView image_team_2;
        private TextView textview_match_timer;
        private TextView textview_team1_score;
        private TextView textview_team2_score;
        private ConstraintLayout cc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_team_1 = (ImageView) itemView.findViewById(R.id.choose_item_logo_team1);
            image_team_2 = (ImageView) itemView.findViewById(R.id.choose_item_logo_team2);

            textview_match_timer = (TextView) itemView.findViewById(R.id.choose_match_timer);
            textview_team1_score = (TextView) itemView.findViewById(R.id.choose_item_score_team1);
            textview_team2_score = (TextView) itemView.findViewById(R.id.choose_item_score_team2);
            cc  = (ConstraintLayout) itemView.findViewById(R.id.choose_cc);



            cc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, MatchView.class);
                    intent.putExtra( "GAME_NUMBER", String.valueOf(getAdapterPosition()+1) );
                    intent.putExtra( "MATCH_ID", displayMatchDetailsList.get(getAdapterPosition()).getDatetime() );
                    intent.putExtra( "REGION", displayMatchDetailsList.get(getAdapterPosition()).getRegion() );
                    intent.putExtra( "WINNER", displayMatchDetailsList.get(getAdapterPosition()).getWinner() );
                    intent.putExtra( "TEAM1", displayMatchDetailsList.get(getAdapterPosition()).getTeam1().getTeamCode() );
                    intent.putExtra( "TEAM2", displayMatchDetailsList.get(getAdapterPosition()).getTeam2().getTeamCode() );

                    Log.d(TAG, "onCreate: matchID" + displayMatchDetailsList.get(getAdapterPosition()).getMatch_id() + " region: " + displayMatchDetailsList.get(getAdapterPosition()).getRegion()+ " winner: " +  displayMatchDetailsList.get(getAdapterPosition()).getWinner() +" position: "+getAdapterPosition());

                    context.startActivity(intent);

                }
            });


        }



    }


    // allows clicks events to be caught
    void setClickListener(RecyclerView_ChooseGame_Adapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}

