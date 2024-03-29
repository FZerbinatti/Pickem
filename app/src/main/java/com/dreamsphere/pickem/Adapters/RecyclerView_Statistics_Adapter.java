package com.dreamsphere.pickem.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dreamsphere.pickem.Interfaces.RecyclerViewClickListener;
import com.dreamsphere.pickem.Models.RegionStats;
import com.dreamsphere.pickem.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class RecyclerView_Statistics_Adapter extends RecyclerView.Adapter <RecyclerView_Statistics_Adapter.ViewHolder> {

    private Context context;
    private List<RegionStats> regionsCorrectsTotalsPicksList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;
    RegionStats thisRegionStats;
    String imageTeamPath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference regionReference = storage.getReference("region_img");

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
        float percentage =0f;
        BigDecimal bd = BigDecimal.valueOf(0);
        if (correctPicks>0){

            percentage =  ( (float) correctPicks/totalPicks)*100f;
            bd = new BigDecimal(Float.toString(percentage));
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

        }



        Log.d(TAG, "onBindViewHolder: regionName: "+regionName +" picks: "+correctPicks+"/"+totalPicks +" -> " +percentage+"%" + " "+bd);

        thisRegionStats.setCorrectPicks(correctPicks);
        thisRegionStats.setTotalPicks(totalPicks);
        thisRegionStats.setRegionName(regionName);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .error(R.drawable.ic_loading_error);


        //String local_image =imageTeamPath +regionName+".png";

        regionReference.child(regionName.replace(" ", "")+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context)
                        .load(uri) // Uri of the picture
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(viewHolder.region_image);

            }
        });



        viewHolder.progressbar_statistics_item.setProgress(Math.round(percentage));
        ColorStateList colorStateList = ContextCompat.getColorStateList(viewHolder.progressbar_statistics_item.getContext(), progressBarColor(Math.round(percentage)));
        viewHolder.progressbar_statistics_item.setProgressTintList(colorStateList);

        viewHolder.textview_correctPicks.setText(correctPicks.toString()+"/");
        viewHolder.textview_percentage.setText(String.valueOf(bd)+"%");
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
        private ProgressBar progressbar_statistics_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            region_image = (ImageView) itemView.findViewById(R.id.statistic_item_region_icon);
            textview_region_name = (TextView) itemView.findViewById(R.id.statistic_item_region_name);
            textview_correctPicks = (TextView) itemView.findViewById(R.id.statistic_item_correct);
            statistic_item_totals = (TextView) itemView.findViewById(R.id.statistic_item_totals);
            textview_percentage = (TextView) itemView.findViewById(R.id.statistic_item_percentage);
            progressbar_statistics_item = (ProgressBar) itemView.findViewById(R.id.progressbar_statistics_item);
        }


    }

    private Integer progressBarColor(Integer potenza_segnale){

        if (potenza_segnale < 20 ){
            return (R.color.r1);
        }else if (potenza_segnale >= 20 && potenza_segnale <40){
            return (R.color.r2);
        }else if (potenza_segnale >= 40 && potenza_segnale <55){
            return (R.color.y1);
        } else if (potenza_segnale >= 55 && potenza_segnale <70){
            return (R.color.y2);
        }else if (potenza_segnale >= 70 && potenza_segnale <80){
            return (R.color.g1);
        }else if (potenza_segnale >= 80 && potenza_segnale <90){
            return (R.color.g2);
        }else if (potenza_segnale >=90){
            return (R.color.g3);
        }else return R.color.transparent;
    }


}

