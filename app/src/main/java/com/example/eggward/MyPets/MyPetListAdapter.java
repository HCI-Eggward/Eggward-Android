package com.example.eggward.MyPets;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eggward.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyPetListAdapter extends BaseAdapter {

    private String userEmail ="eggward@ewhain.net";

    public interface ListBtnClickListener{
        void onListBtnClick(int position);
    }

    private ListBtnClickListener listBtnClickListener ;

    ArrayList<CustomMypetListDto> MypetList = new ArrayList<>();

    public static FirebaseFirestore database = FirebaseFirestore.getInstance();

    List<Integer> levelList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();

    public MyPetListAdapter() {

    }


    @Override
    public int getCount() {
        return MypetList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        CustomViewHolder holder;

        if (rowView == null) {
            rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypet_list_item, null, false);
            holder = new CustomViewHolder();
            holder.petLevel = (TextView) rowView.findViewById(R.id.textview_level);
            holder.petName = (TextView) rowView.findViewById(R.id.textview_name);
            holder.imageBtn = (ImageButton) rowView.findViewById(R.id.image_pet);

            rowView.setTag(holder);

            CustomMypetListDto gridViewItem = MypetList.get(position);

            holder.petLevel.setText("Lv."+gridViewItem.getLevel());
            holder.petName.setText(gridViewItem.getName());

            holder.imageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MyPetDetailActivity.class);
                    intent.putExtra("name", holder.petName.getText());

                    ((MyPetListActivity) v.getTag()).startActivity(intent);
                }
            });
        }
        return rowView;
    }

        class CustomViewHolder {
            TextView petLevel;
            TextView petName;
            ImageButton imageBtn;
        }

    public void getMypetList(){
        CollectionReference ref = database.collection("User").document(userEmail).collection("petList");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String level = ( document.getData().get("level").toString());
                        String name = document.getId();

                        levelList.add(Integer.parseInt(level));
                        nameList.add(name);
                        Log.v("데이터",levelList.get(0).toString());
                        Log.v("이름",nameList.get(0));
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

        @Override
        public CustomMypetListDto getItem ( int position){
            return MypetList.get(position);
        }

        public void addItem (){
        Log.v("어댑터 추가","설공");
            for(int i=0 ; i<nameList.size();i++){
                CustomMypetListDto item = CustomMypetListDto
                        .builder()
                        .level(levelList.get(i))
                        .name(nameList.get(i))
                        .build();

                MypetList.add(item);
            }
        }
    }

