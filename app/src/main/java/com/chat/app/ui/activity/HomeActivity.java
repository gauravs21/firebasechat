package com.chat.app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.chat.app.R;
import com.chat.app.model.User;
import com.chat.app.ui.adapter.UserAdapter;
import com.chat.app.utility.PrefsUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    User user;
    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference newRef = firebaseDatabase.child("user");
    TextView textView;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager manager;
//    boolean isPresent=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        user = new User(PrefsUtil.getEmail(this), PrefsUtil.getFcm(this), PrefsUtil.getUserId(this));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        newRef.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user=dataSnapshot.getValue(User.class);
                Log.e("DB1233", String.valueOf(dataSnapshot.getChildrenCount()));
                Map<String, Object> userMap = (HashMap<String, Object>)
                        dataSnapshot.getValue();
                ArrayList<User> userArrayList = new ArrayList<>();
                for (Object object :
                        userMap.values()) {
                    if (object instanceof Map) {
                        Map<String, Object> mapObj = (Map<String, Object>) object;
                        User itemsReceived = new User();
                        if (!mapObj.get("email").equals(PrefsUtil.getEmail(HomeActivity.this))) {
                            itemsReceived.setEmail((String) mapObj.get("email"));
                            itemsReceived.setDevice_token((String) mapObj.get("device_token"));
                            itemsReceived.setUserId((String)mapObj.get("userId"));
                            userArrayList.add(itemsReceived);
                        }

                    }
                }
                Log.e("DB", String.valueOf(userArrayList.size()));
                adapter = new UserAdapter(HomeActivity.this, userArrayList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                DatabaseReference userRef=newRef.push();
//                if (dataSnapshot.getValue() != null) {
//                    for (DataSnapshot data :
//                            dataSnapshot.getChildren()) {
//                        if (data.child("email").getValue().equals(user.getEmail())) {
//                            Log.e("DBSS", "value exists");
//                            isPresent=true;
//                            break;
//                        } else {
//                            Log.e("DB", "in else");
//                            isPresent=false;
//                        }
//                    }
//                    if (!isPresent)
//                        userRef.setValue(user);
//                }
//                else {
//                    Log.e("DB", "in outer else");
//
//                    userRef.setValue(user);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
