package com.francesco.pickem.Adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Models.DisplayMatch;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


    public class Region_selection_Adapter_calendar extends RecyclerView.Adapter<Region_selection_Adapter_calendar.ViewHolder> {

        private static final String TAG = "RecyclerViewAdapter";

        //vars

        private ArrayList<String> allRegions = new ArrayList<>();
        private String imageRegionPath;
        private Context context;
        ArrayList <String> selectedCalendarRegions;

        public Region_selection_Adapter_calendar(ArrayList<String> allRegions, String imageRegionPath, Context context, ArrayList <String> selectedCalendarRegions) {

            Log.d(TAG, "Region_selection_Adapter_calendar: allRegions: "+allRegions.size());
            Log.d(TAG, "Region_selection_Adapter_calendar: selectedCalendarRegions: "+selectedCalendarRegions.size());
            for(int i=0; i<selectedCalendarRegions.size(); i++){

                Log.d(TAG, "Region_selection_Adapter_calendar: "+selectedCalendarRegions.get(i));
            }

            this.allRegions = allRegions;
            this.imageRegionPath = imageRegionPath;
            this.context = context;
            this.selectedCalendarRegions = selectedCalendarRegions;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leagueselection_withname, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            




            RequestOptions options2 = new RequestOptions()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.logo_lck);


            String local_image =imageRegionPath+allRegions.get(position).replace(" ", "")+".png";
            //Log.d(TAG, "instantiateItem: $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ "+imageRegionPath);


            try {
                holder.region_name_calendar.setText(getUppercase(allRegions.get(position)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (selectedCalendarRegions.contains(allRegions.get(position))){
                Log.d(TAG, "onBindViewHolder: seleziono: " +allRegions.get(position));
                holder.opacityImage.setVisibility(View.GONE);
            }


            Glide.with(context)
                    .load(new File(local_image)) // Uri of the picture
                    .apply(options2)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(holder.regionImage);


            holder.regionImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: clicked on an image: " + allRegions.get(position));
                    if (holder.opacityImage.getVisibility() == View.VISIBLE){
                        holder.opacityImage.setVisibility(View.GONE);
                        if (!selectedCalendarRegions.contains(allRegions.get(position))){
                            Log.d(TAG, "onClick: aggiunto: "+allRegions.get(position));
                            selectedCalendarRegions.add(allRegions.get(position));
                            updateUserCalendarRegions(selectedCalendarRegions);
                        }

                    }else {
                        holder.opacityImage.setVisibility(View.VISIBLE);
                        if (selectedCalendarRegions.contains(allRegions.get(position))){


                            selectedCalendarRegions.remove(allRegions.get(position));
                            Log.d(TAG, "onClick: rimosso: "+allRegions.get(position));
                            updateUserCalendarRegions(selectedCalendarRegions);

                        }

                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return allRegions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView regionImage;
            ImageView opacityImage;
            TextView region_name_calendar;


            public ViewHolder(View itemView) {
                super(itemView);
                regionImage = itemView.findViewById(R.id.league_logo);
                opacityImage = itemView.findViewById(R.id.opacity_region);
                region_name_calendar = itemView.findViewById(R.id.region_name_calendar);


            }
        }


        public String getUppercase(String br) throws IOException
        {
            String data, answer="";
            char[] findupper=br.toCharArray();
            for(int i=0;i<findupper.length;i++)
            {
                if(findupper[i]>=65&&findupper[i]<=91) //ascii value in between 65 and 91 is A to Z
                {
                    answer+=findupper[i]; //adding only uppercase
                }
            }

            return answer;
        }


        public void updateUserCalendarRegions (ArrayList <String> calendarRegions){

            for(int i=0; i<calendarRegions.size(); i++){
                Log.d(TAG, "onClick: "+selectedCalendarRegions.get(i));
            }


            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("UserCalendar")
                    .setValue(calendarRegions);







        }





    }