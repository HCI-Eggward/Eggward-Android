package com.example.eggward.EggBreeding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.eggward.R;

public class EggHatchActivity extends AppCompatActivity {

    ImageView imageView;
    String eggName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg_hatch);

        Intent subIntent = getIntent();
        eggName = subIntent.getStringExtra("eggName");

        imageView = findViewById(R.id.egg_imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EggBreedResultActivity.class);
                intent.putExtra("eggName", eggName);
                startActivity(intent);
            }
        });
    }
}