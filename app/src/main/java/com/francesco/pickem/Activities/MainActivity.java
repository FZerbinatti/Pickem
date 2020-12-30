package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.francesco.pickem.R;
import com.francesco.pickem.Services.PreferencesData;

public class MainActivity extends AppCompatActivity {

    ProgressBar mainActivityProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityProgressBar = findViewById(R.id.mainActivityProgressBar);

        mainActivityProgressBar.setVisibility(View.VISIBLE);

        if ( PreferencesData.getUserLoggedInStatus(this) ){
            Intent intent = new Intent(MainActivity.this, PicksActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }
}