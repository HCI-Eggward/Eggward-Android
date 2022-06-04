package com.example.eggward.EggBreeding;

import static android.content.ContentValues.TAG;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eggward.Backlogs.BacklogActivity;
import com.example.eggward.MyPets.MyPetListActivity;
import com.example.eggward.R;
import com.example.eggward.Schedule.ScheduleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EggBreedActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    TextView eggNameTextView;
    TextView eggTextbox;
    ImageView eggImage;
    ProgressBar eggProgressBar;
    TextView eggProgressDetail;
    TextView eggStatusTextView;
    TextView eggAgeTextView;

    public static FirebaseFirestore database = FirebaseFirestore.getInstance();

    String userEmail;
    String eggName="달걀이름";
    int curExp;
    int maxExp;
    String eggStatus;
    int eggAge;
    String textboxMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg_breed);

        eggNameTextView = findViewById(R.id.eggName);
        eggTextbox = findViewById(R.id.egg_textbox);
        eggImage = findViewById(R.id.egg_imageView);
        eggProgressBar = findViewById(R.id.egg_progressBar);
        eggProgressDetail = findViewById(R.id.egg_progressDetail);
        eggStatusTextView = findViewById(R.id.egg_status_val);
        eggAgeTextView = findViewById(R.id.egg_age_val);

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
                    case R.id.navigation_mypet:
                        intent = new Intent(getApplicationContext(), MyPetListActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        getExpInfo();
    }

    private void getExpInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            DocumentReference docRef = database.collection("User").document(userEmail).collection("eggBreed").document("eggBreed");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            eggName = (String) document.getData().get("name");
                            curExp = Integer.parseInt(String.valueOf(document.getData().get("curExp")));
                            maxExp = Integer.parseInt(String.valueOf(document.getData().get("maxExp")));
                            eggAge = Integer.parseInt(String.valueOf(document.getData().get("age")));
                            eggStatus = (String) document.getData().get("status");

                            eggNameTextView.setText(eggName);
                            eggAgeTextView.setText(String.valueOf(eggAge));
                            eggStatusTextView.setText(eggStatus);

                            eggProgressDetail.setText(curExp+"/"+maxExp);
                            if (curExp == 100)
                                hatchEgg();
                            else {
                                changeProgressBar();
                                changeTextBox();
                                changeEggImage();
                            }
                        } else {
                            Log.e("유저 정보 오류", "사용자 정보가 존재하지 않습니다.");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }

            });
        }
    }

    private void hatchEgg() {
        initEggExp();
        Intent intent = new Intent(getApplicationContext(), EggHatchActivity.class);
        intent.putExtra("eggName", eggName);
        startActivity(intent);
    }

    private void initEggExp() {
        database.collection("User").document(userEmail).collection("eggBreed").document("eggBreed")
                .update("curExp", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.v("경험치 초기화 성공", "success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("경험치 초기화 실패", "failure");
                    }
                });
    }

    private void changeProgressBar(){
        eggProgressBar.setProgress(curExp);
    }

    private void changeTextBox() {
        switch(curExp/10){
            case 0:
            case 1:
            case 2:
                textboxMsg = "오늘도 #가보자고";
                break;
            case 3:
            case 4:
                textboxMsg = "조금 더 힘이 넘치는 것 같아요";
                break;
            case 5:
            case 6:
                textboxMsg = "공부하기 딱 좋은 날이네요";
                break;
            case 7:
            case 8:
                textboxMsg = "오늘은 뭔가 기분이 좋아요";
                break;
            case 9:
                textboxMsg = "이제 곧 깨어날 수 있는거야?";
                break;
        }
        eggTextbox.setText(textboxMsg);
    }

    private void changeEggImage() {
        Drawable drawable;
        switch(curExp/20){
            case 0:
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.egg_0, null);
                break;
            case 1:
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.egg_20, null);
                break;
            case 2:
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.egg_40, null);
                break;
            case 3:
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.egg_60, null);
                break;
            case 4:
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.egg_80, null);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + curExp / 20);
        }
        eggImage.setImageDrawable(drawable);
    }

}