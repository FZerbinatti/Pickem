package com.francesco.pickem.Activities.Statistics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.francesco.pickem.R;

public class AnalistActivity extends AppCompatActivity {

    ImageButton button_add_analyst, analists_back_arrow;
    RecyclerView analysts_recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analist);

        analists_back_arrow = findViewById(R.id.analists_back_arrow);
        analysts_recyclerview = findViewById(R.id.analysts_recyclerview);
        button_add_analyst = findViewById(R.id.button_add_analyst);

        analists_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        button_add_analyst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AnalistActivity.this, AddAnalystActivity.class);
                startActivity(intent);
            }
        });


    }
}