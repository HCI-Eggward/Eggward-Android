package com.example.eggward.Backlogs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.eggward.EggBreeding.EggBreedActivity;
import com.example.eggward.MyPets.MyPetListActivity;
import com.example.eggward.R;
import com.example.eggward.Schedule.ScheduleActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BacklogActivity extends AppCompatActivity {

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backlog);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.navigation_backlog);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_schedule:
                        intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_egg:
                        intent = new Intent(getApplicationContext(), EggBreedActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_mypet:
                        intent = new Intent(getApplicationContext(), MyPetListActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }
}