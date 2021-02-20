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
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RecyclerView_Calendar_Adapter extends RecyclerView.Adapter <RecyclerView_Calendar_Adapter.ViewHolder> {

    private Context context;
    private List<MatchDetails> displayMatchDetailsList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    MatchDetails thisMatch;
    String imageTeamPath;

    public RecyclerView_Calendar_Adapter() {
    }

    public RecyclerView_Calendar_Adapter(Context context, List<MatchDetails> displayMatchDetailsList) {
        this.context = context;
        this.displayMatchDetailsList = displayMatchDetailsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.calendar_recyciclervirew_item2, viewGroup,false);
        thisMatch = new MatchDetails();

        imageTeamPath = context.getFilesDir().getAbsolutePath() + "/images/teams/";

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String team1 = displayMatchDetailsList.get(i).getTeam1();
        String team2 = displayMatchDetailsList.get(i).getTeam2();
        String match_ID = displayMatchDetailsList.get(i).getId();
        String dateTime = displayMatchDetailsList.get(i).getDatetime();
        String firstMatch = displayMatchDetailsList.get(i).getState();

        String match_time = getTimeMinutesFromDatetime(dateTime);
        String match_date = getLocalDateFromDateTime(dateTime);

        Log.d(TAG, "onBindViewHolder: match_ID:"+match_ID);


        thisMatch.setId(match_ID);
        thisMatch.setTeam1(team1);
        thisMatch.setTeam2(team2);
        thisMatch.setDatetime(dateTime);
        thisMatch.setState(firstMatch);

        if (thisMatch.getState().equals("0")){
            viewHolder.textview_match_date.setVisibility(View.GONE);
        }


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


        viewHolder.textview_match_date.setText(match_date);
        viewHolder.textview_match_timer.setText(match_time);
        viewHolder.textview_team1_name.setText(team1);
        viewHolder.textview_team2_name.setText(team2);

    }

    @Override
    public int getItemCount() {
        return displayMatchDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView image_team_1;
        private ImageView image_team_2;
        private TextView textview_match_date;
        private TextView textview_match_timer;
        private TextView textview_team1_name;
        private TextView textview_team2_name;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_team_1 = (ImageView) itemView.findViewById(R.id.item_logo_team1);
            image_team_2 = (ImageView) itemView.findViewById(R.id.item_logo_team2);
            textview_match_date = (TextView) itemView.findViewById(R.id.item_calendar_date);
            textview_match_timer = (TextView) itemView.findViewById(R.id.item_calendar_time);
            textview_team1_name = (TextView) itemView.findViewById(R.id.item_calendar_team1);
            textview_team2_name = (TextView) itemView.findViewById(R.id.item_calendar_team2);

        }



    }

    private String getTimeMinutesFromDatetime(String datetimeInput){
        Log.d(TAG, "getTimeMinutesFromDatetime: datetimeInput: "+datetimeInput);

        String time="";
        String ora = "";
        String minuto = "";
         String[] datetime = datetimeInput.split("T");

        if (datetime.length==2) {
            time = datetime[1];

            String[] time_array = time.split(":");

            if (time_array.length == 3) {
                ora = time_array[0];
                minuto = time_array[1];
            }
        }

        return ora+":"+minuto;
    }

    private String getLocalDateFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTimeeee: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM  ");
        //SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);
        Log.d(TAG, "getLocalDateFromDateTimeeeee: localDatetime: "+localDatetime);

        return localDatetime;
    }


}

