package com.example.eggward.Schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.eggward.Backlogs.BacklogActivity;
import com.example.eggward.EggBreeding.EggBreedActivity;
import com.example.eggward.MyPets.MyPetListActivity;
import com.example.eggward.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    // TODO : Intent -> 로그인 정보 받아오기
    String userEmail = "eggward@ewhain.net";

    BottomNavigationView navigationView;
    FloatingActionButton addScheduleButton;
    EditText inputCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        inputCategory = new EditText(this);

        addScheduleButton = findViewById(R.id.fab_categories);
        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 카테고리 만드는 화면 띄우기
                new AlertDialog.Builder(ScheduleActivity.this)
                        .setTitle("카테고리 추가")
                        .setMessage("새 카테고리의 이름을 작성해주세요.")
                        .setView(inputCategory)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // add category to DB
                                // path -> User/{userEmail}/todoList/{categoryName}
                                String categoryName = inputCategory.getText().toString();
                                Map<String, Object> categoryArrayMap = new HashMap();
                                categoryArrayMap.put("todo", new ArrayList<>());
                                database.collection("User").document(userEmail).collection("todoList").document(categoryName).set(categoryArrayMap);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).show();
            }
        });

        navigationView = findViewById(R.id.navigationView);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_backlog:
                        intent = new Intent(getApplicationContext(), BacklogActivity.class);
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