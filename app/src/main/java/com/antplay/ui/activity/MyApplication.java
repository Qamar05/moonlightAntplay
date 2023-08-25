package com.antplay.ui.activity;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static void activityDestroy(){
        Log.i("test_desroyy" , "wekbvjbherv");
    }
}
