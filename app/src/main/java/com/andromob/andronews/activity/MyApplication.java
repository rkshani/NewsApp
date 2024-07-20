package com.andromob.andronews.activity;

import android.app.Application;
import android.os.StrictMode;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MyApplication extends Application {
    private static MyApplication sInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        if (!FacebookSdk.isFullyInitialized()){
            FacebookSdk.fullyInitialize();
        }
        AppEventsLogger.activateApp(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static MyApplication getInstance() {
        return sInstance ;
    }

}