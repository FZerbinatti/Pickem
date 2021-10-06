package com.dreamsphere.pickem.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamsphere.pickem.Models.SimpleRegion;
import com.dreamsphere.pickem.R;

import java.util.ArrayList;

public class SimpleRegionRecyclerViewAdapter extends RecyclerView.Adapter<SimpleRegionRecyclerViewAdapter.ViewHolder> {

    private ArrayList<SimpleRegion> regionList;
    private Context context;

    LayoutInflater mInflater;
    EloTrackerRecyclerViewAdapter.ItemClickListener mClickListener;

    static  String TAG = "EloTrackerRecyclerViewAdapter: ";

    // data is passed into the constructor
    public SimpleRegionRecyclerViewAdapter(Context context, ArrayList<SimpleRegion> regionList) {
        this.mInflater = LayoutInflater.from(context);
        this.regionList = regionList;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public SimpleRegionRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.region_picker_item, parent, false);
        return new SimpleRegionRecyclerViewAdapter.ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(SimpleRegionRecyclerViewAdapter.ViewHolder holder, int position) {


        String region_name = regionList.get(position).getName();
        if (region_name.equals("Mondiali")){
            region_name="Worlds";
        }
        Boolean region_selected = regionList.get(position).getChecked();

        holder.item_regionList_name.setText(region_name);

        if(region_selected){
            ColorStateList colorStateListGreen = ContextCompat.getColorStateList(holder.region_picker_region_background.getContext(), R.color.material_green);
            holder.region_picker_region_background.setBackgroundTintList(colorStateListGreen);
        } else {
            ColorStateList colorStateListRed = ContextCompat.getColorStateList(holder.region_picker_region_background.getContext(), R.color.material_red);
            holder.region_picker_region_background.setBackgroundTintList(colorStateListRed);
        }

/*        holder.region_picker_region_background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (regionList.get(position).getChecked()){
                    Log.d(TAG, "onClick: was true now false");

                    regionList.get(position).setChecked(false);
                }else {
                    Log.d(TAG, "onClick: was false now true");
                    regionList.get(position).setChecked(true);
                }
                notifyDataSetChanged();
                return false;
            }
        });*/





    }

    // total number of rows
    @Override
    public int getItemCount() {
        return regionList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView item_regionList_name;
        ConstraintLayout region_picker_region_background;


        ViewHolder(View itemView) {
            super(itemView);
            item_regionList_name = itemView.findViewById(R.id.region_picker_region_name);
            region_picker_region_background = itemView.findViewById(R.id.region_picker_region_background);

            //itemView.setOnClickListener(this);
/*            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int pos = getAdapterPosition();
                    if (pos !=  RecyclerView.NO_POSITION){
                        SimpleRegion simpleRegion = regionList.get(pos);

                        if (simpleRegion.getChecked()){
                            Log.d(TAG, "onClick: was true now false");

                            simpleRegion.setChecked(false);
                        }else {
                            Log.d(TAG, "onClick: was false now true");
                            simpleRegion.setChecked(true);
                        }

                    }
                    return false;
                }


            });*/
        }


    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return regionList.get(id).getName();
    }

    // allows clicks events to be caught
    void setClickListener(EloTrackerRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
