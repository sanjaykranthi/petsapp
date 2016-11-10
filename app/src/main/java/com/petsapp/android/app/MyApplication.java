package com.petsapp.android.app;

import android.app.Application;

import com.petsapp.android.helper.MyPreferenceManager;

/**
 * Created by WEB DESIGNING on 25-05-2016.
 */
public class MyApplication extends Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;

    private MyPreferenceManager pref;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }
}
