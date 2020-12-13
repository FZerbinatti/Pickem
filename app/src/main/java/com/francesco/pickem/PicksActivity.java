package com.francesco.pickem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.os.Bundle;

import com.francesco.pickem.Adapters.League_selection_Adapter;
import com.francesco.pickem.Models.SelectionLeague;

import java.util.ArrayList;
import java.util.List;

public class PicksActivity extends AppCompatActivity {

    ViewPager viewPager;
    League_selection_Adapter adapter;
    List<SelectionLeague> selectedLeagues;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    ConstraintLayout pick_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picks);

        initializeLeagueSelection();

    }

    private void initializeLeagueSelection() {

        viewPager = findViewById(R.id.viewPager_picksActivity);
        pick_background = findViewById(R.id.pick_background);


        selectedLeagues = new ArrayList<>();
        selectedLeagues.add(new SelectionLeague(R.drawable.logo_lck, "LCK"));
        selectedLeagues.add(new SelectionLeague(R.drawable.logo_lec, "LEC"));
        selectedLeagues.add(new SelectionLeague(R.drawable.logo_lpl, "LPL"));
        selectedLeagues.add(new SelectionLeague(R.drawable.logo_lcs, "LCS"));

        adapter = new League_selection_Adapter(selectedLeagues, this);

        viewPager = findViewById(R.id.viewPager_picksActivity);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(400, 0, 400, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.background_lck),
                getResources().getColor(R.color.background_lec),
                getResources().getColor(R.color.background_lpl),
                getResources().getColor(R.color.background_lcs)
        };

        colors = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() -1) && position < (colors.length - 1)) {
                    /*viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );*/
                    pick_background.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );


                }

                else {
                    //viewPager.setBackgroundColor(colors[colors.length - 1]);
                    pick_background.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
}