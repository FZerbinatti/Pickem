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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Interfaces.RecyclerViewClickListener;
import com.francesco.pickem.Models.RegionStats;
import com.francesco.pickem.R;

import java.io.File;
import java.util.List;

public class RecyclerView_Statistics_Adapter extends RecyclerView.Adapter <RecyclerView_Statistics_Adapter.ViewHolder> {

    private Context context;
    private List<RegionStats> regionsCorrectsTotalsPicksList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    RegionStats thisRegionStats;
    String imageTeamPath;

    public RecyclerView_Statistics_Adapter() {
    }

    public RecyclerView_Statistics_Adapter(Context context, List<RegionStats> regionsCorrectsTotalsPicksList) {
        this.context = context;
        this.regionsCorrectsTotalsPicksList = regionsCorrectsTotalsPicksList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.statistics_recyciclervirew_item, viewGroup,false);
        thisRegionStats = new RegionStats();

        imageTeamPath = context.getFilesDir().getAbsolutePath() + "/images/regions/";

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String regionName = regionsCorrectsTotalsPicksList.get(i).getRegionName();
        Integer correctPicks = regionsCorrectsTotalsPicksList.get(i).getCorrectPicks();
        Integer totalPicks = regionsCorrectsTotalsPicksList.get(i).getTotalPicks();
        float percentage =0;
        if (correctPicks>0){
            percentage = (correctPicks/totalPicks)*100;
        }

        Log.d(TAG, "onBindViewHolder: regionName: "+regionName +" picks: "+correctPicks+"/"+totalPicks +" -> " +percentage+"%");

        thisRegionStats.setCorrectPicks(correctPicks);
        thisRegionStats.setTotalPicks(totalPicks);
        thisRegionStats.setRegionName(regionName);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.ic_loading_error);


        String local_image =imageTeamPath +regionName+".png";

        Glide.with(context)
                .load(new File(local_image)) // Uri of the picture
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(viewHolder.region_image);


        viewHolder.textview_correctPicks.setText(correctPicks.toString()+"/");
        viewHolder.textview_percentage.setText(String.valueOf(percentage)+"%");
        viewHolder.statistic_item_totals.setText(totalPicks.toString());
        viewHolder.textview_region_name.setText(regionName);

    }

    @Override
    public int getItemCount() {
        return regionsCorrectsTotalsPicksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView region_image;
        private TextView textview_region_name;
        private TextView textview_correctPicks;
        private TextView statistic_item_totals;
        private TextView textview_percentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            region_image = (ImageView) itemView.findViewById(R.id.statistic_item_region_icon);
            textview_region_name = (TextView) itemView.findViewById(R.id.statistic_item_region_name);
            textview_correctPicks = (TextView) itemView.findViewById(R.id.statistic_item_correct);
            statistic_item_totals = (TextView) itemView.findViewById(R.id.statistic_item_totals);
            textview_percentage = (TextView) itemView.findViewById(R.id.statistic_item_percentage);
        }


    }


}

