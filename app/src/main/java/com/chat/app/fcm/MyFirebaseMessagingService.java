package com.chat.app.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/*
 * Created by kopite on 29/3/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("DB","message received");
    }

    public MyFirebaseMessagingService() {
        super();
        Log.e("DB","something received");
    }
    
}
