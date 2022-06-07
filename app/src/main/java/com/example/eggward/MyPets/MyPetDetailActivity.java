package com.example.eggward.MyPets;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.eggward.Backlogs.BacklogActivity;
import com.example.eggward.EggBreeding.EggBreedActivity;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class MyPetDetailActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    TextView petNameTextView;
    TextView petLevelTextView;
    ProgressBar petProgressBar;
    TextView petProgressDetail;
    TextView petTextbox;
    ImageView petImage;
    ImageView petHappyImage;
    TextView itemCount;

    int imageChange = 0;

    public static FirebaseFirestore database = FirebaseFirestore.getInstance();

    String userEmail;
    int curExp;
    int maxExp;
    int level;
    String petName;
    String textboxMsg;
    int count=13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pet_detail);

        Intent subIntent = getIntent();
        petName = subIntent.getStringExtra("name");
        Log.v("넘어온 값",petName);

        petNameTextView = findViewById(R.id.petName);
        petLevelTextView = findViewById(R.id.petLevel);
        petProgressBar = findViewById(R.id.pet_progressBar);
        petProgressDetail = findViewById(R.id.pet_progressDetail);
        petTextbox = findViewById(R.id.pet_textbox);
        petImage = findViewById(R.id.pet_imageView);
        petHappyImage = findViewById(R.id.pet_happy_imageView);
        itemCount = findViewById(R.id.count_item2);

        itemCount.setText(String.valueOf(count));

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

        getExpInfo();
    }


    public void getExpInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            userEmail = user.getEmail();
            DocumentReference ref = database.collection("User").document(userEmail).collection("petList").document(petName);
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        curExp = Integer.parseInt(document.getData().get("curExp").toString());
                        maxExp = Integer.parseInt(document.getData().get("maxExp").toString());
                        level = Integer.parseInt(document.getData().get("level").toString());
                        Log.v("petname", petName);
                        Log.v("curExp", String.valueOf(curExp));

                        petNameTextView.setText(petName);
                        petLevelTextView.setText("Lv."+level);
                        petProgressDetail.setText(curExp + "/" + maxExp);

                        if(curExp == 100){
                            petProgressDetail.setText(curExp + "/" + maxExp);
                            changeProgressBar();
                            levelUp();
                        }
                        else{
                            changeProgressBar();
                            changeTextBox();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

    }
    private void levelUp(){
        initPetExp();
        DocumentReference ref = database.collection("User").document(userEmail).collection("petList").document(petName);

        final Task<Void> level_inc =
                ref.update("level", FieldValue.increment(1));

    }

    private void initPetExp(){
        database.collection("User").document(userEmail).collection("petList").document(petName)
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
        petProgressBar.setProgress(curExp);
    }

    private void changeTextBox(){
        switch(curExp){
            case 0:
            case 10:
            case 20:
                textboxMsg="오늘도 수고 많았어요!";
                break;
            case 30:
            case 40:
                textboxMsg="음~ 너무 맛있네요!";
                break;
            case 50:
            case 60:
                textboxMsg = "맛있는거 먹어서 기분이 좋다! 너도?";
                break;
            case 70:
            case 80:
                textboxMsg = "음 맛있어~ 이건 어느 나무 잎이야?";
                break;
            case 90:
                textboxMsg = "덕분에 요즘 너무 행복해요!";
                break;
       }
       petTextbox.setText(textboxMsg);
    }

    public void changePetImage(View view){
        if(imageChange == 0){
            count-=1;
            itemCount.setText(String.valueOf(count));
            petImage.setVisibility(View.VISIBLE);
            petHappyImage.setVisibility(View.GONE);

            updateData();
            getExpInfo();
            imageChange=1;


        }else if(imageChange == 1){
            petImage.setVisibility(View.GONE);
            petHappyImage.setVisibility(View.VISIBLE);
            imageChange=0;
        }
    }
    public void updateData() {
        DocumentReference ref = database.collection("User").document(userEmail).collection("petList").document(petName);

        final Task<Void> curExp_inc =
                ref.update("curExp", FieldValue.increment(10));

    }


}