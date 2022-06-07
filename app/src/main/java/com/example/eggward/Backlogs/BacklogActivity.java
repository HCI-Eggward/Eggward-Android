package com.example.eggward.Backlogs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.example.eggward.Backlogs.domain.BacklogChildItem;
import com.example.eggward.EggBreeding.EggBreedActivity;
import com.example.eggward.MyPets.MyPetListActivity;
import com.example.eggward.R;
import com.example.eggward.Schedule.ScheduleActivity;
import com.example.eggward.Schedule.ScheduleListAdapter;
import com.example.eggward.Schedule.domain.ChildItem;
import com.example.eggward.Schedule.domain.ParentItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class BacklogActivity extends AppCompatActivity {
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    ArrayList<String> backlogList;

    Integer categoryIndex = 0;

    // TODO : Intent -> 로그인 정보 받아오기
    String userEmail = "eggward@ewhain.net";
    ArrayList<String> categoryList;

    AlertDialog.Builder builder;

    ExpandableListView listView;
    BacklogListAdapter listAdapter;
    ArrayList<ParentItem> groupList = new ArrayList<>();
    ArrayList<ArrayList<BacklogChildItem>> childList = new ArrayList<>();
    ArrayList<ArrayList<BacklogChildItem>> categoriesList = new ArrayList<>();

    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backlog);

        categoryList = new ArrayList<>();

        getTodoCategory();

        listView = findViewById(R.id.expandableBacklogList);

        listAdapter = new BacklogListAdapter();
        listAdapter.parentItems = groupList;
        listAdapter.childItems = childList;

        listView.setAdapter(listAdapter);
        listView.setGroupIndicator(null);

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

    public void setListItems() {
        groupList.clear();
        childList.clear();

        childList.addAll(categoriesList);

        for (int i=0; i<categoryList.size(); i++) {
            Log.v("here", categoryList.get(i));
            groupList.add(new ParentItem(categoryList.get(i)));
        }

        listAdapter.notifyDataSetChanged();
    }

    public void getTodoCategory() {
        CollectionReference ref = database.collection("User").document(userEmail).collection("backlogList");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        categoryList.add(document.getId());
                    }

                    for (int i=0; i<categoryList.size(); i++) {
                        categoriesList.add(new ArrayList<BacklogChildItem>());
                    }

                }
            }
        });

        getAllBacklogData();
    }

    public void getAllBacklogData() {
        CollectionReference ref = database.collection("User").document(userEmail).collection("backlogList");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ArrayList<HashMap<String, String>> tmpList = (ArrayList) document.get("backlog");
                        if (tmpList != null) {
                            for (int i = 0; i < tmpList.size(); i++) {
                                HashMap<String, String> tmpMap = tmpList.get(i);
                                String todoName = tmpMap.get("name");
                                String todoDate = tmpMap.get("date");
                                BacklogChildItem item = new BacklogChildItem(todoName, todoDate);
                                categoriesList.get(categoryIndex).add(item);
                            }
                            categoryIndex++;
                            setListItems();
                        }

                    }
                } else {
                    //
                }
            }
        });
    }



}