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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyPetDetailActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    TextView petNameTextView;
    TextView petLevelTextView;
    ProgressBar petProgressBar;
    TextView petProgressDetail;
    TextView petTextbox;
    ImageView petImage;
    ImageView petHappyImage;

    ImageView itemClothesImageView;
    ImageView itemFoodImageView;
    ImageView itemBallImageView;

    Button itemClothesCountBtn;
    Button itemFoodCountBtn;
    Button itemBallCountBtn;

    public static FirebaseFirestore database = FirebaseFirestore.getInstance();

    int position;
    String userEmail;
    int curExp;
    int maxExp;
    int level;
    String petName;
    String textboxMsg;
    int clothesItemCount;
    int foodItemCount;
    int ballItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pet_detail);

        Intent subIntent = getIntent();
        petName = subIntent.getStringExtra("name");
        position = subIntent.getIntExtra("position", 0);

        petNameTextView = findViewById(R.id.petName);
        petLevelTextView = findViewById(R.id.petLevel);
        petProgressBar = findViewById(R.id.pet_progressBar);
        petProgressDetail = findViewById(R.id.pet_progressDetail);
        petTextbox = findViewById(R.id.pet_textbox);

        petImage = findViewById(R.id.pet_imageView);
        petHappyImage = findViewById(R.id.pet_happy_imageView);

        setPetImage();

        itemClothesImageView = findViewById(R.id.item1);
        itemFoodImageView = findViewById(R.id.item2);
        itemBallImageView = findViewById(R.id.item3);

        itemClothesCountBtn = findViewById(R.id.item_clothes_count);
        itemFoodCountBtn = findViewById(R.id.item_food_count);
        itemBallCountBtn = findViewById(R.id.item_ball_count);

        AddButtonOnClickListener();

        navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.navigation_mypet);
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
        getItemInfo();
    }

    private void setPetImage() {
        Drawable petImageDrawable;
        Drawable petHappyImageDrawable;
        switch(position){
            case 0:
                petImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_choala, null);
                petHappyImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_choala_happy, null);
                break;
            case 1:
                petImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_rabbit, null);
                petHappyImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_rabbit_happy, null);
                break;
            case 2:
                petImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_penguin, null);
                petHappyImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_rabbit_happy, null);
                break;
            case 3:
                petImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_mouse, null);
                petHappyImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_mouse_happy, null);
                break;
            case 4:
                petImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_bird, null);
                petHappyImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_bird_happy, null);
                break;
            case 5:
                petImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_fish, null);
                petHappyImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_fish_happy, null);
                break;
            case 6:
                petImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_panda, null);
                petHappyImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_panda_happy, null);
                break;
            case 7:
                petImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_monkey, null);
                petHappyImageDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pet_monkey_happy, null);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }
        petImage.setImageDrawable(petImageDrawable);
        petHappyImage.setImageDrawable(petHappyImageDrawable);
    }

    private void AddButtonOnClickListener() {
        itemClothesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clothesItemCount > 0)
                    updateData("clothes");
                else
                    Toast.makeText(getApplicationContext(), "아이템을 사용할 수 없습니다", Toast.LENGTH_LONG).show();
            }
        });

        itemFoodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodItemCount > 0)
                    updateData("food");
                else
                    Toast.makeText(getApplicationContext(), "아이템을 사용할 수 없습니다", Toast.LENGTH_LONG).show();
            }
        });

        itemBallImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ballItemCount > 0)
                    updateData("ball");
                else
                    Toast.makeText(getApplicationContext(), "아이템을 사용할 수 없습니다", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getExpInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
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
                        petLevelTextView.setText("Lv." + level);
                        petProgressDetail.setText(curExp + "/" + maxExp);

                        if (curExp == 100) {
                            petProgressDetail.setText(curExp + "/" + maxExp);
                            changeProgressBar();
                            levelUp();
                        } else {
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


    public void getItemInfo () {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            DocumentReference ref = database.collection("User").document(userEmail).collection("itemList").document("itemList");
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        clothesItemCount = Integer.parseInt(String.valueOf(document.getData().get("clothes")));
                        foodItemCount = Integer.parseInt(String.valueOf(document.getData().get("food")));
                        ballItemCount = Integer.parseInt(String.valueOf(document.getData().get("ball")));

                        itemClothesCountBtn.setText(String.valueOf(clothesItemCount));
                        itemFoodCountBtn.setText(String.valueOf(foodItemCount));
                        itemBallCountBtn.setText(String.valueOf(ballItemCount));
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

    }

    private void levelUp() {
        database.collection("User").document(userEmail).collection("petList").document(petName)
                .update("curExp", 0, "level", level+1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        curExp = 0;
                        level+=1;
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

    private void changeProgressBar() {
        petProgressBar.setProgress(curExp);
    }

    private void changeTextBox() {
        switch (curExp) {
            case 0:
            case 10:
            case 20:
                textboxMsg = "오늘도 수고 많았어요!";
                break;
            case 30:
            case 40:
                textboxMsg = "음~ 너무 맛있네요!";
                break;
            case 50:
            case 60:
                textboxMsg = "맛있는거 먹어서 기분이 좋다! 너도?";
                break;
            case 70:
            case 80:
                textboxMsg = "요즘은 하루하루가 즐거워요";
                break;
            case 90:
                textboxMsg = "덕분에 요즘 너무 행복해요!";
                break;
        }
        petTextbox.setText(textboxMsg);
    }

    public void changePetImage() {
        getExpInfo();

        petImage.setVisibility(View.GONE);
        petHappyImage.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                petImage.setVisibility(View.VISIBLE);
                petHappyImage.setVisibility(View.GONE);
            }
            }, 1000); // 3000==3초
    }

    private void uploadExpData(){
        DocumentReference ref = database.collection("User").document(userEmail).collection("petList").document(petName);
        ref.update("curExp", FieldValue.increment(10));

        curExp += 10;
        if (curExp == 100)
            levelUp();
    }

    private void decreaseItemCount(String itemName) {
         switch(itemName){
            case "clothes":
                clothesItemCount-=1;
                itemClothesCountBtn.setText(String.valueOf(clothesItemCount));
                break;
            case "food":
                foodItemCount-=1;
                itemFoodCountBtn.setText(String.valueOf(foodItemCount));
                break;
            case "ball":
                ballItemCount-=1;
                itemBallCountBtn.setText(String.valueOf(ballItemCount));
                break;
        }
        DocumentReference ref = database.collection("User").document(userEmail).collection("itemList").document("itemList");
        ref.update(itemName, FieldValue.increment(-1));
    }

    public void updateData(String itemName) {
        uploadExpData();
        changePetImage();
        changeTextBox();
        decreaseItemCount(itemName);
    }


}