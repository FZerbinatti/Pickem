package com.francesco.pickem.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Models.RegionDetails;
import com.francesco.pickem.Models.TeamDetails;
import com.francesco.pickem.R;

import java.util.ArrayList;
import java.util.List;

public class Region_selection_Adapter extends PagerAdapter {

    private ArrayList<RegionDetails> leaguesSelectedList;
    private LayoutInflater layoutInflater;
    private Context context;
    private String TAG ="Adapter ";


    public Region_selection_Adapter(ArrayList<RegionDetails> leaguesSelectedList, Context context) {
        this.leaguesSelectedList = leaguesSelectedList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return leaguesSelectedList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardview_item_leagueselection, container, false);

        ImageView imageView;
        //TextView title, desc;

        imageView = view.findViewById(R.id.league_logo);
       // title = view.findViewById(R.id.league_name);

        //imageView.setImageResource(leaguesSelectedList.get(position).getImage());
        //title.setText(leaguesSelectedList.get(position).getLeague_name());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.logo_lec)
                .error(R.drawable.logo_lck);

        //Log.d(TAG, "/////////////////////////////////////////instantiateItem: "+leaguesSelectedList.get(position).getImage());

        Glide.with(context).load(leaguesSelectedList.get(position).getImage()).placeholder(R.drawable.ic_load).apply(options).into(imageView);


/*        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("param", models.get(position).getTitle());
                context.startActivity(intent);
                // finish();
            }
        });*/

        container.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
