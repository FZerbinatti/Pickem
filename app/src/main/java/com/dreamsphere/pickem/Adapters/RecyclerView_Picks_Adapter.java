package com.dreamsphere.pickem.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
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
import com.dreamsphere.pickem.Activities.IndipendentActivities.ActivityChooseGame;
import com.dreamsphere.pickem.Activities.IndipendentActivities.MatchView;
import com.dreamsphere.pickem.Interfaces.RecyclerViewClickListener;
import com.dreamsphere.pickem.Models.DisplayMatch;
import com.dreamsphere.pickem.R;
import com.dreamsphere.pickem.Services.DatabaseHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RecyclerView_Picks_Adapter extends RecyclerView.Adapter <RecyclerView_Picks_Adapter.ViewHolder> {

    private Context context;
    private List<DisplayMatch> displayMatchDetailsList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    ItemClickListener mClickListener;
    DisplayMatch thisMatch;
    String match_prediction;
    String imageTeamPath;
    DatabaseHelper databaseHelper;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference teamReference = storage.getReference("team_img");


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
        String dateTime = (( displayMatchDetailsList.get(i).getDatetime()));
        Long team1_score = displayMatchDetailsList.get(i).getTeam1_score();
        Long team2_score = displayMatchDetailsList.get(i).getTeam2_score();
        String team1Image = displayMatchDetailsList.get(i).getUrlLogoteam1();
        String team2Image = displayMatchDetailsList.get(i).getUrlLogoteam2();

        databaseHelper= new DatabaseHelper(context);

        //Log.d(TAG, "onBindViewHolder: //////////////////////////////// match_ID:"+match_ID +" datetime: "+dateTime + " winner: " + match_winner);
        //se team1 sul SQLite è vuoto riempilo
/*        if (!databaseHelper.teamsInsertedForRegionDateTime(region, dateTime)){
            databaseHelper.insertMatchTeams(region, dateTime, team1, team2);
        }*/

        thisMatch.setYear(year);
        thisMatch.setRegion(region);
        thisMatch.setId(match_ID);
        thisMatch.setWinner(match_winner);
        thisMatch.setTeam1(team1);
        thisMatch.setTeam2(team2);
        thisMatch.setDate(match_date);
        thisMatch.setTime(match_time);
        thisMatch.setDatetime(dateTime);
        thisMatch.setTeam1_score(team1_score);
        thisMatch.setTeam2_score(team2_score);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .error(R.drawable.ic_loading_error)
                ;



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

                //String local_image =imageTeamPath +team1+".png";

                teamReference.child(thisMatch.getTeam1().replace(" ", "")+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(context)
                                //.load(new File(local_image)) // Uri of the picture
                                .load(uri)
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade(200))
                                .into(viewHolder.image_team_1);

                    }
                });



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

                //String local_image =imageTeamPath +team2+".png";


                teamReference.child(thisMatch.getTeam2().replace(" ", "")+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(context)
                                //.load(new File(local_image)) // Uri of the picture
                                .load(uri)
                                .apply(options)
                                .transition(DrawableTransitionOptions.withCrossFade(200))
                                .into(viewHolder.image_team_2);

                    }
                });




            }

/*        if (!match_winner.trim().isEmpty())    {
            Log.d(TAG, "onBindViewHolder: "+region+" "+dateTime+" "+match_winner);
            databaseHelper.updateWinner(region, dateTime, match_winner);
        }*/
        Log.d(TAG, "onBindViewHolder: current:"+System.currentTimeMillis());
        Log.d(TAG, "onBindViewHolder: match:"+datetimeToMillis(dateTime));
        Long sum = datetimeToMillis(dateTime) + 1000*60*60;
        Log.d(TAG, "onBindViewHolder: current+1h= "+sum);

        if (match_winner.trim().isEmpty() && System.currentTimeMillis()>datetimeToMillis(dateTime) && System.currentTimeMillis()< sum){
            Log.d(TAG, "onBindViewHolder: condition true");
            viewHolder.match_live.setVisibility(View.VISIBLE);
        }else {
            Log.d(TAG, "onBindViewHolder: contidion false");
            viewHolder.match_live.setVisibility(View.INVISIBLE);

        }

        if (!match_winner.trim().isEmpty() ){
            viewHolder.item_vs.setVisibility(View.INVISIBLE);
            viewHolder.item_info.setVisibility(View.VISIBLE);
            viewHolder.item_info.setEnabled(true);


        }else {
            viewHolder.item_info.setEnabled(false);
        }


        viewHolder.textview_match_timer.setText(match_time);
        viewHolder.textview_team1_score.setText(team1_score.toString());
        viewHolder.textview_team2_score.setText(team2_score.toString());

        // se non ha scelto e il match è concluso
        getUserPick(thisMatch, viewHolder);

        Boolean elapsed = matchAlreadyElapsedQuestionMark(thisMatch);
        Log.d(TAG, "loadDataWithPrediction: match elapsed? " +elapsed);

        if (elapsed){
            //se il match è passato, disabilita la possibilità di selezione
            viewHolder.image_team_1.setEnabled(false);
            viewHolder.image_team_2.setEnabled(false);

            //se il match è già passato, allora setta il match winner
            if (match_winner.equals(team1)) {
                viewHolder.icon_prediction_correct_team1.setVisibility(View.VISIBLE);
                viewHolder.icon_prediction_wrong_team2.setVisibility(View.VISIBLE);

            } else if (match_winner.equals(team2) ) {
                viewHolder.icon_prediction_correct_team2.setVisibility(View.VISIBLE);
                viewHolder.icon_prediction_wrong_team1.setVisibility(View.VISIBLE);
            }
        }else {
            // altrimenti i match si possono ancora scegliere/cambiare
            viewHolder.image_team_1.setEnabled(true);
            viewHolder.image_team_2.setEnabled(true);
        }

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
        private TextView textview_team1_score;
        private TextView textview_team2_score;
        private TextView match_live;

        private ImageView item_vs;
        private ImageView item_info;

        private TextView icon_prediction_correct_team1, icon_prediction_wrong_team1;
        private TextView icon_prediction_correct_team2, icon_prediction_wrong_team2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_team_1 = (ImageView) itemView.findViewById(R.id.item_logo_team1);
            image_team_2 = (ImageView) itemView.findViewById(R.id.item_logo_team2);
            opacity_team_1 = (ImageView) itemView.findViewById(R.id.opacity_team_1);
            opacity_team_2 = (ImageView) itemView.findViewById(R.id.opacity_team_2);
            textview_match_timer = (TextView) itemView.findViewById(R.id.textview_match_timer);
            textview_team1_score = (TextView) itemView.findViewById(R.id.item_score_team1);
            textview_team2_score = (TextView) itemView.findViewById(R.id.item_score_team2);
            match_live = (TextView) itemView.findViewById(R.id.match_live);

            item_vs = (ImageView) itemView.findViewById(R.id.item_vs);
            item_info = (ImageView) itemView.findViewById(R.id.item_info);

            icon_prediction_correct_team1 = (TextView) itemView.findViewById(R.id.icon_prediction_correct_team1);
            icon_prediction_wrong_team1 = (TextView) itemView.findViewById(R.id.icon_prediction_wrong_team1);
            icon_prediction_correct_team2 = (TextView) itemView.findViewById(R.id.icon_prediction_correct_team2);
            icon_prediction_wrong_team2 = (TextView) itemView.findViewById(R.id.icon_prediction_wrong_team2);


            image_team_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DisplayMatch displayMatchClicked = displayMatchDetailsList.get(getAdapterPosition());
                    displayMatchClicked.setPrediction(displayMatchDetailsList.get(getAdapterPosition()).getTeam1());
                    displayMatchClicked.setDatetime(displayMatchDetailsList.get(getAdapterPosition()).getDatetime());

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
                    displayMatchClicked.setDatetime(displayMatchDetailsList.get(getAdapterPosition()).getDatetime());

                    updateUserPick(displayMatchClicked);
                    opacity_team_1.setVisibility(View.VISIBLE);
                    opacity_team_2.setVisibility(View.INVISIBLE);

                }
            });

            item_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String datetime = displayMatchDetailsList.get(getAdapterPosition()).getDatetime();

                    String[] array = datetime.split("-");
                    String year = array[0];

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_statistics))
                            .child(displayMatchDetailsList.get(getAdapterPosition()).getRegion())
                            .child(displayMatchDetailsList.get(getAdapterPosition()).getRegion()+year)
                            .child(context.getString(R.string.firebase_matches_stats))
                            .child(datetime);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer children = (int) snapshot.getChildrenCount();
                           // Log.d(TAG, "onDataChange: childreN : " +children);
                            if (children==1){
                                Intent intent = new Intent(context, MatchView.class);
                                intent.putExtra( "GAME_NUMBER", "1" );
                                intent.putExtra( "MATCH_ID", datetime );
                                intent.putExtra( "REGION", displayMatchDetailsList.get(getAdapterPosition()).getRegion() );
                                intent.putExtra( "WINNER", displayMatchDetailsList.get(getAdapterPosition()).getWinner() );
                                intent.putExtra( "TEAM1", displayMatchDetailsList.get(getAdapterPosition()).getTeam1() );
                                intent.putExtra( "TEAM2", displayMatchDetailsList.get(getAdapterPosition()).getTeam2() );

                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ActivityChooseGame.class);
                                intent.putExtra( "MATCH_BO1", 0 );
                                intent.putExtra( "MATCH_ID", datetime );
                                intent.putExtra( "REGION", displayMatchDetailsList.get(getAdapterPosition()).getRegion() );
                                intent.putExtra( "YEAR", year );
                                context.startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                }
            });


        }



    }

    public void updateUserPick (DisplayMatch displayMatch){

        
        databaseHelper = new DatabaseHelper(context);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("UserPicks")
                .child(displayMatch.getRegion())
                .child(displayMatch.getYear())
                .child(displayMatch.getDatetime())
                .setValue(displayMatch.getPrediction());


        databaseHelper.updatePrediction(displayMatch.getRegion(), displayMatch.getDatetime(), displayMatch.getPrediction());

    }

    public void getUserPick (DisplayMatch displayMatch, ViewHolder viewHolder){


        String match_winner = displayMatch.getWinner();
        Log.d(TAG, "getUserPick: winner? "+match_winner);
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
                //Log.d(TAG, "onDataChange: //// "+match_prediction);

                if (match_prediction != null) {

                    displayMatch.setPrediction(match_prediction);
                    loadDataWithPrediction(displayMatch, viewHolder);

                    //databaseHelper= new DatabaseHelper(context);

                    //databaseHelper.updatePrediction(displayMatch.getRegion(), displayMatch.getDatetime(), match_prediction);
                }


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
                strDate = sdf.parse(convertDatetimeZtoLocale(matchDays.getDatetime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            // Log.d(TAG, "selectMatchDay: System.currentTimeMillis(): "+System.currentTimeMillis());;

            long matchTimeMillis = strDate.getTime();

            // Log.d(TAG, "selectMatchDay: matchTimeMillis:"+matchTimeMillis);
            if (System.currentTimeMillis() < matchTimeMillis) {
                return false;
            }else {
                return true;
            }


    }

    public Long datetimeToMillis(String datetime) {
        //in base agli ID dell'array list, trova la data sucessiva o coincidente a quella attuale
        Integer itemPosition=0;
            // Log.d(TAG, "loadMatchDays: "+matchDays.get(i));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date strDate = null;

            try {
                strDate = sdf.parse(convertDatetimeZtoLocale(datetime));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            long matchTimeMillis = strDate.getTime();
        //Log.d(TAG, "datetimeToMillis: "+matchTimeMillis);
        return matchTimeMillis;
    }

    // allows clicks events to be caught
    void setClickListener(RecyclerView_Picks_Adapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public String convertDatetimeZtoLocale(String datetime){
        //Log.d(TAG, "convertDatetimeZtoLocale: eating this: "+datetime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date strDate = null;

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);

        //Log.d(TAG, "convertDatetimeZtoLocale: shitting this: "+formattedDate.toString());

        return formattedDate.toString();
    }


}

