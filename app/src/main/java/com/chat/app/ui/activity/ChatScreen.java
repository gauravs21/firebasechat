package com.chat.app.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.chat.app.Chat;
import com.chat.app.R;
import com.chat.app.model.ChatMessage;
import com.chat.app.utility.PrefsUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        frameLayout = (FrameLayout) findViewById(R.id.frag_chat_fl_send);
        etMessage = (EditText) findViewById(R.id.frag_chat_et_input);
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
                            chatKey=d.getKey();
                            newUser = false;
                            break;
                        }
                        //chat created by you
                        else if (d.child("to").getValue().equals(toEmail)
                                && d.child("from").getValue().equals(fromEmail)) {
                            Log.e("DBC12", "you created");
                            chatKey=d.getKey();
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
                            chatKey=dataSnapshot.getKey();
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
                    ChatMessage message = new ChatMessage(messageBody, toEmail, fromEmail);
                    messageRef.child(chatKey).push().setValue(message);
                    etMessage.setText("");
                }
            }
        });
    }
}
