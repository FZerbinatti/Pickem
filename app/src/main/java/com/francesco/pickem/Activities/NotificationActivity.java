package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.francesco.pickem.R;

public class NotificationActivity extends AppCompatActivity {

    SwitchCompat switch_notification;
    ConstraintLayout show_notifications_box;
    private String TAG ="NotificationActivity: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        switch_notification = (SwitchCompat)  findViewById(R.id.switch_notification);
        show_notifications_box = findViewById(R.id.show_notifications_box);

        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (switch_notification.isChecked()){
                    Log.d(TAG, "onCreate: ON");
                    show_notifications_box.setVisibility(View.VISIBLE);
                }else {
                    Log.d(TAG, "onCreate: OFF");
                    show_notifications_box.setVisibility(View.GONE);
                }
            }
        });



        changeNavBarColor();
    }



    public void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }
}