package com.dreamsphere.pickem.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dreamsphere.pickem.Models.AnalistPersonChoosen;
import com.dreamsphere.pickem.R;

import java.util.ArrayList;

public class ChooseAnalystRecyclerViewAdapter extends RecyclerView.Adapter<ChooseAnalystRecyclerViewAdapter.ViewHolder> {

    private ArrayList<AnalistPersonChoosen> analystsList;
    private Context context;

    LayoutInflater mInflater;
    EloTrackerRecyclerViewAdapter.ItemClickListener mClickListener;

    static  String TAG = "EloTrackerRecyclerViewAdapter: ";

    // data is passed into the constructor
    public ChooseAnalystRecyclerViewAdapter(Context context, ArrayList<AnalistPersonChoosen> analystsList) {
        this.mInflater = LayoutInflater.from(context);
        this.analystsList = analystsList;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ChooseAnalystRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.analyst_picker_item, parent, false);
        return new ChooseAnalystRecyclerViewAdapter.ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ChooseAnalystRecyclerViewAdapter.ViewHolder holder, int position) {

        String image_link = analystsList.get(position).getImage();
        String analyst_name = analystsList.get(position).getUsername();
        String analyst_region = analystsList.get(position).getRegion();
        Boolean analyst_selected = analystsList.get(position).getChoosen();

        holder.analyst_name.setText(analyst_name);
        holder.analyst_region.setText(analyst_region);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .error(R.drawable.ic_loading_error);

        Glide
                .with(context)
                .load(image_link)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.background_round_corners)
                .apply(options)
                .into(holder.analyst_image);



        if(analyst_selected){
            ColorStateList colorStateListGreen = ContextCompat.getColorStateList(holder.analyst_picker_region_background.getContext(), R.color.material_green);
            holder.analyst_picker_region_background.setBackgroundTintList(colorStateListGreen);
        } else {
            ColorStateList colorStateListRed = ContextCompat.getColorStateList(holder.analyst_picker_region_background.getContext(), R.color.material_red);
            holder.analyst_picker_region_background.setBackgroundTintList(colorStateListRed);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return analystsList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView analyst_name;
        ImageView analyst_image;
        TextView analyst_region;
        ConstraintLayout analyst_picker_region_background;


        ViewHolder(View itemView) {
            super(itemView);
            analyst_name = itemView.findViewById(R.id.choose_analyst_name);
            analyst_image = itemView.findViewById(R.id.choose_analyst_imageview);
            analyst_region = itemView.findViewById(R.id.choose_analyst_region);
            analyst_picker_region_background = itemView.findViewById(R.id.analyst_picker_region_background);

        }


    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return analystsList.get(id).getUserId();
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
