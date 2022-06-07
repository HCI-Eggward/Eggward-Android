package com.example.eggward.EggBreeding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eggward.Backlogs.BacklogActivity;
import com.example.eggward.MyPets.MyPetListActivity;
import com.example.eggward.R;
import com.example.eggward.Schedule.ScheduleActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class EggBreedResultActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    TextView eggNameTextView;
    TextView petMsgTextView;
    Button confirmBtn;

    String eggName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg_breed_result);

        Intent subIntent = getIntent();
        eggName = subIntent.getStringExtra("eggName");

        eggNameTextView = findViewById(R.id.eggName);
        eggNameTextView.setText(eggName);
        petMsgTextView = findViewById(R.id.egg_petMsg);
        confirmBtn = findViewById(R.id.egg_confirmbutton);

        String petMsg = "축하합니다!\n"+eggName+"(이)가 부화했습니다!";
        SpannableString spannableString = new SpannableString(petMsg);
        int start = petMsg.indexOf(eggName);
        int end = start + eggName.length();
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        petMsgTextView.setText(spannableString);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPetListActivity.class);
                startActivity(intent);
            }
        });

        navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.navigation_egg);
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