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
import com.francesco.pickem.Models.ItemAnalistRecyclerVIew;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RecyclerView_Analyst_Adapter extends RecyclerView.Adapter <RecyclerView_Analyst_Adapter.ViewHolder> {

    private Context context;
    private List<ItemAnalistRecyclerVIew> ItemAnalistsList;
    private String TAG ="Adapter RecyclerView_Analyst_Adapter";
    RecyclerViewClickListener clickListener;
    ItemAnalistRecyclerVIew thisMatch;
    String imageTeamPath;

    public RecyclerView_Analyst_Adapter() {
    }

    public RecyclerView_Analyst_Adapter(Context context, List<ItemAnalistRecyclerVIew> displayMatchDetailsList) {
        this.context = context;
        this.ItemAnalistsList = displayMatchDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.analysts_recyciclervirew_item, viewGroup,false);
        thisMatch = new ItemAnalistRecyclerVIew();

        imageTeamPath = context.getFilesDir().getAbsolutePath() + "/images/teams/";

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_loading_error);

        String team1 = ItemAnalistsList.get(i).getTeam1();
        String dateTime = ItemAnalistsList.get(i).getDatetime();
        String match_time = getTimeMinutesFromDatetime(dateTime);
        String match_date = getLocalDateFromDateTime(dateTime);
        thisMatch.setDatetime(dateTime);
        viewHolder.analysts_item_time.setText(match_time);

        if (team1.isEmpty()){
            Log.d(TAG, "onBindViewHolder: analist prediction: "+ItemAnalistsList.get(i).getPrediction());
            //allora è il pick di un analyst
            String analistName = ItemAnalistsList.get(i).getTeam2();
            String prediction = ItemAnalistsList.get(i).getPrediction();
            Log.d(TAG, "onBindViewHolder: analyst name: "+analistName);
            
            thisMatch.setPrediction(prediction);

            viewHolder.item_logo_team_predicted.setVisibility(View.VISIBLE);
            viewHolder.analysts_item_name.setVisibility(View.VISIBLE);
            viewHolder.item_logo_team1 .setVisibility(View.INVISIBLE);
            viewHolder.item_logo_team2.setVisibility(View.INVISIBLE);
            viewHolder.analysts_item_time.setVisibility(View.GONE);
            viewHolder.analysts_item_vs.setVisibility(View.INVISIBLE);
            viewHolder.analysts_item_team1.setVisibility(View.INVISIBLE);
            viewHolder.analysts_item_team2.setVisibility(View.INVISIBLE);
            viewHolder.line_separator_analistpick.setVisibility(View.INVISIBLE);



            viewHolder.analysts_item_name.setText(analistName);

            String local_image1 =imageTeamPath +prediction+".png";

            Glide.with(context)
                    .load(new File(local_image1)) // Uri of the picture
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(viewHolder.item_logo_team_predicted);


        }else {
            Log.d(TAG, "onBindViewHolder: match display");
            //allora siamo nel display di entrambi i teams

            viewHolder.item_logo_team_predicted.setVisibility(View.INVISIBLE);
            viewHolder.analysts_item_name.setVisibility(View.INVISIBLE);
            viewHolder.item_logo_team1 .setVisibility(View.VISIBLE);
            viewHolder.item_logo_team2.setVisibility(View.VISIBLE);
            viewHolder.analysts_item_time.setVisibility(View.VISIBLE);
            viewHolder.analysts_item_vs.setVisibility(View.VISIBLE);
            viewHolder.analysts_item_team1.setVisibility(View.VISIBLE);
            viewHolder.analysts_item_team2.setVisibility(View.VISIBLE);

            String team2 = ItemAnalistsList.get(i).getTeam2();
            thisMatch.setTeam1(team1);
            thisMatch.setTeam2(team2);

            String local_image1 =imageTeamPath +team1+".png";

            Glide.with(context)
                    .load(new File(local_image1)) // Uri of the picture
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(viewHolder.item_logo_team1);

            String local_image2 =imageTeamPath +team2+".png";

            Glide.with(context)
                    .load(new File(local_image2)) // Uri of the picture
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(viewHolder.item_logo_team2);

            viewHolder.analysts_item_team1.setText(team1);
            viewHolder.analysts_item_team2.setText(team2);


        }


    }

    @Override
    public int getItemCount() {
        return ItemAnalistsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView item_logo_team1;
        private ImageView item_logo_team2;
        private TextView analysts_item_time;
        private TextView analysts_item_vs;
        private TextView analysts_item_team1;
        private TextView analysts_item_team2;
        private ImageView item_logo_team_predicted;
        private TextView analysts_item_name;
        private View line_separator_analistpick;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            item_logo_team1 = (ImageView) itemView.findViewById(R.id.item_logo_team1);
            item_logo_team2 = (ImageView) itemView.findViewById(R.id.item_logo_team2);
            analysts_item_time = (TextView) itemView.findViewById(R.id.analysts_item_time);
            analysts_item_vs = (TextView) itemView.findViewById(R.id.analysts_item_vs);
            analysts_item_team1 = (TextView) itemView.findViewById(R.id.analysts_item_team1);
            analysts_item_team2 = (TextView) itemView.findViewById(R.id.analysts_item_team2);

            item_logo_team_predicted = (ImageView) itemView.findViewById(R.id.item_logo_team_predicted);
            analysts_item_name = (TextView) itemView.findViewById(R.id.analysts_item_name);
            line_separator_analistpick = (View) itemView.findViewById(R.id.line_separator_analistpick);


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

