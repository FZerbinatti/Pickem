package com.francesco.pickem.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Models.SingleMatch;
import com.francesco.pickem.R;

import java.util.List;

public class RecyclerView_Picks_Adapter extends RecyclerView.Adapter <RecyclerView_Picks_Adapter.ViewHolder> {

    private Context context;
    private List<SingleMatch> singleMatchList;
    private String TAG ="Adapter RecyclerVIew";
    RecyclerViewClickListener clickListener;



    public RecyclerView_Picks_Adapter(Context context, List<SingleMatch> singleMatchList) {
        this.context = context;
        this.singleMatchList = singleMatchList;

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

        String team1LogoURL = (singleMatchList.get(i).getUrlLogoTeam1());
        String team2LogoURL = (singleMatchList.get(i).getUrlLogoTeam2());

        Log.d(TAG, "onBindViewHolder: team1LogoURL: "+team1LogoURL);
        Log.d(TAG, "onBindViewHolder: team2LogoURL: "+team2LogoURL);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.logo_lec)
                .error(R.drawable.logo_lck);

        Glide.with(context).load(team1LogoURL).placeholder(R.drawable.ic_load).apply(options).into(viewHolder.image_team_1);
        Glide.with(context).load(team2LogoURL).placeholder(R.drawable.ic_load).apply(options).into(viewHolder.image_team_2);

    }

    @Override
    public int getItemCount() {
        return singleMatchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView image_team_1;
        private ImageView opacity_team_1;
        private ImageView image_team_2;
        private ImageView opacity_team_2;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_team_1 = (ImageView) itemView.findViewById(R.id.logo_team_1);
            image_team_2 = (ImageView) itemView.findViewById(R.id.logo_team_2);
            opacity_team_1 = (ImageView) itemView.findViewById(R.id.opacity_team_1);
            opacity_team_2 = (ImageView) itemView.findViewById(R.id.opacity_team_2);


            image_team_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opacity_team_2.setVisibility(View.VISIBLE);
                    opacity_team_1.setVisibility(View.INVISIBLE);
                }
            });
            image_team_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opacity_team_1.setVisibility(View.VISIBLE);
                    opacity_team_2.setVisibility(View.INVISIBLE);
                }
            });


        }



    }
}

