package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StatsActivity extends AppCompatActivity{
    // implements GestureDetector.OnGestureListener
    Context context;
    //gesture listener stuff
/*    private float x1,x2,y1,y2;
    private static int MIN_DISTANCE =150;
    private GestureDetector gestureDetector;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        //this.gestureDetector = new GestureDetector(StatsActivity.this, this);
        context = this;
        setupBottomNavView();
    }




    private void setupBottomNavView() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.button_statistics);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.button_picks:
                        Intent intentPicks= new Intent(context, PicksActivity.class);
                        startActivity(intentPicks);
                        Animatoo.animateFade(context);
                        break;
                    case R.id.button_calendar:
                        Intent intentCalendar= new Intent(context, Calendar.class);
                        startActivity(intentCalendar);
                        Animatoo.animateFade(context);
                        break;

                    case R.id.button_statistics:
                        Intent intentStats= new Intent(context, StatsActivity.class);
                        startActivity(intentStats);
                        Animatoo.animateFade(context);
                        break;

                    case R.id.button_settings:
                        Intent intentNotif= new Intent(context, SettingsActivity.class);
                        startActivity(intentNotif);
                        Animatoo.animateFade(context);
                        break;
                }
                return true;
            }
        });

    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                float valueX = x2-x1;

                if (Math.abs(valueX)> MIN_DISTANCE){

                    if (x1>x2){
                        //swiped right


                        Intent intent = new Intent(StatsActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        Animatoo.animateSlideLeft(context);
                    }else {
                        //swiped left


                        Intent intent = new Intent(StatsActivity.this, Calendar.class);
                        startActivity(intent);
                        Animatoo.animateSlideRight(context);

                    }

                }
        }

        return super.onTouchEvent(event);
    }*/
   /* @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }*/
}