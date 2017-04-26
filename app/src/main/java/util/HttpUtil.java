package util;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import model.dbLocation;

/**
 * Created by hlkhjk_ok on 17/4/6.
 */

public class HttpUtil {
    private final static String TAG = "HttpUtil";
    public static void doRequestUrl(final String url_str, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
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
        }).start();
    }

    public interface HttpCallbackListener {
        void onSuccess(String value);
        void onError(String value);
    }

    //    ------------openWeatherMap------------------------------------
    // 获取url地址
    public static String getOpenWeatherMapUrl(String lat, String lng) {
        return new StringBuilder().append("http://api.openweathermap.org/data/2.5/weather?lat=").append(lat).append("&lon=").append(lng).append("&APPID=650ea91248b8559f22af9d981b0da207&units=metric").toString();
    }

    /*
     {"status":"1","info":"OK","infocode":"10000","count":"1","suggestion":{"keywords":[],"cities":[]},"districts":[{"citycode":"010","adcode":"110000","name":"北京市","center":"116.405285,39.904989","level":"province","districts":[{"citycode":"010","adcode":"110100","name":"北京市市辖区","center":"116.405285,39.904989","level":"city","districts":[]}]}]}
    */
    public static HashMap<String, Object> parseJsonRealTimeWeatherDataFromOpenWeather(String jsonStr) {
        HashMap<String, Object> weatherInfo=null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            weatherInfo = new HashMap<String, Object>();

            if (jsonObject.getInt("cod") == 200 ) {
                JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
                weatherInfo.put("iconid", weather.getString("icon"));
                weatherInfo.put("startTs", (long)jsonObject.getInt("dt"));
                weatherInfo.put("temp", jsonObject.getJSONObject("main").getDouble("temp"));
                weatherInfo.put("humi", jsonObject.getJSONObject("main").getDouble("humidity"));
                weatherInfo.put("sunrise", (long)jsonObject.getJSONObject("sys").getInt("sunrise"));
                weatherInfo.put("sunset", (long)jsonObject.getJSONObject("sys").getInt("sunset"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return weatherInfo;
        }
    }

    public static String getTypeAheadUrl(String keywords) {
        return new StringBuilder().append("http://restapi.amap.com/v3/assistant/inputtips?keywords=").append(keywords).append("&key=ac232cf3d39df0fde9d6eba60859a9dd").toString();
    }

    public static void parseJsonLocationData(ArrayList<dbLocation> locations, String jsonStr, ArrayList<String> listitems) {
        Log.d(TAG, "parseJsonLocationData: " + jsonStr);
        try {
            JSONObject resultJson = new JSONObject(jsonStr);

            if (resultJson.getInt("status") == 1) {
                for(int i=0; i<resultJson.getInt("count"); i++) {
                    JSONObject loc_item = resultJson.getJSONArray("tips").getJSONObject(i);
                    if (loc_item.getString("location").indexOf(',') < 0) {
                        Log.d(TAG, "parseJsonLocationData: invalid data " + loc_item.getString("location"));
                        continue;
                    }
                    Log.d(TAG, "parseJsonLocationData: [name] " + loc_item.getString("name") + "\n" + loc_item.getString("district"));
                    Log.d(TAG, "parseJsonLocationData: [latlng] " + loc_item.getString("location"));

                    String district = loc_item.getString("district") == "[]" ? "" : loc_item.getString("district");
                    String locStr = loc_item.getString("location");
                    Pattern pattern = Pattern.compile("[, |]+");
                    String[] strs = pattern.split(locStr);

                    String address = loc_item.getString("address");
                    locations.add(new dbLocation(Double.parseDouble(strs[1]), Double.parseDouble(strs[0]), loc_item.getString("name")));
                    listitems.add(new String(loc_item.getString("name") + "( " + district + " )"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //逆地理编码
    public static String getReverseAddress(String latlng) {
        return new StringBuilder().append("http://restapi.amap.com/v3/geocode/regeo?&location=").append(latlng).append("key=ac232cf3d39df0fde9d6eba60859a9dd")
                .append("&radius=500").toString();
    }

    public static void getLocationCityName(double lat, double lng) {
        String latlng = new StringBuilder().append(Double.toString(lat)).append(",").append(Double.toString(lng)).toString();


    }
}
