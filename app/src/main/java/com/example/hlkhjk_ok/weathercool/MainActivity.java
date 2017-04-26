package com.example.hlkhjk_ok.weathercool;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import database.dbutil;
import model.WeatherEnum;
import model.WeatherInfo;
import model.dbLocation;
import uimodel.NotifyChangeListener;
import uimodel.WeatherAdapater;
import util.HttpUtil;
import util.HttpUtil.HttpCallbackListener;
import util.LocationUtil;
import util.ViewUtil;
import util.caiyunWeather;
import views.Activity_forcityinfo;
import views.Activity_weather;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    final static String TAG = "WeatherCool";
    private final static int city_Request_Code = 1;
    private List<dbLocation> loclist = null;
    private Object synObject = new Object();
    private List<WeatherInfo> weatherList = new ArrayList<WeatherInfo>();
    private WeatherAdapater adapater;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WeatherEnum.OPENWEATHERMAP.ordinal()) {
                weatherList.add((WeatherInfo) msg.obj);
                adapater.notifyDataSetChanged();
                Log.d(TAG, "handleMessage: 刷新数据了" + weatherList.size());
            } else if (msg.what == WeatherEnum.CAIYUNTIANQI.ordinal()) {
                Log.d(TAG, "handleMessage: 改变事件");
              adapater.notifyDataSetChanged();
            } else {
                Log.d(TAG, "handleMessage: ERROR " + (String)msg.obj);
            } 
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtil.setFullScreen(this);
        setContentView(R.layout.activity_home);

        initData();
    }

    private void initData() {
        final ListView listview = (ListView) findViewById(R.id.homecontainer);
        adapater = new WeatherAdapater(this, R.layout.weather_home_item, weatherList, new NotifyChangeListener() {
            @Override
            public void change(int position) {
                if (weatherList.size() == 1 || loclist == null || weatherList == null) return;
                dbLocation currloc = loclist.get(position);
                loclist.remove(position);
                weatherList.remove(position);

                dbutil.removeDBInfo(currloc);
                adapater.notifyDataSetChanged();
            }
        });
        listview.setAdapter(adapater);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Toast.makeText(MyApplication.getContext(), "想要飞起?", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView addbox = (ImageView) findViewById(R.id.addbox);
        addbox.setOnClickListener(this);

        if (SplashActivity.isUpdateLocation()) {
            dbLocation loc = LocationUtil.getCurrdbLocation();
            LocationUtil.stopUpdateLocation();

            dbutil.insertLocationDB(loc);
            reFreshWeatherInfo(loc);
        } else {
            loclist = dbutil.getAllLocationInfo();
            for (dbLocation loc:loclist) {
                reFreshWeatherInfo(loc);
            }
        }
    }

    public void requestWeather(final String lat, String lng, final HttpUtil.HttpCallbackListener listener) {
        final String url = caiyunWeather.getCaiYunUrl(lat, lng);
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (synObject) {
                    HttpsURLConnection connection = null;
                    try {
                        URL urlC = new URL(url);
                        connection = (HttpsURLConnection) urlC.openConnection();
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);

                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder();
                        String str = "";

                        while ((str = reader.readLine()) != null) {
                            result.append(str);
                        }

                        if (result.toString().length() != 0) {
                            listener.onSuccess(result.toString());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        listener.onError(e.getMessage());
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }
        }).start();
    }

    public void reFreshWeatherInfo(final dbLocation location) {
        String lat = Double.toString(location.getLatitude());
        String lng = Double.toString(location.getLongitude());
        final String cityname = location.getCity();

        requestWeather(lat, lng, new HttpCallbackListener() {
            @Override
            public void onSuccess(String value) {
                Log.d(TAG, "onSuccess: " + value);
                WeatherInfo weather = caiyunWeather.parseJsonObjectWeather(value);
                if (weather == null) return;

                weather.setCityname(cityname);
                Log.d(TAG, "onSuccess: weather " + weather.toString());

                Message msg = new Message();
                msg.what = WeatherEnum.OPENWEATHERMAP.ordinal();
                msg.obj = weather;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(String value) {
                Message msg = new Message();
                msg.what = WeatherEnum.ERRORWEATHER.ordinal();
                msg.obj  = value;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addbox) {
            Log.d(TAG, "onClick: addbox");
            Intent intent = new Intent(this, Activity_forcityinfo.class);
            startActivityForResult(intent, city_Request_Code);
        }
//        } else if (v.getId() == R.id.delBtn) {
////            adapater.getPosition()
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case city_Request_Code:
                if (resultCode == RESULT_OK) {
                    dbLocation location = (dbLocation) data.getSerializableExtra("data");
                    Log.d(TAG, "onActivityResult: getData: " + location.toString());
                    dbutil.insertLocationDB(location);
                    reFreshWeatherInfo(location);
                }
        }

       super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: " + this.getClass().toString());
        super.onDestroy();
        ActivityCollector.finishAll();
        LocationUtil.onDestroy();
    }

}