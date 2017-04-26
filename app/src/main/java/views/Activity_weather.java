package views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.hlkhjk_ok.weathercool.R;

import model.dbLocation;

/**
 * Created by hlkhjk_ok on 17/4/25.
 */

public class Activity_weather extends AppCompatActivity {
    private static final String TAG = "WeatherCool";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherfull);

        Intent intent = getIntent();
        dbLocation location = (dbLocation) intent.getSerializableExtra("data");

        Log.d(TAG, "onCreate: ");
    }

}
