package com.example.eggward.Backlogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eggward.R;
import com.example.eggward.Schedule.domain.ChildItem;
import com.example.eggward.Schedule.domain.ParentItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BacklogListAdapter extends BaseExpandableListAdapter {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    String userEmail = "eggward@ewhain.net";

    ArrayList<String> todoContentList;

    ArrayList<ParentItem> parentItems;
    ArrayList<ArrayList<ChildItem>> childItems;

    View view;
    Context context;
    EditText dateText;

    @Override
    public int getGroupCount() {
        return parentItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (childItems != null)
            return childItems.get(groupPosition).size();
        else
            return 0;
    }

    @Override
    public ParentItem getGroup(int groupPosition) {
        return parentItems.get(groupPosition);
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition) {
        return childItems.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        Context context = parent.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.expandable_list_parent, parent, false);
        }

        TextView categoryTitle = view.findViewById(R.id.categoryTitle);
        categoryTitle.setText(getGroup(groupPosition).getCategoryTitle());

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        view = convertView;
        context = parent.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.expandable_list_child, parent, false);
        }

        CheckBox todoContent = view.findViewById(R.id.todoCheckBox);
        todoContent.setText(getChild(groupPosition, childPosition).getTodoContent());
        todoContent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dateText = new EditText(context);
                if (todoContent.isChecked()) {
                    Map<String, Object> todoArrayMap = new HashMap();
                    todoContentList = new ArrayList<>();
                    todoContentList.add("quiz 3");
                    todoArrayMap.put(dateText.getText().toString(), todoContentList);

                    // 카테고리 만드는 화면 띄우기
                    new AlertDialog.Builder(context)
                            .setTitle("일정에 Backlog 추가하기")
                            .setMessage("Backlog를 추가할 날짜를 입력해주세요.")
                            .setView(dateText)
                            .setPositiveButton("OK", (dialog, which) -> {
                                database.collection("User").document(userEmail).collection("todoList").document("AI").set(todoArrayMap, SetOptions.merge());
                            }).show();
                }
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addItem(int groupPosition, ChildItem item) {
        childItems.get(groupPosition).add(item);
    }

    public void removeChild(int groupPosition, int childPosition) {
        childItems.get(groupPosition).remove(childPosition);
    }
}
