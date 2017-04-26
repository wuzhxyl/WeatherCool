package views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hlkhjk_ok.weathercool.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import model.dbLocation;
import util.HttpUtil;
import util.ViewUtil;

/**
 * Created by hlkhjk_ok on 17/4/13.
 */

public class Activity_forcityinfo extends AppCompatActivity {
    private ImageView okBtn;
    private ImageView backBtn;
    private EditText edittext;
    private static String keywords = "";
    private final static String TAG = "WeatherCool";
    private ArrayList<dbLocation> locationList = new ArrayList<dbLocation>();
    private ArrayAdapter<String> adapater = null;
    private ArrayList<String> listItems = new ArrayList<String>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                adapater.notifyDataSetChanged();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtil.setFullScreen(this);
        setContentView(R.layout.activity_citylist);

        adapater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        ((ImageView) findViewById(R.id.ok)).setVisibility(View.INVISIBLE);

        ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapater);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dbLocation location = locationList.get(position);

                Intent intent = new Intent();
                intent.putExtra("data", location);

                Log.d(TAG, "onItemClick: locdata " + location.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        edittext = (EditText) findViewById(R.id.edit);
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null) {
                    return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                }
                return false;
            }
        });

        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { Log.d(TAG, "beforeTextChanged: "); }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keywords = s.toString();
                Log.d(TAG, "onTextChanged: length" + keywords.length());
                if (keywords.length() == 0) return;
                String url = HttpUtil.getTypeAheadUrl(keywords);
                doRequestUrl(url, new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onSuccess(String value) {
                        locationList.clear();
                        listItems.clear();
                        HttpUtil.parseJsonLocationData(locationList, value, listItems);

                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onError(String value) {
                        Log.d(TAG, "onError: nothing to do");
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: ");
            }
        });
    }

    public void doRequestUrl(final String url_str, final HttpUtil.HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized(locationList){
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(url_str);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);

                        InputStream in = connection.getInputStream();
                        BufferedReader freader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder result = new StringBuilder("");
                        String str = "";
                        while ((str=freader.readLine()) != null) {
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
}
