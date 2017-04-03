package com.chat.app;

import android.app.Application;
import android.support.multidex.MultiDex;


/*
 * Created by kopite on 27/3/17.
 */

public class Chat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
}
