package database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hlkhjk_ok.weathercool.MyApplication;

import java.util.ArrayList;

import model.dbLocation;

/**
 * Created by hlkhjk_ok on 17/4/21.
 */

public class dbutil {
    private static final String dbName = "locdb";
    private dbLocation location = new dbLocation();

    private static LocationDB dbHelper = new LocationDB(MyApplication.getContext(), dbName, null, 1);
    public static SQLiteDatabase getWritableDB() {
        SQLiteDatabase db = null;
        try {
            db=dbHelper.getWritableDatabase();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return db;
    }

    public static void insertLocationDB(dbLocation location) {
        SQLiteDatabase db = getWritableDB();
        if (db == null || !db.isOpen()) return;

        double lat=location.getLatitude();
        double lng = location.getLongitude();
        String latlng = Double.toString(lat) + "," + Double.toString(lng);

        ContentValues value = new ContentValues();

        value.put("lat", lat);
        value.put("lng", lng);
        value.put("location", latlng);
        value.put("city", location.getCity());
        db.insert(dbName, null, value);
    }

    public static void updateLocationDB(double lat, double lng, String city) {
        String latlng = "";
    }

    public static ArrayList<dbLocation> getAllLocationInfo() {
        String TAG = "WeatherCool";
        ArrayList<dbLocation> dblist = new ArrayList<dbLocation>();
        Log.d(TAG, "getAllLocationInfo: 1111111111111");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "getAllLocationInfo: 2222222222222");
        if (db == null) return dblist;
        if (!db.isOpen()) {
            Log.d(TAG, "getAllLocationInfo: 没有这个");
            return dblist;
        }
        Cursor cursor = db.query(dbName,null,null,null,null,null,null);
        dbLocation loc = null;
        Log.d("WeatherCool", "getAllLocationInfo: " + cursor.toString());
        if (cursor.moveToFirst()) {
            Log.d("WeatherCool", "getAllLocationInfo: ");
            do {
                loc = new dbLocation();
                String city = cursor.getString(cursor.getColumnIndex("city"));

                loc.setCity(city != null ? city : "");

                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lng = cursor.getDouble(cursor.getColumnIndex("lng"));
                String latlng = cursor.getString(cursor.getColumnIndex("location"));

                Log.d(TAG, "getAllLocationInfo: city " + city);
                Log.d(TAG, "getAllLocationInfo: latlng " + latlng);
                Log.d(TAG, "getAllLocationInfo: lat " + lat);
                Log.d(TAG, "getAllLocationInfo: lng " + lng);

                loc.setLatlng(latlng);
                loc.setLatitude(lat);
                loc.setLongitude(lng);
                loc.setCity(city);

                Log.d(TAG, "getAllLocationInfo: locdata " + loc.toString());
                dblist.add(loc);
            } while(cursor.moveToNext());
        } else {
            Log.d(TAG, "getAllLocationInfo: 无数据");
        }
        return dblist;
    }

    public static void doDelDBTable() {
        SQLiteDatabase db = getWritableDB();
        if (db != null) {
            db.execSQL("drop table if exists "+dbName);
        }
    }

    public static void removeDBInfo(dbLocation location) {
        if (location == null) return;
        SQLiteDatabase db = getWritableDB();
        if (db != null) {
            db.delete(dbName, "location == ?", new String[] {location.getLatlng()});
        }
    }

}
