package com.chat.app.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.chat.app.R;
import com.chat.app.model.ChatMessage;
import com.chat.app.ui.adapter.MessageAdapter;
import com.chat.app.utility.PrefsUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatScreen extends AppCompatActivity {

    public static final String EMAIL = "email";
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference messageRef = reference.child("chats");
    DatabaseReference newChat = messageRef.push();
    String toEmail, fromEmail;
    String chatKey;
    boolean newUser = true;
    FrameLayout frameLayout;
    EditText etMessage;
    RecyclerView rvMessage;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        frameLayout = (FrameLayout) findViewById(R.id.frag_chat_fl_send);
        etMessage = (EditText) findViewById(R.id.frag_chat_et_input);
        rvMessage = (RecyclerView) findViewById(R.id.rvMessages);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvMessage.setLayoutManager(manager);
        toEmail = getIntent().getStringExtra(EMAIL);
        fromEmail = PrefsUtil.getEmail(this);
        Log.e("DB", toEmail);
        //create thread
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check if chat is already created and stop user creating new thread every time
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot d :
                            dataSnapshot.getChildren()) {
                        //chat created by other user
                        if (d.child("from").getValue().equals(toEmail)
                                && d.child("to").getValue().equals(fromEmail)) {
                            Log.e("DBC12", "other created");
                            chatKey = d.getKey();
                            chatListener(chatKey);
                            newUser = false;
                            break;
                        }
                        //chat created by you
                        else if (d.child("to").getValue().equals(toEmail)
                                && d.child("from").getValue().equals(fromEmail)) {
                            Log.e("DBC12", "you created");
                            chatKey = d.getKey();
                            chatListener(chatKey);
                            newUser = false;
                            break;
                        }
                    }

                }
                //create new thread
                if (newUser) {
                    Log.e("DBC12", "new chat created");
                    newChat.child("to").setValue(toEmail);
                    newChat.child("from").setValue(PrefsUtil.getEmail(ChatScreen.this));
                    newChat.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            chatKey = dataSnapshot.getKey();
                            Log.e("DB", chatKey);
                            chatListener(chatKey);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //add message
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageBody = etMessage.getText().toString().trim();
                if (messageBody.length() > 0) {
                    long time=System.currentTimeMillis();

                    ChatMessage message = new ChatMessage(messageBody, toEmail, fromEmail,time);
                    messageRef.child(chatKey).push().setValue(message);
                    etMessage.setText("");
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });


    }

    private void chatListener(String chatKey) {

        Query query = messageRef.child(chatKey).orderByChild("messageBody");

        query.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("DBChat", "child added");
                Log.e("DB1233", String.valueOf(dataSnapshot.getValue()));
                Map<String, Object> chatMap = (HashMap<String, Object>)
                        dataSnapshot.getValue();
                ArrayList<ChatMessage> messageArrayList = new ArrayList<>();
                messageArrayList.clear();
                for (Object ob : chatMap.values()) {

                    if (ob instanceof Map) {
                        Map<String, Object> messageMap = (Map<String, Object>) ob;
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setFrom((String) messageMap.get("from"));
                        chatMessage.setTo((String) messageMap.get("to"));
                        chatMessage.setMessageBody((String) messageMap.get("messageBody"));
                        chatMessage.setTimestamp((long) messageMap.get("timestamp"));
                        Log.e("DB",(String) messageMap.get("messageBody"));
                        messageArrayList.add(chatMessage);
                    }
                }
                Log.e("DB", String.valueOf(messageArrayList.size()));
                adapter = new MessageAdapter(ChatScreen.this, messageArrayList);
                rvMessage.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
