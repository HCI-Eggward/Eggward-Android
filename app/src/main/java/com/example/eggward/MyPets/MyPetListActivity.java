package com.example.eggward.MyPets;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.eggward.Backlogs.BacklogActivity;
import com.example.eggward.EggBreeding.EggBreedActivity;
import com.example.eggward.R;
import com.example.eggward.Schedule.ScheduleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MyPetListActivity extends AppCompatActivity {
    private GridView gridView;
    private MyPetListAdapter adapter;

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pet_list);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_schedule:
                        intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_backlog:
                        intent = new Intent(getApplicationContext(), BacklogActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_egg:
                        intent = new Intent(getApplicationContext(), EggBreedActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        gridView = (GridView) findViewById(R.id.mypetListArea);

        adapter = new MyPetListAdapter();
        adapter.getMypetList();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addItem();
                gridView.setAdapter(adapter);
            }
        },1500);

       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           @Override
           public void onItemClick(AdapterView parent, View v, int position, long id){
               Intent intent = new Intent(getApplicationContext(),MyPetDetailActivity.class);
               startActivity(intent);
           }
       });
    }


}