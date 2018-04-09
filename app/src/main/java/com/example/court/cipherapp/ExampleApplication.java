package com.example.court.cipherapp;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by court on 6/04/2018.
 */

public class ExampleApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }
    public static RefWatcher getRefWatcher(Context context) {
        ExampleApplication application = (ExampleApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;
}