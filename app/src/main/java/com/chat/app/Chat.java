package com.chat.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.chat.app.utility.PrefsUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import io.realm.Realm;


/*
 * Created by kopite on 27/3/17.
 */

public class Chat extends Application {
    DatabaseReference reference;
    DatabaseReference metaRef;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("App", "terminated");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("App", "onCreate");
        Realm.init(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        reference = FirebaseDatabase.getInstance().getReference();
        if (PrefsUtil.getLogin(Chat.this)) {
            metaRef = reference.child("meta")
                    .child(PrefsUtil.getUserId(this)).child("status");

            metaRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
        }
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    private final class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        private int runningActivities = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//            Log.e("App", "onActivityCreated");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            runningActivities++;
            if (runningActivities >= 1) {
                if (PrefsUtil.getLogin(Chat.this)) {
                    Log.e("DB",PrefsUtil.getUserId(Chat.this));
                    metaRef = reference.child("meta")
                            .child(PrefsUtil.getUserId(Chat.this)).child("status");
                    metaRef.setValue("online");
                    Log.e("Appn", "APP IN FOREGROUND");
                }
            }

        }

        @Override
        public void onActivityResumed(Activity activity) {
//            Log.e("App", "onActivityResumed");
        }

        @Override
        public void onActivityPaused(Activity activity) {
//            Log.e("App", "onActivityPaused");

        }

        @Override
        public void onActivityStopped(Activity activity) {
            runningActivities--;
            if (runningActivities == 0) {
                if (PrefsUtil.getLogin(Chat.this)) {
                    Log.e("Appn", "APP IN Background");
                    metaRef = reference.child("meta")
                            .child(PrefsUtil.getUserId(Chat.this)).child("status");

                    metaRef.setValue(ServerValue.TIMESTAMP);
                }
            }

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//            Log.e("App", "onActivitySaveInstanceState");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
//            Log.e("App", "onActivityDestroyed");
        }
    }
}
