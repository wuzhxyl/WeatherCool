package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hlkhjk_ok on 17/4/21.
 */

public class LocationDB extends SQLiteOpenHelper {
    public static final String TAG= "WeatherCool";
    public static final String CREATE_LOCATION = "create table locdb ("
            + "id integer primary key autoincrement, "
            + "lat real, "
            + "lng real, "
            + "location text, "
            + "city text )";

    private Context mContext;

    public LocationDB(Context context, String dbname, SQLiteDatabase.CursorFactory factory, int ver) {
        super(context, dbname, factory, ver);
        Log.d("WeatherCool", "LocationDB: 创建新的");
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: ");
        db.execSQL(CREATE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
