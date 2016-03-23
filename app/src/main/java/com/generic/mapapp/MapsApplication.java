package com.generic.mapapp;

import android.app.Application;
import android.content.Context;

public class MapsApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MapsApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MapsApplication.context;
    }
}