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
    Integer[] colors_backgroundlistview = null;
    Integer[] colors_background_navbar = null;

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

        /*Integer[] colors_bluegradient = {
                getResources().getColor(R.color.var2),
                getResources().getColor(R.color.var1),
                getResources().getColor(R.color.var2),
                getResources().getColor(R.color.var1)
        };*/

        Integer[] colors_temp = {
                getResources().getColor(R.color.sfum1),
                getResources().getColor(R.color.sfum2),
                getResources().getColor(R.color.sfum3),
                getResources().getColor(R.color.sfum4)
        };

        //colors_background_navbar = colors_bluegradient;
        colors_backgroundlistview = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() -1) && position < (colors_backgroundlistview.length - 1)) {
/*                    viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors_background_navbar[position],
                                    colors_background_navbar[position + 1]
                            )
                    );*/
                    pick_background.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors_backgroundlistview[position],
                                    colors_backgroundlistview[position + 1]
                            )
                    );


                }

                else {
//                    viewPager.setBackgroundColor(colors_background_navbar[colors_background_navbar.length - 1]);
                    pick_background.setBackgroundColor(colors_backgroundlistview[colors_backgroundlistview.length - 1]);
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