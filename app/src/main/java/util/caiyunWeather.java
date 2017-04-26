package util;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import model.WeatherInfo;

/**
 * Created by hlkhj on 2017/4/24 0024.
 */

public class caiyunWeather {
    public static String getCaiYunUrl(String lat, String lng) {
        return new StringBuilder().append("https://api.caiyunapp.com/v2/5Jn=rqANZl-i590W/").append(lng).append(",")
                .append(lat).append("/realtime.json").toString();
    }

    public static void requestWeather(final String lat, String lng, final HttpUtil.HttpCallbackListener listener) {
        final String url = getCaiYunUrl(lat, lng);

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized ("") {
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

    public static WeatherInfo parseJsonObjectWeather(String jsonStr) {
        JSONObject object = null;
        WeatherInfo result = null;
        try {
            object = new JSONObject(jsonStr);
            result = new WeatherInfo();

            if (object.getString("status").equals("ok")) {
                JSONObject info = object.getJSONObject("result");
                result.setServerTs(object.getLong("server_time"));
                result.setPm(info.getLong("pm25"));
                result.setHumi(info.getDouble("humidity"));
                result.setTemp(info.getDouble("temperature"));
                result.setImage(info.getString("skycon"));
                //// 降水强度  //未来一小时每分钟的降雨量，0.03-0.25是小雨，0.25-0.35是中雨, 0.35以上是大雨，根据不同地区情况可以有所调整.
                result.setIntensity(info.getJSONObject("precipitation").getJSONObject("local").getDouble("intensity"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
