package com.chat.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.app.Chat;
import com.chat.app.R;
import com.chat.app.model.ChatMessage;
import com.chat.app.model.DocumentModel;
import com.chat.app.model.User;
import com.chat.app.ui.adapter.MessageAdapter;
import com.chat.app.utility.Constants;
import com.chat.app.utility.PrefsUtil;
import com.chat.app.utility.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatScreen extends AppCompatActivity {
    long startnow, endnow, userStatus;
    public static final String TAG = "DB";
    public static final String EMAIL = "email";
    public static final String USER_ID = "user_id";
    public static final String FILE = "file";
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference messageRef = reference.child("chats");
    DatabaseReference conversation = reference.child("conversationRecord");
    DatabaseReference newChat = messageRef.push();
    StorageReference docRef;
    DatabaseReference metaRef;
    String toEmail, fromEmail, fileSize, chatKey, newThreadKey, toUserId, fromuserId;
    public static boolean IS_TYPING = false;
    FrameLayout frameLayout;
    EditText etMessage;
    TextView tvName, tvStatus, tvIndicator;
    RecyclerView rvMessage;
    RecyclerView.Adapter adapter;
    ArrayList<ChatMessage> messageArrayList = new ArrayList<>();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String messageType;
    Query query;
    private ValueEventListener chatThreadListener, messageListener, typingListener, statusListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_screen);
        ActionBar actionBar = getSupportActionBar();
        toEmail = getIntent().getStringExtra(EMAIL);
        toUserId = getIntent().getStringExtra(USER_ID);
        fromuserId = PrefsUtil.getUserId(this);
        fromEmail = PrefsUtil.getEmail(this);
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_actionbar);
            tvName = (TextView) findViewById(R.id.custom_actionBar_tv_username);
            tvStatus = (TextView) findViewById(R.id.actionbar_tv_status);
            tvIndicator = (TextView) findViewById(R.id.actionbar_tv_typing);
            tvName.setText(toEmail);
        }

        metaRef = reference.child("meta")
                .child(toUserId).child("status");

        frameLayout = (FrameLayout) findViewById(R.id.frag_chat_fl_send);
        etMessage = (EditText) findViewById(R.id.frag_chat_et_input);
        rvMessage = (RecyclerView) findViewById(R.id.rvMessages);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvMessage.setLayoutManager(manager);
        adapter = new MessageAdapter(ChatScreen.this, messageArrayList);
        rvMessage.setAdapter(adapter);
        toEmail = getIntent().getStringExtra(EMAIL);
        toUserId = getIntent().getStringExtra(USER_ID);
        fromuserId = PrefsUtil.getUserId(this);
        fromEmail = PrefsUtil.getEmail(this);
        Log.e("DB", fromuserId);

        newThreadKey = newChat.getKey();
        //create thread

//        messageThreadListener();

        //create thread
//        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                startnow = android.os.SystemClock.uptimeMillis();
//                //check if chat is already created and stop user creating new thread every time
//                if (dataSnapshot.getValue() != null) {
//                    for (DataSnapshot d :
//                            dataSnapshot.getChildren()) {
//                        //chat created by other user
//                        if (d.child("from").getValue().equals(toEmail)
//                                && d.child("to").getValue().equals(fromEmail)) {
//                            Log.e("DBC12", "other created");
//                            chatKey = d.getKey();
//                            chatListener(chatKey);
//                            newUser = false;
//                            break;
//                        }
//                        //chat created by you
//                        else if (d.child("to").getValue().equals(toEmail)
//                                && d.child("from").getValue().equals(fromEmail)) {
//                            Log.e("DBC12", "you created");
//                            endnow= android.os.SystemClock.uptimeMillis();
//                            chatKey = d.getKey();
//                            chatListener(chatKey);
//                            newUser = false;
//                            break;
//                        }
//                    }
//
//                }
//                //create new thread
//                if (newUser) {
//                    Log.e("DBC12", "new chat created");
//                    newChat.child("to").setValue(toEmail);
//                    newChat.child("from").setValue(PrefsUtil.getEmail(ChatScreen.this));
//                    newChat.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            chatKey = dataSnapshot.getKey();
//                            endnow= android.os.SystemClock.uptimeMillis();
//                            Log.e("DB", chatKey);
//                            chatListener(chatKey);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//
//                Log.e("DB",startnow + " end "+endnow);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        //add message


        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messageBody = etMessage.getText().toString().trim();
                if (messageBody.length() > 0) {
                    long time = System.currentTimeMillis();

                    long messageStatus = 0;
                    int fileSize = 0;
                    final ChatMessage message = new ChatMessage(messageBody, toEmail, fromEmail,
                            time, Constants.MSG_TYPE.NORMAL, fileSize, null, messageStatus);
                    final String messageKey = messageRef.child(chatKey).child("messages").push().getKey();

                    messageRef.child(chatKey).child("messages").child(messageKey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "completed");
                            messageRef.child(chatKey).child("messages").child(messageKey).child("messageStatus").setValue(1);
                            Log.e(TAG, messageRef.child(chatKey).getKey());

                        }
                    });
                    etMessage.setText("");
//                    View view = getCurrentFocus();
//                    if (view != null) {
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    }
                }
            }
        });

        //textwatcher for typing indicator
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                final String message = etMessage.getText().toString().trim();

                if (message.length() == 0) {
                    IS_TYPING = false;
                    sendTypingStatus(IS_TYPING);
//                    handleTextEmpty();
                } else {
//                    handleTextNotEmpty();
                    IS_TYPING = true;
                    sendTypingStatus(IS_TYPING);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.postDelayed(this, 4000);
                            if (message.equals(s.toString())){
                                sendTypingStatus(false);
                            }

                        }
                    }, 4000);
                }

            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 60000);

                String activeTime = UserUtils.convertTimeStampToLastSeen(userStatus);
                tvStatus.setText(activeTime);
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageThreadListener();
        getStatus();


    }

    private void messageThreadListener() {
        chatThreadListener = (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e(TAG, String.valueOf(dataSnapshot));

                startnow = android.os.SystemClock.uptimeMillis();
                //check if user has chat history
                if (dataSnapshot.getValue() != null) {
                    //check if chat is already created and stop user creating new thread every time
                    chatKey = String.valueOf(dataSnapshot.child(fromuserId).child("conversation").child(toUserId).getValue());
                    Log.e(TAG + " chat0", chatKey);

                    //if chat is for first time between users
                    if (UserUtils.isNull(chatKey)) {
                        conversation.child(fromuserId).child("conversation").child(toUserId).setValue(newThreadKey);
                        conversation.child(toUserId).child("conversation").child(fromuserId).setValue(newThreadKey);
                        Log.e(TAG + " chat1", chatKey);
                        observeTyping();


                        chatListener(newThreadKey);
                    } else {
                        Log.e(TAG + " chat2", chatKey);
                        chatListener(chatKey);
                        observeTyping();

                    }

                } else {
//                    Log.e(TAG + " chat3", chatKey);
                    conversation.child(fromuserId).child("conversation").child(toUserId).setValue(newThreadKey);
                    conversation.child(toUserId).child("conversation").child(fromuserId).setValue(newThreadKey);
                    chatListener(newThreadKey);
                    observeTyping();

                }
                endnow = android.os.SystemClock.uptimeMillis();
                Log.e("DB", startnow + " end " + endnow);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        conversation.addValueEventListener(chatThreadListener);

    }


    //adding typing Indicator to firebase message thread
    private void sendTypingStatus(boolean isTyping) {
        if (isTyping) {
            messageRef.child(chatKey).child("typingIndicator").child(fromuserId).setValue(true);
        } else {
            messageRef.child(chatKey).child("typingIndicator").child(fromuserId).setValue(false);
        }
    }


    private void chatListener(final String chatKey) {


        query = messageRef.child(chatKey).child("messages").orderByChild("timestamp");
        messageListener = (new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> chatMap;
                messageArrayList.clear();
                Log.e("DBcount", String.valueOf(dataSnapshot.getChildrenCount()));

                for (DataSnapshot da : dataSnapshot.getChildren()) {
                    if (da.getValue() instanceof Map) {
                        chatMap = (HashMap<String, Object>)
                                da.getValue();
                        Log.e("DBC", String.valueOf(chatMap));
                        Map<String, Object> messageMap = chatMap;
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setFrom((String) messageMap.get("from"));
                        chatMessage.setTo((String) messageMap.get("to"));
                        chatMessage.setMessageBody((String) messageMap.get("messageBody"));
                        chatMessage.setMessageType((String) messageMap.get("messageType"));
                        chatMessage.setTimestamp((long) messageMap.get("timestamp"));
                        chatMessage.setFileLength((long) messageMap.get("fileLength"));
                        chatMessage.setDownloadLink((String) messageMap.get("downloadLink"));
                        chatMessage.setMessageStatus((long) messageMap.get("messageStatus"));
                        messageArrayList.add(chatMessage);
                    }
                }
//
//
//                Map<String, Object> chatMap = (HashMap<String, Object>)
//                        dataSnapshot.getValue();
//
//                for (Object ob : chatMap.values()) {
//
//                    if (ob instanceof Map) {
//                        Map<String, Object> messageMap = (Map<String, Object>) ob;
//                        ChatMessage chatMessage = new ChatMessage();
//                        chatMessage.setFrom((String) messageMap.get("from"));
//                        chatMessage.setTo((String) messageMap.get("to"));
//                        chatMessage.setMessageBody((String) messageMap.get("messageBody"));
//                        chatMessage.setTimestamp((long) messageMap.get("timestamp"));
//                        Log.e("DB",(String) messageMap.get("messageBody"));
//                        messageArrayList.add(chatMessage);
//                    }
//                }
                adapter.notifyDataSetChanged();
                rvMessage.scrollToPosition(rvMessage.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query.addValueEventListener(messageListener);
        messageRef.child(chatKey).child("typingIndicator")
                .child(fromuserId).onDisconnect().setValue(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.upload_document:
                searchDocuments();
                return true;
            default:
                return false;
        }
    }

    private void searchDocuments() {
//        startActivity();
        startActivityForResult(new Intent(ChatScreen.this, DocumentList.class), Constants.REQUEST_CODE.GET_DOC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result ok
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //for doc
                case Constants.REQUEST_CODE.GET_DOC: {
                    Log.e("DB", String.valueOf(data));
                    final DocumentModel document = (DocumentModel) data.getSerializableExtra(FILE);

                    switch (document.getType()) {
                        case Constants.DocumentExtension.TXT:
                            messageType = Constants.MSG_TYPE.TEXT;
                            break;
                        case Constants.DocumentExtension.PPT:
                            messageType = Constants.MSG_TYPE.PPT;
                            break;
                        case Constants.DocumentExtension.PPTX:
                            messageType = Constants.MSG_TYPE.PPT;
                            break;
                        case Constants.DocumentExtension.XLS:
                            messageType = Constants.MSG_TYPE.EXCEL_SHEET;
                            break;
                        case Constants.DocumentExtension.XLSX:
                            messageType = Constants.MSG_TYPE.EXCEL_SHEET;
                            break;
                        case Constants.DocumentExtension.DOC:
                            messageType = Constants.MSG_TYPE.DOC;
                            break;
                        case Constants.DocumentExtension.DOCX:
                            messageType = Constants.MSG_TYPE.DOC;
                            break;
                        case Constants.DocumentExtension.PDF:
                            messageType = Constants.MSG_TYPE.PDF;
                            break;
                    }
                    if (document.getFileLength() > Constants.MAX_SIZE)
                        Toast.makeText(this, "File is too large", Toast.LENGTH_SHORT).show();
                    else {
                        fileSize = UserUtils.getFileSize(document.getFileLength());
                        Log.e("DBData", document.getType());
                        Uri uploadUri = Uri.fromFile(new File(document.getPath()));
                        docRef = storageRef.child(document.getName());
                        docRef.putFile(uploadUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @SuppressWarnings("VisibleForTests")
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                Log.e("DB", String.valueOf(progress));
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @SuppressWarnings("VisibleForTests")
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                long time = System.currentTimeMillis();
                                assert downloadUrl != null;
                                long messageStatus = 1;
                                final String messageKey = messageRef.child(chatKey).child("messages").push().getKey();

                                ChatMessage message = new ChatMessage(document.getName(), toEmail, fromEmail,
                                        time, messageType, document.getFileLength(), downloadUrl.toString(), messageStatus);
                                messageRef.child(chatKey).child("messages").child(messageKey).setValue(message)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                messageRef.child(chatKey).child("messages").child(messageKey).child("messageStatus").setValue(1);
                                            }
                                        });
                                Log.e("DB", String.valueOf(downloadUrl));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatScreen.this, "failed to upload", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendTypingStatus(false);
        removeFirebaseListeners();
    }

    private void observeTyping() {
        typingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, String.valueOf(dataSnapshot.getValue()));
                if (dataSnapshot.getValue() != null) {
                    boolean typing = (boolean) dataSnapshot.getValue();
                    if (typing)
                        tvStatus.setText(R.string.typing);
                    if (!typing)
                        getStatus();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        messageRef.child(chatKey).child("typingIndicator").child(toUserId).addValueEventListener(typingListener);

    }

    private void getStatus() {
        statusListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG + "typing", String.valueOf(dataSnapshot.getValue()));
                try {
                    String userStatus = (String) dataSnapshot.getValue();
                    if (userStatus.equalsIgnoreCase("online"))
                        tvStatus.setText(userStatus);
                } catch (ClassCastException e) {
                    userStatus = (long) dataSnapshot.getValue();
                    String activeTime = UserUtils.convertTimeStampToLastSeen(userStatus);
                    tvStatus.setText(activeTime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        metaRef.addValueEventListener(statusListner);

    }

    private void removeFirebaseListeners() {
        conversation.removeEventListener(chatThreadListener);
        query.removeEventListener(messageListener);
        metaRef.removeEventListener(statusListner);
    }


}
