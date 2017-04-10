package com.chat.app;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.google.firebase.database.FirebaseDatabase;


/*
 * Created by kopite on 27/3/17.
 */

public class Chat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
