package com.sweng411.smashrun;

import android.app.Application;
import android.content.Context;


//This class is purely for letting the repo get the app context
public class App extends Application {

    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }
}