package util;

import android.renderscript.Double2;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.hlkhjk_ok.weathercool.MyApplication;

import model.dbLocation;

/**
 * Created by hlkhjk_ok on 17/4/18.
 */

public class LocationUtil {
    private static String TAG = "WeatherCool";
    private static String lat = "";
    private static String lng = "";
    private static String cityname = "";

    private static dbLocation loc = new dbLocation();
    private static AMapLocationClient mLocationClient = null;
    private static AMapLocationClientOption mLocationOption = null;
    private static AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                Log.d(TAG, "onLocationChanged: " + aMapLocation.toString());
                if (aMapLocation.getErrorCode() == 0) {
                    lat = Double.toString(aMapLocation.getLatitude());
                    lng = Double.toString(aMapLocation.getLongitude());
                    cityname = aMapLocation.getCity();

                    loc.setLatitude(aMapLocation.getLatitude());
                    loc.setLongitude(aMapLocation.getLongitude());

                    String city = aMapLocation.getCity();
                    if (city.isEmpty()) city = "";
                    String district = aMapLocation.getDistrict();
                    if (district.isEmpty()) district = "";
                    String street = aMapLocation.getStreet();
                    if (street.isEmpty()) street = "";

                    loc.setCity(city);
                    loc.setLatitude(aMapLocation.getLatitude());
                    loc.setLongitude(aMapLocation.getLongitude());
                    loc.setLatlng(Double.toString(aMapLocation.getLatitude())+","+ Double.toString(aMapLocation.getLongitude()));
                } else {
                    Log.d(TAG, "onLocationChanged: ERROR" + aMapLocation.getErrorCode());
                    loc.setLatitude(30.012);
                    loc.setLongitude(120.12);
                    loc.setCity("杭州");
                    loc.setLatlng("30.012，120.12");
                }
            }
        }
    };

    public static void startUpdateLocation() {
        synchronized ("") {
            if (mLocationClient != null && mLocationClient.isStarted()) {
                return;
            }

            mLocationClient = new AMapLocationClient(MyApplication.getContext());
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setInterval(200);
            mLocationOption.setLocationCacheEnable(false);

            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(mLocationListener);
            mLocationClient.startLocation();
        }
    }

    public static void stopUpdateLocation() {
        synchronized ("") {
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
            }
        }
    }

    public static dbLocation getCurrdbLocation() {
        return loc;
    }

    public static void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }
}
