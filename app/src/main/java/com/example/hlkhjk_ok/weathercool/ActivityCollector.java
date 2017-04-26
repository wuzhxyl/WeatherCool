package com.example.hlkhjk_ok.weathercool;

import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * Created by hlkhjk_ok on 17/4/14.
 */

public class ActivityCollector {
    public static ArrayList<AppCompatActivity> activities = new ArrayList<AppCompatActivity>();

    public static void addActivity(AppCompatActivity activity) {
        activities.add(activity);
    }

    public static void removeActivity(AppCompatActivity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (AppCompatActivity activity:activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
