package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.francesco.pickem.PicksActivity;
import com.francesco.pickem.R;

public class LoginActivity extends AppCompatActivity {
    Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.button_login);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( LoginActivity.this, PicksActivity.class);
                startActivity(intent);
                finish();
            }
        });





        changeNavBarColor();

    }

    private void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }
}