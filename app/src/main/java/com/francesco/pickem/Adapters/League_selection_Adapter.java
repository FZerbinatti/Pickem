package com.francesco.pickem.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.francesco.pickem.Models.TeamDetails;
import com.francesco.pickem.R;

import java.util.List;

public class League_selection_Adapter extends PagerAdapter {

    private List<TeamDetails> leaguesSelectedList;
    private LayoutInflater layoutInflater;
    private Context context;
    private String TAG ="Adapter ";


    public League_selection_Adapter(List<TeamDetails> leaguesSelectedList, Context context) {
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

        imageView.setImageResource(leaguesSelectedList.get(position).getImage());
        //title.setText(leaguesSelectedList.get(position).getLeague_name());


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
