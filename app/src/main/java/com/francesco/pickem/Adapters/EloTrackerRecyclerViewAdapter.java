package com.francesco.pickem.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.francesco.pickem.Activities.EloTracker.NewTrackEloDay;
import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.R;

import java.util.ArrayList;

public class EloTrackerRecyclerViewAdapter extends RecyclerView.Adapter<EloTrackerRecyclerViewAdapter.ViewHolder> {

     ArrayList<EloTracker> eloTrackerDays;
     LayoutInflater mInflater;
     ItemClickListener mClickListener;
     Context context;
     static  String TAG = "EloTrackerRecyclerViewAdapter: ";

    // data is passed into the constructor
    public EloTrackerRecyclerViewAdapter(Context context, ArrayList<EloTracker> eloData) {
        this.mInflater = LayoutInflater.from(context);
        this.eloTrackerDays = eloData;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_manual_elo_tracker, parent, false);
        return new ViewHolder(view);
    }



    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Integer elo_wins = eloTrackerDays.get(position).getWins();
        Integer elo_losses = eloTrackerDays.get(position).getLosses();

        EloTracker eloData =  new EloTracker(
                eloTrackerDays.get(position).getID(),
                eloTrackerDays.get(position).getDate(),
                elo_wins,
                elo_losses,
                eloTrackerDays.get(position).getElo(),
                eloTrackerDays.get(position).getLps()
                );

        holder.item_elotracker_date.setText(eloData.getDate().toString());
        holder.item_elotracker_wins.setText(eloData.getWins().toString());
        holder.item_elotracker_losses.setText(eloData.getLosses().toString());
        holder.item_elotracker_elo.setText(eloData.getElo().toString());
        holder.item_elotracker_lps.setText(eloData.getLps().toString());


        if(elo_wins > elo_losses){
            ColorStateList colorStateListGreen = ContextCompat.getColorStateList(holder.background_item_elotracker.getContext(), R.color.material_green);
            holder.background_item_elotracker.setBackgroundTintList(colorStateListGreen);
        } else if (elo_losses > elo_wins){
            ColorStateList colorStateListRed = ContextCompat.getColorStateList(holder.background_item_elotracker.getContext(), R.color.material_red);
            holder.background_item_elotracker.setBackgroundTintList(colorStateListRed);
        }else {
            ColorStateList colorStateListYellow = ContextCompat.getColorStateList(holder.background_item_elotracker.getContext(), R.color.material_yellow);
            holder.background_item_elotracker.setBackgroundTintList(colorStateListYellow);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return eloTrackerDays.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView item_elotracker_date, item_elotracker_wins, item_elotracker_losses,  item_elotracker_elo , item_elotracker_lps ;
        ConstraintLayout background_item_elotracker;


        ViewHolder(View itemView) {
            super(itemView);
            item_elotracker_date = itemView.findViewById(R.id.item_elotracker_date);
            item_elotracker_wins = itemView.findViewById(R.id.item_elotracker_wins);
            item_elotracker_losses = itemView.findViewById(R.id.item_elotracker_losses);
            item_elotracker_elo = itemView.findViewById(R.id.item_elotracker_elo);
            item_elotracker_lps = itemView.findViewById(R.id.item_elotracker_lps);
            background_item_elotracker = itemView.findViewById(R.id.background_item_elotracker);

            //itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();
                    if (pos !=  RecyclerView.NO_POSITION){
                        EloTracker eloTracker = eloTrackerDays.get(pos);
                        Intent intent = new Intent(context, NewTrackEloDay.class);
                        intent.putExtra(context.getResources().getString(R.string.elotracker_id), eloTrackerDays.get(pos).getID());
                        intent.putExtra(context.getResources().getString(R.string.elotracker_date), eloTrackerDays.get(pos).getDate());
                        intent.putExtra(context.getResources().getString(R.string.elotracker_wins), eloTrackerDays.get(pos).getWins());
                        intent.putExtra(context.getResources().getString(R.string.elotracker_losses), eloTrackerDays.get(pos).getLosses());
                        intent.putExtra(context.getResources().getString(R.string.elotracker_elo), eloTrackerDays.get(pos).getElo());
                        intent.putExtra(context.getResources().getString(R.string.elotracker_lps), eloTrackerDays.get(pos).getLps());


                        Log.d(TAG, "onClick: PASSING DATA TO ITEM CLICKED ID:" +eloTrackerDays.get(pos).getID() );


                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }

                }
            });
        }

/*        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            };


        }*/
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return eloTrackerDays.get(id).getID();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
