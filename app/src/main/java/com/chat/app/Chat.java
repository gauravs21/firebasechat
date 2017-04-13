package com.chat.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;


/*
 * Created by kopite on 27/3/17.
 */

public class Chat extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
