package com.chat.app.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import com.chat.app.R;
import com.chat.app.model.ChatMessage;
import com.chat.app.model.DocumentModel;
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
    StorageReference imageRef;
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
    int count ;
    private ValueEventListener chatThreadListener, messageListener, typingListener, statusListener;
    private Uri captureImageUri, selectedImageUri;

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
        adapter = new MessageAdapter(ChatScreen.this, messageArrayList, count);
        rvMessage.setAdapter(adapter);
        toEmail = getIntent().getStringExtra(EMAIL);
        toUserId = getIntent().getStringExtra(USER_ID);
        fromuserId = PrefsUtil.getUserId(this);
        fromEmail = PrefsUtil.getEmail(this);
//        Log.e("DB", fromuserId);

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
                    boolean isRead = false;
                    final String messageKey = messageRef.child(chatKey).child("messages").push().getKey();
                    final ChatMessage message = new ChatMessage(messageBody, toEmail, fromEmail,
                            time, Constants.MSG_TYPE.NORMAL, fileSize, null, messageStatus, isRead);


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
                    sendTypingStatus(false);
//                    handleTextEmpty();
                } else {
//                    handleTextNotEmpty();

                    sendTypingStatus(false);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.postDelayed(this, 4000);
                            if (message.equals(s.toString())) {
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
                getStatus();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageThreadListener();
        getStatus();
        count=0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_options, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sendTypingStatus(false);
        removeFirebaseListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.upload_document:
                searchDocuments();
                return true;
            case R.id.upload_image:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    imagePicker();

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Constants.REQUEST_CODE.REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            default:
                return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQUEST_CODE.REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imagePicker();
                } else {
                    Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result ok
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //for doc
                case Constants.REQUEST_CODE.GET_DOC:
//                    Log.e("DB", String.valueOf(data));

                    final DocumentModel document = (DocumentModel) data.getSerializableExtra(FILE);
                    uploadFile(document);
                    break;

                case Constants.REQUEST_CODE.SELECT_FILE:
                    selectedImageUri = data.getData();

//                    File file = new File(image_path);
//                    Log.e("DB" + "Name", image_path + " size " + file.length());

                    uploadImage(selectedImageUri);
                    break;

                case Constants.REQUEST_CODE.REQUEST_CAMERA:
                    uploadImage(captureImageUri);
//                    String capturedPath = UserUtils.getImagePath(this, captureImageUri);
//                    Uri uploadUri = Uri.fromFile(new File(capturedPath));
//                    File imageFile = new File(uploadUri.getPath());
//                    Log.e("DB" + "Name", capturedPath + "size "+ imageFile.length());
                    break;
            }
        }
    }

    private void getStatus() {
        statusListener = new ValueEventListener() {
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
        metaRef.addValueEventListener(statusListener);

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

    //adding typing Indicator to firebase message thread
    private void sendTypingStatus(boolean isTyping) {
        if (isTyping) {
            messageRef.child(chatKey).child("typingIndicator").child(fromuserId).setValue(true);
        } else {
            messageRef.child(chatKey).child("typingIndicator").child(fromuserId).setValue(false);
        }
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
//                    Log.e(TAG + " chat0", chatKey);

                    //if chat is for first time between users
                    if (UserUtils.isNull(chatKey)) {
                        conversation.child(fromuserId).child("conversation").child(toUserId).setValue(newThreadKey);
                        conversation.child(toUserId).child("conversation").child(fromuserId).setValue(newThreadKey);
//                        Log.e(TAG + " chat1", chatKey);
                        observeTyping();


                        chatListener(newThreadKey);
                    } else {
//                        Log.e(TAG + " chat2", chatKey);
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

    private void chatListener(final String chatKey) {


        query = messageRef.child(chatKey).child("messages").orderByChild("timestamp");
        messageListener = (new ValueEventListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> chatMap;
                messageArrayList.clear();
//                Log.e("DBcount", String.valueOf(dataSnapshot.getChildrenCount()));
                for (DataSnapshot da : dataSnapshot.getChildren()) {
                    if (da.getValue() instanceof Map) {
                        chatMap = (HashMap<String, Object>)
                                da.getValue();
//                        Log.e("DBC", String.valueOf(chatMap));
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
                        String fromEmail = (String) messageMap.get("from");
                        Log.e("email", fromEmail);
                        if (fromEmail.equalsIgnoreCase(toEmail)) {
                            boolean isRead = ((boolean) messageMap.get("isRead"));
                            if (!isRead) {
                                count++;
                                DatabaseReference update = reference.child("chats").child(chatKey)
                                        .child("messages").child(da.getKey()).child("isRead");
                                update.setValue(true);
                                Log.e(TAG + "count", String.valueOf(count));
                            }


                        }
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
                rvMessage.scrollToPosition(rvMessage.getAdapter().getItemCount() - (1 + count));
                count = 0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query.addValueEventListener(messageListener);
        messageRef.child(chatKey).child("typingIndicator")
                .child(fromuserId).onDisconnect().setValue(false);
    }

    private void imagePicker() {
        final CharSequence[] items = {"Take Photo", "Choose from Library"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatScreen.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    captureImageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageUri);
                    startActivityForResult(intent, Constants.REQUEST_CODE.REQUEST_CAMERA);

                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            Constants.REQUEST_CODE.SELECT_FILE);
                }
            }
        });
        builder.show();
    }

    private void uploadImage(Uri imageUri) {
        String image_path = UserUtils.getImagePath(ChatScreen.this, imageUri);
        Uri uploadUri = Uri.fromFile(new File(image_path));
        final File imageFile = new File(uploadUri.getPath());
        final String fileName = imageFile.getName();
        Log.e("DB" + "Name", image_path + "size " + imageFile.length() + " name " + imageFile.getName());
        imageRef = storageRef.child("images/" + fromuserId + imageFile.getName());


        imageRef.putFile(uploadUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                boolean isRead = false;
                final String messageKey = messageRef.child(chatKey).child("messages").push().getKey();

                ChatMessage message = new ChatMessage(fileName, toEmail, fromEmail,
                        time, Constants.MSG_TYPE.IMAGE, imageFile.length(), downloadUrl.toString(), messageStatus, isRead);
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

    private void searchDocuments() {
//        startActivity();
        startActivityForResult(new Intent(ChatScreen.this, DocumentList.class), Constants.REQUEST_CODE.GET_DOC);
    }

    private void uploadFile(final DocumentModel document) {
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
//                        Log.e("DBData", document.getType());
            Uri uploadUri = Uri.fromFile(new File(document.getPath()));
            docRef = storageRef.child("documents/" + document.getName());

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
                    boolean isRead = false;
                    final String messageKey = messageRef.child(chatKey).child("messages").push().getKey();

                    ChatMessage message = new ChatMessage(document.getName(), toEmail, fromEmail,
                            time, messageType, document.getFileLength(), downloadUrl.toString(), messageStatus, isRead);
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
    }


    private void removeFirebaseListeners() {
        conversation.removeEventListener(chatThreadListener);
        query.removeEventListener(messageListener);
        metaRef.removeEventListener(statusListener);
    }

}
