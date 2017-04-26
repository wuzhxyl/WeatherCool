package model;

import com.example.hlkhjk_ok.weathercool.R;

/**
 * Created by hlkhjk_ok on 17/4/14.
 */

public class WeatherInfo {
    private double temp;
    private double humi;
    private float pm;
    private long serverTs;
    private double intensity;
    private String cityname;
    private String image;

    public WeatherInfo() {}

    public WeatherInfo(double temp, double humi, float pm) {
        this.temp = temp;
        this.humi = humi;
        this.pm   = pm;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Temp: ").append(temp).append("Humi: ").append(humi)
                .append("cityname: ").append(cityname).append("PM: ").append(pm)
                .append("dispcontent").append("image: ").append(image).append("intesity: ").append(intensity).toString();
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHumi() {
        return humi;
    }

    public void setHumi(double humi) {
        this.humi = humi;
    }

    public float getPm() {
        return pm;
    }

    public void setPm(float pm) {
        this.pm = pm;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setServerTs(long serverTs) {
        this.serverTs = serverTs;
    }

    public long getServerTs() {
        return serverTs;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public static int getWeatherIcon(String skycon) {
        if (skycon.equals("CLEAR_DAY"))
            return R.drawable.weather_clear_day;
        else if (skycon.equals("CLEAR_NIGHT"))
            return R.drawable.weather_clear_night;
        else if (skycon.equals("PARTLY_CLOUDY_DAY"))
            return R.drawable.weather_cloudy_day;
        else if (skycon.equals("PARTLY_CLOUDY_NIGHT"))
            return R.drawable.weather_cloudy_night;
        else if (skycon.equals("CLOUDY"))
            return R.drawable.weather_overcast;
        else if (skycon.equals("RAIN"))
            return R.drawable.weather_rain;
        else if (skycon.equals("SNOW"))
            return R.drawable.weather_snow;
        else if (skycon.equals("WIND"))
            return R.drawable.weather_wind;
        else if (skycon.equals("FOG"))
            return R.drawable.weather_fog;

        return R.drawable.weather_clear_day;
    }
}
