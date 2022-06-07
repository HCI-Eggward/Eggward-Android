package com.example.eggward.Schedule;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.example.eggward.R;
import com.example.eggward.Schedule.domain.ChildItem;
import com.example.eggward.Schedule.domain.ParentItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleListAdapter extends BaseExpandableListAdapter {
    ArrayList<ParentItem> parentItems;
    ArrayList<ArrayList<ChildItem>> childItems;

    View view;
    Context context;

    public static FirebaseFirestore database = FirebaseFirestore.getInstance();
    String userEmail = "eggward@ewhain.net";

    ImageButton rewardImage;

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
                rewardImage = new ImageButton(context);
                Drawable drawable = ResourcesCompat.getDrawable(parent.getResources(), R.drawable.ic_rewards, null);
                rewardImage.setImageDrawable(drawable);
                if (todoContent.isChecked()) {
                    // 카테고리 만드는 화면 띄우기
                    new AlertDialog.Builder(context)
                            .setTitle("획득 내역")
                            .setMessage("30 Exp")
                            .setView(rewardImage)
                            .setPositiveButton("OK", (dialog, which) -> {
                                // do nothing
                            }).show();
                }

                database.collection("User").document(userEmail).collection("eggBreed").document("eggBreed")
                        .update("curExp", FieldValue.increment(30));

                database.collection("User").document(userEmail).collection("itemList").document("itemList")
                        .update("ball", FieldValue.increment(1), "clothes", FieldValue.increment(1), "food", FieldValue.increment(1));

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
