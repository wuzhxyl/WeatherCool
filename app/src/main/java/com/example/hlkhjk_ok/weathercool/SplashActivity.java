package com.example.hlkhjk_ok.weathercool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Locale;

import database.dbutil;
import model.dbLocation;
import util.LocationUtil;
import util.ViewUtil;

/**
 * Created by hlkhjk_ok on 17/4/18.
 */

public class SplashActivity extends AppCompatActivity {
    private final static String TAG = "WeatherCool";
    private static ArrayList<dbLocation> loclist = null;
    private static boolean hasChecked = false;
    private Button cancelBtn;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                int delay = (int)msg.obj;
                cancelBtn.setText(delay + "s");
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtil.setFullScreen(this);
        setContentView(R.layout.activity_splash);
        cancelBtn = (Button) findViewById(R.id.delay);

        initPermission();
    }

    public void initPermission() {
        loclist = dbutil.getAllLocationInfo();

        ArrayList<String> list = new ArrayList<String>();
        if (loclist == null || loclist.size() == 0) {
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        int size = list.size();
        for (int i = size-1; i >= 0; i--) {
            if (ContextCompat.checkSelfPermission(MyApplication.getContext(), list.get(i)) == PackageManager.PERMISSION_GRANTED) {
                list.remove(i);
            }
        }

        size = list.size();
        if (size > 0) {
            String[] lastPerm = new String[size];

            for (int i = 0; i < size; i++) lastPerm[i] = list.get(i);
            requestPermissions(lastPerm, 1);
            return;
        }

        hasChecked = true;
        if (loclist.size() > 0) {
            goHome();
        } else if (loclist.size() == 0) {
            delay4s(1);
        }
    }

    public static boolean isUpdateLocation() {
        if (!hasChecked) return false;

        if (loclist != null) {
            return loclist.size() == 0;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            int size = permissions.length;

            if (grantResults.length > 0 )
            for (int i=0; i<size; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    delay4s(0);
                    return;
                }
            }

            hasChecked = true;
            if (isUpdateLocation()) {
                LocationUtil.startUpdateLocation();
                delay4s(0);
            } else {
                delay4s(1);
            }
            return;
        }
    }

    public void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void delay4s(final int seconds) {
        new Thread(new Runnable() {
            private int num = 4;

            @Override
            public void run() {
                try {
                    if (seconds == 0) {
                        while (num >= 1) {
                            num--;
                            Thread.sleep(1000);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = num;
                            handler.sendMessage(msg);
                        }
                    } else {
                        Thread.sleep(500);
                    }
                    goHome();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
