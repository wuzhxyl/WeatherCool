package com.example.hlkhjk_ok.weathercool;

import android.app.Application;
import android.content.Context;

/**
 * Created by hlkhjk_ok on 17/4/10.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext() {
        return mContext;
    }

}
