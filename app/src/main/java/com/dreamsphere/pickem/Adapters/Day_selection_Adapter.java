package com.dreamsphere.pickem.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.dreamsphere.pickem.Activities.MainActivities.PicksActivity;
import com.dreamsphere.pickem.R;

import java.util.ArrayList;

public class Day_selection_Adapter extends PagerAdapter {

    private ArrayList<String> matchDaysList;
    private LayoutInflater layoutInflater;
    private Context context;
    private String TAG ="Adapter ";
    PicksActivity picksActivity = new PicksActivity();



    public Day_selection_Adapter(ArrayList<String> matchDaysList, Context context) {
        this.matchDaysList = matchDaysList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return matchDaysList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.match_day_selection_item, container, false);

        TextView textViewDay;


        textViewDay = view.findViewById(R.id.match_day_textview);

        textViewDay.setText(picksActivity.getDisplayDate(matchDaysList.get(position)));

        container.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }



}
