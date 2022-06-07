package com.example.eggward.Schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.eggward.Backlogs.BacklogActivity;
import com.example.eggward.EggBreeding.EggBreedActivity;
import com.example.eggward.MyPets.MyPetListActivity;
import com.example.eggward.R;
import com.example.eggward.Schedule.domain.ChildItem;
import com.example.eggward.Schedule.domain.ParentItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    static ArrayList<String> dateList = new ArrayList<>(Arrays.asList(
            "2022-06-05", "2022-06-06", "2022-06-07", "2022-06-08", "2022-06-09", "2022-06-10", "2022-06-11"
    ));

    Integer categoryIndex = 0;

    // TODO : Intent -> 로그인 정보 받아오기
    String userEmail = "eggward@ewhain.net";
    ArrayList<String> categoryList;
    ArrayList<String> todoContentList;

    AlertDialog.Builder builder;
    ImageButton addTodoButton;

    ExpandableListView listView;
    ScheduleListAdapter listAdapter;
    ArrayList<ParentItem> groupList = new ArrayList<>();
    ArrayList<ArrayList<ChildItem>> childList = new ArrayList<>();
    ArrayList<ArrayList<ChildItem>> categoriesList = new ArrayList<>();

    BottomNavigationView navigationView;
    FloatingActionButton addScheduleButton;
    EditText inputTodo;
    Spinner categorySpinner;
    String selectedCategory = "AI";
    EditText inputCategory;

    // Weakly Calendar Part
    ArrayList<Button> buttonList = new ArrayList<>();
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;

    int calendarIdx;
    String date;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        sharedPreferences = getApplicationContext().getSharedPreferences("eggward", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        calendarIdx = sharedPreferences.getInt("calendarIdx", 3);
        date = sharedPreferences.getString("date", "2022-06-08");
        Log.v("calendarIdx", String.valueOf(calendarIdx));
        Log.v("date", date);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);

        setButtonColor();

        categoryList = new ArrayList<>();

        getTodoCategory();

        listView = findViewById(R.id.expandableList);

        listAdapter = new ScheduleListAdapter();
        listAdapter.parentItems = groupList;
        listAdapter.childItems = childList;

        listView.setAdapter(listAdapter);
        listView.setGroupIndicator(null);

        addTodoButton = findViewById(R.id.addTodoButton);
        addTodoButton.setOnClickListener(view -> {
            // To-Do 추가하는 화면 띄우기
            builder = new AlertDialog.Builder(ScheduleActivity.this);

            // Inflate using dialog themed context.
            final Context context = builder.getContext();
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View alertView = inflater.inflate(R.layout.alert_dialog_todo, null, false);

            categorySpinner = alertView.findViewById(R.id.categorySpinner);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, categoryList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            categorySpinner.setAdapter(spinnerAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    getOriginalTodo();
                    categoryIndex = position;
                    selectedCategory = categoryList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            builder.setTitle("To-Do 추가")
                    .setMessage("새 To-Do의 내용을 작성해주세요.")
                    .setView(alertView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        // add To-Do to DB
                        inputTodo = alertView.findViewById(R.id.inputTodo);
                        Map<String, Object> todoArrayMap = new HashMap();
                        todoContentList.add(inputTodo.getText().toString());
                        todoArrayMap.put(date, todoContentList);

                        ChildItem item = new ChildItem(inputTodo.getText().toString());
                        categoriesList.get(categoryIndex).add(item);

                        setListItems();

                        if (inputTodo.getText() != null)
                            database.collection("User").document(userEmail).collection("todoList").document(selectedCategory).set(todoArrayMap, SetOptions.merge());
                    })
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                        // do nothing
                    }).show();
        });

        addScheduleButton = findViewById(R.id.fab_categories);
        addScheduleButton.setOnClickListener(view -> {
            inputCategory = new EditText(this);
            // 카테고리 만드는 화면 띄우기
            new AlertDialog.Builder(ScheduleActivity.this)
                    .setTitle("카테고리 추가")
                    .setMessage("새 카테고리의 이름을 작성해주세요.")
                    .setView(inputCategory)
                    .setPositiveButton("OK", (dialog, which) -> {
                        // add category to DB
                        // path -> User/{userEmail}/todoList/{categoryName}
                        String categoryName = inputCategory.getText().toString();
                        Map<String, Object> categoryArrayMap = new HashMap();
                        categoryArrayMap.put("todo", null);
                        database.collection("User").document(userEmail).collection("todoList").document(categoryName).set(categoryArrayMap);
                    })
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                        // do nothing
                    }).show();
        });

        navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.navigation_schedule);
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
        CollectionReference ref = database.collection("User").document(userEmail).collection("todoList");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        categoryList.add(document.getId());
                        Log.v("here~", document.getId());
                    }

                    for (int i=0; i<categoryList.size(); i++) {
                        categoriesList.add(new ArrayList<ChildItem>());
                    }

                    getAllData();
                }
            }
        });
    }

    public void getAllData() {
        CollectionReference ref = database.collection("User").document(userEmail).collection("todoList");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.v("selectedcategory", selectedCategory);
                        Log.v("date", date);
                        ArrayList tmpList = (ArrayList) document.get(date);
                        if (tmpList != null) {
                            for (int i = 0; i < tmpList.size(); i++) {
                                Log.v("child item", (String) tmpList.get(i));
                                ChildItem item = new ChildItem((String) tmpList.get(i));
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

    public void getOriginalTodo()  {
        todoContentList = new ArrayList<>();
        CollectionReference ref = database.collection("User").document(userEmail).collection("todoList");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(selectedCategory)) {
                            Log.v("selectedcategory", selectedCategory);
                            Log.v("date", date);
                            ArrayList tmpList = (ArrayList) document.get(date);
                            if (tmpList != null) {
                                for (int i = 0; i < tmpList.size(); i++) {
                                    todoContentList.add((String) tmpList.get(i));
                                    Log.v("child item", "di");
                                    ChildItem item = new ChildItem((String) tmpList.get(i));
                                    categoriesList.get(categoryIndex).add(item);
                                }
                                setListItems();
                            }
                        }
                    }
                } else {
                    //
                }
            }
        });
    }

    void setButtonColor() {
        buttonList.add(button1);
        buttonList.add(button2);
        buttonList.add(button3);
        buttonList.add(button4);
        buttonList.add(button5);
        buttonList.add(button6);
        buttonList.add(button7);

        for (int i=0; i<7; i++) {
            Button button = buttonList.get(i);
            int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    editor.putInt("calendarIdx", finalI);
                    editor.putString("date", dateList.get(finalI));
                    editor.commit();
                    finish(); //현재 액티비티 종료 실시
                    overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                    startActivity(intent); //현재 액티비티 재실행 실시
                    overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                }
            });
        }

        Drawable yellowBg = ResourcesCompat.getDrawable(getResources(), R.drawable.yellow_rounded_box, null);
        Drawable whiteBg = ResourcesCompat.getDrawable(getResources(), R.drawable.white_rounded_box, null);

        for (int i=0; i<7; i++) {
            Button button = buttonList.get(i);
            if (i == calendarIdx)
                button.setBackground(yellowBg);
            else {
                button.setBackground(whiteBg);
            }
        }
    }

}